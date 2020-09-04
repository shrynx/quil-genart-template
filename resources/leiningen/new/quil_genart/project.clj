(defproject {{name}} "1.0.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [quil "3.1.0"]
                 [incanter/incanter-core "1.9.3"]
                 [generateme/fastmath "2.0.3"]
                 [genartlib/genartlib "0.1.20"]
                 [thi.ng/geom "1.0.0-RC4"]
                 [thi.ng/color "1.4.0"]
                 [clj-jgit "1.0.0"]]
  :main sketch.run
  :aot [sketch.run sketch]
  :profiles {:repl {:main sketch.dev
                    :aot [sketch.dev sketch]}
             :uberjar {:main sketch.run
                       :aot [sketch.run sketch]
                       :jvm-opts ["-Xms4096m" "-Xmx6144m" "-server"]}})