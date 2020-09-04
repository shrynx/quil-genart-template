(ns sketch.dev
  {:clj-kondo/config '{:lint-as {quil.core/defsketch clojure.core/def}}}
  (:require [quil.core :as q]
            [clj-jgit.porcelain :as git]
            [clj-jgit.querying :as gitq]
            [sketch :as sketch]
            [sketch.config :refer [window-width window-height draw-count random-seed-gen noise-seed-gen draw-svg?]]))

(def ^{:private true} git-repo (try
                                 (git/load-repo ".")
                                 (catch Throwable t
                                   (git/git-init)
                                   (git/load-repo "."))))

(defn- draw-commit-save []
  (let [curr-time   (System/currentTimeMillis)
        time-stamp  (.toString (java.util.Date.))
        commit-hash (subs ((gitq/commit-info git-repo
                                             (let [commit-message (str time-stamp)]
                                               (git/git-add git-repo ".")
                                               (git/git-commit git-repo commit-message))) :id) 0 8)]
    (doseq [img-num (range draw-count)]
      (let [random-seed (random-seed-gen)
            noise-seed (noise-seed-gen)
            img-filename (str "artworks/" curr-time "-" time-stamp "-" commit-hash "/img-" img-num "-{ seed: " random-seed " }-{ noise-seed: " noise-seed " }." (if draw-svg? "svg" "png"))]
        (println "setting seed to:" random-seed "and noise seed to:" noise-seed)
        (q/random-seed random-seed)
        (q/noise-seed noise-seed)
        (try
          (if draw-svg?
            (do
              (q/do-record (q/create-graphics window-width window-height :svg img-filename)
                           (sketch/draw))
              (println "gen time:" (/ (- (System/currentTimeMillis) curr-time) 1000.0) "s")
              (println "done saving" img-filename))
            (do
              (sketch/draw)
              (q/save img-filename)
              (println "gen time:" (/ (- (System/currentTimeMillis) curr-time) 1000.0) "s")
              (println "done saving" img-filename)))
          (catch Throwable t
            (println "Exception in draw function:" t)))))))

(q/defsketch artwork
  :title "sketch"
  :setup sketch/setup
  :draw draw-commit-save
  :size [window-width window-height]
  :settings #(do
              (q/pixel-density 2)
              (q/smooth 8)))

(defn refresh []
  (use :reload-all 'sketch)
  (.redraw artwork))