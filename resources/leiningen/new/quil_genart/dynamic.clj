(ns sketch.dynamic
  (:require [quil.applet :refer [current-applet]]
            [quil.core :as q]
            [genartlib.random :as r]
            [genartlib.util :as u]
            [fastmath.fields :as f]
            [fastmath.core :as m]
            [fastmath.vector :as v]
            [sketch.config :as cfg]))

(defn setup []
  (q/smooth)
  (q/no-loop)
  (q/hint :disable-async-saveframe)
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
