(ns sketch.core
  (:require [quil.core :as q]
            [clj-jgit.porcelain :as git]
            [clj-jgit.querying :as gitq]
            [sketch.dynamic :as dynamic]
            [sketch.config :refer [main-width main-height draw-count]])
  (:gen-class))

(def ^{:private true} git-repo (try
                                 (git/load-repo ".")
                                 (catch Throwable t
                                   (git/git-init)
                                   (git/load-repo "."))))

(defn- draw-commit-save []
  (let [time-stamp  (quot (System/currentTimeMillis) 1000)
        commit-hash (subs ((gitq/commit-info git-repo
                                             (let [commit-message (str time-stamp)]
                                               (git/git-add git-repo ".")
                                               (git/git-commit git-repo commit-message))) :id) 0 8)]
    (doseq [img-num (range draw-count)]
      (let [cur-time   (System/currentTimeMillis)
            seed       (System/nanoTime)
            noise-seed (* (rand) 10000)]
        (println "setting seed to:" seed "and noise seed to:" noise-seed)
        (q/random-seed seed)
        (q/noise-seed noise-seed)
        (try
          (dynamic/draw)
          (catch Throwable t
            (println "Exception in draw function:" t)))
        (println "gen time:" (/ (- (System/currentTimeMillis) cur-time) 1000.0) "s")
        (let [img-filename (str "artworks/" time-stamp "-" commit-hash "/img-" img-num "-{ seed: " seed "}-{ noise-seed: " noise-seed "}.png")]
          (q/save img-filename)
          (println "done saving" img-filename))))))

(q/defsketch artwork
  :title "sketch"
  :setup dynamic/setup
  :draw draw-commit-save
  :size [main-width main-height])

(defn refresh []
  (use :reload 'sketch.dynamic)
  (.redraw artwork))

(defn get-applet []
  artwork)