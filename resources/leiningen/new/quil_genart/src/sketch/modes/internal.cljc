(ns sketch.modes.internal
  (:require [quil.core :as q :include-macros true]
            [sketch :as sketch]
            [sketch.config :as cfg]
            [sketch.utils.common :as c]
            [tick.core :as t]
            #?@(:clj
                [[clojure.java.shell :refer [sh]]])
            #?@(:cljs
                [[tick.locale-en-us]])))

#?(:clj
   (defn git-init []
     (let [{:keys [exit]} (sh "git" "rev-parse" "--is-inside-work-tree")]
       (when (not= exit 0)
         (sh "git" "init")))))

#?(:clj
   (defn git-commit-hash [message]
     ;; Ensure repo is initialized
     (git-init)
     ;; Stage all changes
     (sh "git" "add" ".")
     ;; Check for any changes to commit
     (let [{:keys [out]} (sh "git" "status" "--porcelain")
           files-changed? (not (clojure.string/blank? out))]
       (if files-changed?
         (do
           ;; Commit changes
           (sh "git" "commit" "-m" message))
         ;; No changes, do nothing
         nil))
     ;; Get latest commit hash
     (let [{:keys [out]} (sh "git" "rev-parse" "--short" "HEAD")]
       (clojure.string/trim out))))

(def renderer
  #?(:clj (if (c/in? [:svg :pdf] cfg/renderer)
            :java2d
            cfg/renderer)
     :cljs :p2d))

(defn draw-save []
  #?(:clj
     (let [time-stamp  (t/format (t/inst))
           commit-hash (git-commit-hash time-stamp)]
       (doseq [img-num (range cfg/draw-count)]
         (let [start-time   (t/inst)
               random-seed (cfg/random-seed-gen)
               noise-seed (cfg/noise-seed-gen)
               img-filename (str "artworks/" time-stamp "-" commit-hash "/img-" img-num "-{ seed: " random-seed " }-{ noise-seed: " noise-seed " }." (if (= cfg/renderer :svg) "svg" "png"))]
           (println "setting seed to:" random-seed "and noise seed to:" noise-seed "at" (.format (java.text.SimpleDateFormat. "HH:mm:s dd/MM/yyyy") (new java.util.Date)))
           (q/random-seed random-seed)
           (q/noise-seed noise-seed)
           (try
             (if (= cfg/renderer :svg)
               (do
                 (q/do-record (q/create-graphics cfg/window-width cfg/window-height :svg img-filename)
                              (q/with-translation [cfg/start-x cfg/start-y]
                                (sketch/draw)))
                 (try
                   (println "optimising svg")
                   (sh "svgo" img-filename)
                   (catch Exception _))
                 (println "gen time:" (t/between start-time (t/now)))
                 (println "done saving" img-filename))
               (do
                 (let [gr (q/create-graphics cfg/window-width cfg/window-height cfg/renderer)]
                   (q/with-graphics gr
                     (sketch/setup)
                     (q/scale cfg/scale cfg/scale)
                     (q/with-translation [cfg/start-x cfg/start-y]
                       (sketch/draw))
                     (q/save img-filename))
                   (q/push-matrix)
                   (q/scale (/ 1 cfg/scale) (/ 1 cfg/scale))
                   (q/image gr 0 0)
                   (q/pop-matrix))
                 (println "gen time:" (t/between start-time (t/now)))
                 (println "done saving" img-filename)))
             (catch Exception e
               (println "Exception in draw function:" e))))))
     :cljs
     (let [time-stamp  (t/format (t/inst))
           commit-hash (cfg/get-config-val :commit-hash)]
       (doseq [img-num (range cfg/draw-count)]
         (let [start-time   (t/now)
               random-seed (cfg/random-seed-gen)
               noise-seed (cfg/noise-seed-gen)]
           (println "setting seed to:" random-seed "and noise seed to:" noise-seed)
           (q/random-seed random-seed)
           (q/noise-seed noise-seed)
           (let [gr (q/create-graphics cfg/window-width cfg/window-height renderer)]
             (q/with-graphics gr
               (sketch/setup)
               (q/scale cfg/scale cfg/scale)
               (q/with-translation [cfg/start-x cfg/start-y]
                 (sketch/draw))
               (let [img-filename (str "{{name}}/" time-stamp "-" commit-hash "/img-" img-num "-{ seed: " random-seed " }-{ noise-seed: " noise-seed " }." "png")]
                 (q/save img-filename)
                 (println "gen time:" (t/between start-time (t/now)))
                 (println "done saving" img-filename)))
             (q/push-matrix)
             (q/scale (/ 1 cfg/scale) (/ 1 cfg/scale))
             (q/image gr 0 0)
             (q/pop-matrix)))))))

(defn optimise-display []
  (when (c/not-in? [:svg :pdf] cfg/renderer)
    (q/pixel-density 2)
    (q/smooth 8)))

#?(:clj
   (defn ring-handler [req]
     (if (= "/get-{{name}}-config" (:uri req))
       (let [commit-hash (git-commit-hash (.toString (java.util.Date.)))
             conf (-> (cfg/get-config)
                      (assoc :commit-hash commit-hash)
                      str)]
         {:status 200
          :headers {"Content-Type" "text/plain"}
          :body conf})
       {:status 404
        :headers {"Content-Type" "text/plain"}
        :body nil})))