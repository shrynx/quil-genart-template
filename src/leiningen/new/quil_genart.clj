(ns leiningen.new.quil-genart
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]))

(def render (renderer "quil-genart"))

(defn quil-genart
  [name]
  (let [data {:name      name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh quil-genart project.")
    (->files data
             [".gitignore" (render "gitignore" data)]
             ["README.md" (render "README.md" data)]
             ["project.clj" (render "project.clj" data)]
             ["src/sketch/config.clj" (render "config.clj" data)]
             ["src/sketch/dev.clj" (render "dev.clj" data)]
             ["src/sketch/run.clj" (render "run.clj" data)]
             ["src/config.edn" (render "config.edn" data)]
             ["src/sketch.clj" (render "sketch.clj" data)])))
