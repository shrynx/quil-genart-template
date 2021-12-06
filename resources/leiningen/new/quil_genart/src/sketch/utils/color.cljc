(ns sketch.utils.color
  (:require
   [quil.core :as q]
   [thi.ng.color.core :as col]
   [sketch.utils.random :refer [choice]]))

(defn set-hsv-mode
  "Set the color space to HSB, with the following ranges:
   - Hue: [0, 360)
   - Saturation: [0 100]
   - Brightness: [0 100]
   - Alpha: [0.0, 1.0]"
  []
  (q/color-mode :hsb 360 100 100 1.0))

(defn random-color
  ([palette] (random-color palette nil))
  ([palette ignore] (->> palette
                         (filter (partial not= ignore))
                         (apply choice))))

(defn show-palette [palette]
  (let [height (/ (q/height) (count palette))]
    (q/push-matrix)
    (q/stroke 0 0 100)
    (q/stroke-weight (/ height 20))
    (doseq [[i p] (map-indexed vector palette)
            :let [y (* height i)]]
      (apply q/fill p)
      (q/rect 0 y (q/width) height)
      (q/stroke 0 0 0)
      (q/fill 0 0 100)
      (q/text-size (/ height 5))
      (q/text (str p) (* (q/width) 0.5) (+ y (/ height 2))))
    (q/pop-matrix)))

(defn hsv->rgba
  ([h s v] (hsv->rgba h s v 1))
  ([h s v a]
   (let [[r g b alpha] (->>
                        (col/->HSVA (/ h 360) (/ s 100) (/ v 100) a)
                        col/as-rgba
                        deref)]
     (mapv int [(* 255 r) (* 255 g) (* 255 b) (* alpha 100)]))))

(defn css->hsva [css]
  (let [[h s v a] (->> css col/css col/as-hsva deref)]
    [(int (* 360 h)) (int (* 100 s)) (int (* 100 v)) a]))

(defn set-alpha [color alpha]
  (let [[x y z] color]
    [x y z alpha]))
