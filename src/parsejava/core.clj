(ns parsejava.core
  (use [clojure.walk]
       [parsejava.utils]))

(defn- java-ast [java]
  (japa.parser.JavaParser/parse
   (java.io.ByteArrayInputStream. (.getBytes java))))

(declare visit)

(defn parse [java]
  "Convert java string to ast"
  (visit (java-ast java)))

(defn parse-file [java-file]
  (parse (slurp java-file)))

(defmulti visit (fn [unit] (class unit)))

(defmethod visit :default [unit])
   
(defonce ast-package "japa/parser/ast")

(defmacro defvisit [cls]
  (let [unit (gensym "u")
        fields (declared-fields cls)]
    `(defmethod visit ~cls [~unit]
       (list (keyword ~(.getSimpleName cls))
             ~@(map (fn [f] `(list ~(keyword (.getName f))
                                   (~(getter-name f) ~unit)))
                    (filter property-field? fields))
             ~@(map (fn [f] `(list ~(keyword (.getName f))
                                   (visit (~(getter-name f) ~unit))))
                    (filter (partial package-field? ast-package) fields))
             ~@(map (fn [f] `(list ~(keyword (.getName f))
                                   (map #(visit %) (~(getter-name f) ~unit))))
                    (filter list-field? fields))
             ))))

;; (defmethod visit :default [unit] nil)

(doseq [cls (all-classes "japa/parser/ast/")]
  (eval `(defvisit ~cls)))

;; (defn macroprint [m] (clojure.pprint/pprint (macroexpand-all m)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (println (all-classes ast-package)))
