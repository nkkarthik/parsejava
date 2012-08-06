(defproject parsejava "0.0.1"
  :description "Parse java source files in clojure"
  ;; :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.google.code.javaparser/javaparser "1.0.8"]
                 [clojure-complete/clojure-complete "0.2.1"]]
  :main parsejava.core)
