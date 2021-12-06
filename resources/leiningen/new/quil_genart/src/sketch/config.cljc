(ns sketch.config
  #?(:cljs
     (:require
      [cljs.reader :as reader]))
  #?(:clj
     (:require
      [clojure.edn :as edn])))

#?(:cljs
   (do
     (def browser-config-key "genart-{{name}}")

     (defn set-browser-config [cfg]
       (->> cfg
            str
            (.setItem (.-localStorage js/window) browser-config-key)))

     (defn get-browser-config []
       (->> browser-config-key
            (.getItem (.-localStorage js/window))
            reader/read-string))))

#?(:clj
   (defn get-config [] (->
                        "config.edn"
                        slurp
                        edn/read-string)))

(defn get-config-val [k]
  #?(:clj ((get-config) k)
     :cljs ((get-browser-config) k)))

(def base-ppi 60)
(def base-ppcm 25)

(def unit (get-config-val :unit))
(defn calculate-pixel-density [dim]
  (case unit
    :in (* base-ppi dim)
    :cm (* base-ppcm dim)
    dim))

(def base-width (calculate-pixel-density (get-config-val :width)))
(def base-height (calculate-pixel-density (get-config-val :height)))

(defn- get-margin []
  (let [base-dimension (min base-width base-height)
        margin-percentage (get-config-val :margin-percentage)]
    (/ (* base-dimension margin-percentage) 100)))

(defn- adjust-dimension [dimension]
  (- dimension (get-margin)))

(defn- get-seed [seed]
  (case seed
    :random (rand-int 100000)
    seed))

(def scale (get-config-val :scale))
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
(def draw-count (get-config-val :draw-count))
(def renderer (get-config-val :renderer))
(defn random-seed-gen [] (get-seed (get-config-val :random-seed)))
(defn noise-seed-gen [] (get-seed (get-config-val :noise-seed)))
(def config #?(:clj (get-config)
               :cljs (get-browser-config)))
