(ns samudaya.utils.max-modularity
  (:require [loom.graph :refer [nodes]]
            [loom.alg :as alg]))


(defn init-partition [g]
  (reduce
    (fn [vec v] (conj vec [v]))
    []
    (nodes g)))
