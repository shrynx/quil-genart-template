(ns sketch.modes.internal
  (:require [quil.core :as q :include-macros true]
            [sketch :as sketch]
            [sketch.config :as cfg]
            [sketch.utils.common :as c]
            [tick.core :as t]
            #?@(:clj
                [[clj-jgit.porcelain :as git]
                 [clj-jgit.querying :as gitq]
                 [clojure.java.shell :as shell]])
            #?@(:cljs
                [[tick.locale-en-us]])))

#?(:clj
   (defonce ^:private git-repo (try
                                 (git/load-repo ".")
                                 (catch Exception _
                                   (git/git-init)
                                   (git/load-repo ".")))))
#?(:clj
   (defn git-commit-hash [message]
     (git/git-add git-repo ".")
     (let [files-changed? (->> git-repo
                               git/git-status
                               vals
                               (every? empty?)
                               not)
           commit (if files-changed?
                    (git/git-commit git-repo (str message))
                    (->> git-repo
                         git/git-log
                         first
                         :id))
           commit-hash (-> git-repo
                           (gitq/commit-info commit)
                           :id
                           (subs 0 8))]
       commit-hash)))


(def renderer
  #?(:clj (if (c/in? [:svg :pdf] cfg/renderer)
            :java2d
            cfg/renderer)
     :cljs :p2d))

(defn draw-save []
  #?(:clj
     (let [time-stamp  (t/format (t/formatter "yyyy-mm-dd hh:mm:ss") (t/zoned-date-time))
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
                   (shell/sh "svgo" img-filename)
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
     (let [time-stamp  (t/format (t/formatter "yyyy-mm-dd hh:mm:ss") (t/zoned-date-time))
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