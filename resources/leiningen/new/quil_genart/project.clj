(defproject {{name}} "1.0.0-SNAPSHOT"
  :dependencies [;; dev utils
                 [org.clojure/clojure "1.10.1"]
                 [quil "4.3.1563"]
                 [tick/tick "0.5.0-RC5"]
                 [org.clojure/core.async "1.3.610"]
                 ;; art utils  
                 [thi.ng/geom "1.0.0-RC4"]
                 [thi.ng/color "1.4.0"]
                 ;; dev repl
                 [nrepl "1.3.1"]
                 [cider/cider-nrepl "0.52.1"]]
  :plugins [[cider/cider-nrepl "0.52.1"]]
  :middleware [cider-nrepl.plugin/middleware]
  :profiles {:draw {:main sketch.modes.draw
                    :aot [sketch.modes.draw sketch]
                    :jvm-opts ["-Xms4096m" "-Xmx6144m" "-server"]}
             :animate {:main sketch.modes.animate
                       :aot [sketch.modes.animate sketch]
                       :jvm-opts ["-Xms4096m" "-Xmx6144m" "-server"]}
             :uberjar {:main sketch.modes.print
                       :aot [sketch.modes.print sketch]
                       :jvm-opts ["-Xms4096m" "-Xmx6144m" "-server"]}
             :browser {:dependencies [[org.clojure/clojurescript "1.10.773"]
                                      [com.bhauman/figwheel-main "0.2.12"]
                                      [com.bhauman/rebel-readline-cljs "0.1.4"]]
                       :source-paths ["src"]
                       :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main" "-b" "sketch" "-r"]
                                 "fig:build"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "sketch"]}
                       :resource-paths ["target"]
                       :clean-targets ^{:protect false} ["target"]}}
  :aliases {"draw" ["with-profile" "draw" "repl"]
            "animate" ["with-profile" "animate" "repl"]
            "build" ["with-profile" "uberjar" "run"]
            "browser" ["with-profile" "browser" "fig"]
            "browser:build" ["with-profile" "browser" "fig:build"]})