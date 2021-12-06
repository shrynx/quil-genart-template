(ns sketch.modes.browser
  {:clj-kondo/config '{:lint-as {quil.core/defsketch clojure.core/def}}}
  (:require [quil.core :as q :include-macros true]
            [sketch :as sketch]
            [sketch.modes.internal :as i]
            [sketch.config :as cfg]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [cljs.reader :as reader]))

(defn fetch-config []
  (-> (.fetch js/window "/get-{{name}}-config")
      (.then #(.text %))
      (.then reader/read-string)))

(defn run-sketch []
  (q/defsketch artwork
    :host "sketch-container"
    :title "{{name}}"
    :setup sketch/setup
    :draw i/draw-save
    :size [cfg/base-width cfg/base-height]
    :renderer i/renderer
    :settings i/optimise-display))

(defn ^:export run []
  (go
    (let [config (try (<p! (fetch-config))
                      (catch js/Exception _ (cfg/get-browser-config)))]
      (if (empty? config)
        (js/alert "Can't find sketch config")
        (do
          (cfg/set-browser-config config)
          (if (= cfg/config config)
            (run-sketch)
            (.reload js/window.location true)))))))
