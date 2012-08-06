(ns parsejava.utils
  (require [clojure.string :as s]
           [complete.core]))

(defn capitalize [s]
  "Capitalizes only the first character of the string unlike s/capitalize"
  (apply str (s/capitalize (str (first s))) (rest s)))

(defn all-classes [package]
  "Returns all classes in the given pakage (including sub-packages"
  (map #(Class/forName %) (complete.core/completions package)))

(defn- static? [field]
  (java.lang.reflect.Modifier/isStatic (.getModifiers field)))

(defn declared-fields [cls]
  "Get all declared fields in the class (including fields in superclasses)"
  (when cls
    (concat (filter (comp not static?) (.getDeclaredFields cls))
            (declared-fields (.getSuperclass cls)))))

(defn package-field? [package field]
  "Returns true if the field type belongs to the given package (or sub)"
  (let [pkg (-> (.getType field) .getPackage)]
    (when pkg (-> pkg .getName (.startsWith package)))))

(defn list-field? [field]
  "true if field is List type"
  (= java.util.List (.getType field)))

(defn property-field? [field]
  "true if the field is primitive or String (data can be taken directly)"
  (or (-> (.getType field) .isPrimitive)
      (-> (.getType field) (= String))))

(defn spell-check [field name]
  "names that are mis-spelled are corrected and returned"
  (cond (.startsWith name "is") (spell-check field (.substring name 2))
        (.endsWith name "_") (spell-check field (s/replace name "_" ""))
        (= name "pakage") "package"
        (= name "extendsList") "extends"
        (= name "implementsList") "implements"
        (= name "op") "operator"
        (= name "var") "variable"
        (= name "ext") "extends"
        (= name "sup") "super"
        (and (= japa.parser.ast.stmt.ExpressionStmt (.getDeclaringClass field))
             (= name "expr")) "expression"
        :else name))

(defn- boolean-type? [field] (= Boolean/TYPE (.getType field)))

(defn- use-get [field]
  (= (.getName field) "value"))

(defn getter-name [field]
  (symbol (str "."
               (if (or (not (boolean-type? field)) (use-get field)) "get" "is")
               (capitalize (spell-check field (.getName field))))))