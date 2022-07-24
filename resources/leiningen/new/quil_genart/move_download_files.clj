#!/usr/bin/env bb
(ns move-download-files
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]))

(def project-name "{{name}}")

(defn find-files []
  (fs/glob (str (fs/home) "/" "Downloads") (str project-name "**{.png}")))

(defn get-raw-filename [f]
  (->> (str project-name "_")
       re-pattern
       (str/split f)
       second))

(defn replace-_ [f]
  (str/replace f #"_" ":"))

(defn convert-filename [filename]
  (let [[initial-part-raw image-name-raw] (str/split filename (re-pattern "_img"))
        directory (replace-_ initial-part-raw)
        filename (replace-_ (str "img" image-name-raw))]
    {:directory directory
     :filename filename}))

(doseq [f (find-files)
        :let [{:keys [filename directory]} (->> f
                                                str
                                                get-raw-filename
                                                convert-filename)
              dest-dir (fs/create-dirs (str "." "/" "artworks" "/" directory "-" "browser"))
              dest-file (fs/file dest-dir filename)]]
  (fs/move f dest-file))
