(defproject {{name}} "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [quil "3.1.0"]
                 [incanter/incanter-core "1.9.3"]
                 [generateme/fastmath "1.5.2"]
                 [genartlib/genartlib "0.1.20"]
                 [clj-jgit "0.8.10"]]
  :main  sketch.core
  :aot [sketch.dynamic])