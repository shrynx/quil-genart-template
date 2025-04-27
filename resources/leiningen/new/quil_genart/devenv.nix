{ pkgs, lib, ... }:

{
  languages = {
    clojure.enable = true;
    java.jdk.package = pkgs.jdk21; # jdk8 for quil <= 3.x.x
  };

  packages = with pkgs; [
    leiningen
    babashka
  ];

  scripts = {
    draw.exec = "lein draw";
    animate.exec = "lein animate";
    build.exec = "lein build";
    print.exec = "java - jar ./target/{{name}}-1.0.0-SNAPSHOT-standalone.jar";
    browser.exec = "lein browser";
    browser-build.exec = "lein browser:build";
  };
}
