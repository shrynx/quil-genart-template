(ns sketch.config
  (:require
   [clojure.edn :as edn]))

(defn get-config [k] (let [conf (edn/read-string (slurp "src/config.edn"))]
                       (conf k)))

(def base-width (get-config :width))
(def base-height (get-config :height))

(defn- get-margin []
  (let [base-dimension (min base-width base-height)
        margin-percentage (get-config :margin-percentage)]
    (/ (* base-dimension margin-percentage) 100)))

(defn- adjust-dimension [dimension]
  (- dimension (get-margin)))

(defn- get-seed [seed]
  (case seed
    :random (rand-int 100000)
    seed))

(def scale (get-config :scale))
(def window-width (* scale base-width))
(def window-height (* scale base-height))
(def start-x (/ (get-margin) 2))
(def start-y (/ (get-margin) 2))
(def width (adjust-dimension base-width))
(def height (adjust-dimension base-height))
(defn w [factor] (* factor width))
(defn h [factor] (* factor height))
(def end-x (+ start-x width))
(def end-y (+ start-y height))
(def draw-count (get-config :draw-count))
(def draw-svg? (get-config :draw-svg?))
(defn random-seed-gen [] (get-seed (get-config :random-seed)))
(defn noise-seed-gen [] (get-seed (get-config :noise-seed)))
