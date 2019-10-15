(ns leiningen.new.quil-genart
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "quil-genart"))

(defn quil-genart
  "FIXME: write documentation"
  [name]
  (let [data {:name      name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh 'lein new' quil-genart project.")
    (->files data
             [".gitignore" (render "gitignore" data)]
             ["README.md" (render "README.md" data)]
             ["project.clj" (render "project.clj" data)]
             ["src/sketch/config.clj" (render "config.clj" data)]
             ["src/sketch/core.clj" (render "core.clj" data)]
             ["src/sketch/dynamic.clj" (render "dynamic.clj" data)])))
