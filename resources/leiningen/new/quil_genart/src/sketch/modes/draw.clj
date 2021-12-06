(ns sketch.modes.draw
  {:clj-kondo/config '{:lint-as {quil.core/defsketch clojure.core/def}}}
  (:require [quil.core :as q]
            [sketch :as sketch]
            [sketch.modes.internal :as i]
            [sketch.config :as cfg]))

(q/defsketch artwork
  :title "{{name}}"
  :setup sketch/setup
  :draw i/draw-save
  :size [cfg/base-width cfg/base-height]
  :renderer i/renderer
  :settings i/optimise-display)

(defn refresh []
  (use :reload 'sketch)
  (use :reload 'sketch.config)
  (.redraw artwork))