(ns sketch.config)

(def ^{:private true} config {:width 800
                              :height 800
                              :scale 1
                              :margin-percentage 10
                              :draw-count 1
                              :draw-svg? false})

(defn- adjusted-dimension [dimension]
  (* dimension (- 1 (/ (config :margin-percentage) 100))))

(defn- adjusted-pos [dimension]
  (/ (* dimension (config :margin-percentage)) 200))

(def main-width (apply * (map config [:scale :width])))
(def main-height (apply * (map config [:scale :height])))
(def start-x (adjusted-pos main-width))
(def start-y (adjusted-pos main-height))
(def width (adjusted-dimension main-width))
(def height (adjusted-dimension main-height))
(def end-x (+ start-x width))
(def end-y (+ start-y height))
(def scale (config :scale))
(def draw-count (config :draw-count))
(def draw-svg? (config :draw-svg?))
(defn random-seed-gen [] (rand-int 100000))
(defn noise-seed-gen [] (rand-int 100000))