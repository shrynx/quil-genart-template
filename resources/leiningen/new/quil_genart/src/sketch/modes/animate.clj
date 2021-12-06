(ns sketch.modes.animate
  {:clj-kondo/config '{:lint-as {quil.core/defsketch clojure.core/def}}}
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [sketch :as sketch]
            [sketch.modes.internal :as i]
            [sketch.config :as cfg])
  (:gen-class))

(q/defsketch artwork
  :title "{{name}}"
  :setup sketch/setup
  :draw sketch/draw
  :update sketch/update-state           
  :size [cfg/window-width cfg/window-height]
  :renderer i/renderer
  :middleware [m/fun-mode]
  :settings i/optimise-display)