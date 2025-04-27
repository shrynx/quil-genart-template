{ pkgs, lib, ... }:

{
  languages = {
    clojure.enable = true;
    java.jdk.package = pkgs.jdk21;
  };

  packages = with pkgs; [
    leiningen
  ];
}