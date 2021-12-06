(ns sketch.modes.print
  {:clj-kondo/config '{:lint-as {quil.core/defsketch clojure.core/def}}}
  (:require [quil.core :as q]
            [sketch :as sketch]
            [sketch.modes.internal :as i]
            [sketch.config :as cfg])
  (:gen-class))

(defn draw-save-exit []
  (i/draw-save)
  (q/exit)
  (System/exit 0))

(defn -main []
  (q/defsketch artwork
    :title "{{name}}"
    :setup sketch/setup
    :draw draw-save-exit
    :size [cfg/base-width cfg/base-height]
    :renderer i/renderer
    :settings i/optimise-display))

