(ns sketch
  (:require [quil.core :as q]
            [genartlib.util :as u]
            [sketch.config :as cfg]))

(defn setup []
  (q/no-loop)
  (u/set-color-mode))

(defn draw []
  (println {:width cfg/width
            :height cfg/height
            :start-x cfg/start-x
            :start-y cfg/start-y
            :scale cfg/scale})
  (q/push-matrix)
  (q/scale cfg/scale cfg/scale)
  (q/with-translation [cfg/start-x cfg/start-y]
  ;  start here
    )
  (q/pop-matrix))
