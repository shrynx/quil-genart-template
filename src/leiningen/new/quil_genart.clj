(ns leiningen.new.quil-genart
  (:require [leiningen.new.templates :refer [renderer name-to-path ->files]]
            [leiningen.core.main :as main]
            [clojure.java.shell :as sh]))

(def render (renderer "quil-genart"))

(defn quil-genart [name]
  (let [data {:name      name
              :sanitized (name-to-path name)}]
    (main/info "Generating fresh quil-genart project.")
    (->files data
             [".gitignore" (render "gitignore" data)]
             [".envrc" (render "envrc" data)]
             ["devenv.lock" (render "devenv.lock" data)]
             ["devenv.nix" (render "devenv.nix" data)]
             ["devenv.yaml" (render "devenv.yaml" data)]
             ["README.md" (render "README.md" data)]
             ["project.clj" (render "project.clj" data)]
             ["config.edn" (render "config.edn" data)]
             ["src/sketch.cljc" (render "src/sketch.cljc" data)]
             ["src/sketch/config.cljc" (render "src/sketch/config.cljc" data)]
             ["src/sketch/modes/internal.cljc" (render "src/sketch/modes/internal.cljc" data)]
             ["src/sketch/modes/draw.clj" (render "src/sketch/modes/draw.clj" data)]
             ["src/sketch/modes/print.clj" (render "src/sketch/modes/print.clj" data)]
             ["src/sketch/modes/animate.clj" (render "src/sketch/modes/animate.clj" data)]
             ["src/sketch/modes/browser.cljs" (render "src/sketch/modes/browser.cljs" data)]
             ["src/sketch/utils/common.cljc" (render "src/sketch/utils/common.cljc" data)]
             ["src/sketch/utils/random.cljc" (render "src/sketch/utils/random.cljc" data)]
             ["src/sketch/utils/algebra.cljc" (render "src/sketch/utils/algebra.cljc" data)]
             ["src/sketch/utils/curves.cljc" (render "src/sketch/utils/curves.cljc" data)]
             ["src/sketch/utils/color.cljc" (render "src/sketch/utils/color.cljc" data)]
             ["src/sketch/utils/quadtree.cljc" (render "src/sketch/utils/quadtree.cljc" data)]
             ["src/sketch/utils/circle_packing.cljc" (render "src/sketch/utils/circle_packing.cljc" data)]
             ["resources/public/index.html" (render "resources/public/index.html" data)]
             ["figwheel-main.edn" (render "figwheel-main.edn" data)]
             ["sketch.cljs.edn" (render "sketch.cljs.edn" data)]
             ["scripts/move_download_files.clj" (render "scripts/move_download_files.clj" data)]))
  (sh/sh "chmod" "+x" (str name "/" "scripts/move_download_files.clj")))
