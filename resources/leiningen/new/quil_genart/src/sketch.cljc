(ns sketch
  (:require [quil.core :as q :include-macros true]
            [sketch.utils.color :as col]
            [sketch.config :as cfg]))

(defn setup []
  (q/no-loop)
  (col/set-hsv-mode))

(defn draw []
  ;  start here
  )
