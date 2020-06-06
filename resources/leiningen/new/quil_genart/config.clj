(ns sketch.config)

(def ^{:private true} config {:width 800
                              :height 1200
                              :scale 8
                              :margin-percentage 15
                              :draw-count 1
                              :draw-svg? false})

(defn- adjusted-dimension [dimension]
  (* dimension (- 1 (/ (config :margin-percentage) 100))))

(defn- adjusted-pos [dimension]
  (/ (* dimension (config :margin-percentage)) 200))

(def scale (config :scale))
(def base-width (config :width))
(def base-height (config :height))
(def window-width (* scale base-width))
(def window-height (* scale base-height))
(def start-x (adjusted-pos base-width))
(def start-y (adjusted-pos base-height))
(def width (adjusted-dimension base-width))
(def height (adjusted-dimension base-height))
(def end-x (+ start-x width))
(def end-y (+ start-y height))
(def draw-count (config :draw-count))
(def draw-svg? (config :draw-svg?))
(defn random-seed-gen [] (rand-int 100000))
(defn noise-seed-gen [] (rand-int 100000))