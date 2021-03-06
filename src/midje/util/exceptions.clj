;; -*- indent-tabs-mode: nil -*-

(ns midje.util.exceptions
  (:use [clojure.contrib.str-utils :only [str-join]]))


;;; Reading

(defn stacktrace-as-strings [ex]
  (map (fn [elt] (.toString elt)) (.getStackTrace ex)))

(defn remove-matches [re strings] (remove #(re-find re %) strings))

(defn without-clojure-strings [all-strings]
  (->> all-strings 
       (remove-matches #"^java\.")
       (remove-matches #"^clojure\.")
       (remove-matches #"^sun\.")
       (remove-matches #"^swank\.")
       (remove-matches #"^user\$eval")))
  
(defn without-midje-or-clojure-strings [all-strings]
  (remove-matches #"^midje" (without-clojure-strings all-strings)))

(defn user-error-exception-lines [ex]
  (cons (.toString ex)
        (without-clojure-strings (stacktrace-as-strings ex))))

(defn friendly-exception-lines [ex prefix]
  (cons (.toString ex)
	(map #(str prefix %)
	     (without-midje-or-clojure-strings (stacktrace-as-strings ex)))))

(defn friendly-exception-text [ex prefix]
  (str-join (System/getProperty "line.separator") (friendly-exception-lines ex prefix)))


;;; Writing

(def line-separator (System/getProperty "line.separator"))

(defn user-error [& lines]
  (Error. (apply str (interpose line-separator lines))))
