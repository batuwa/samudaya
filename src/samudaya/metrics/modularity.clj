(ns samudaya.metrics.modularity
  (:require [loom.graph :refer (directed? in-degree out-degree)]
            [loom.alg :as alg]))


(defn- cartesian-product [colls]
  (if (empty? colls)
    '(())
    (for [x (first colls)
          more (cartesian-product (rest colls))]
      (cons x more))))

(defn- create-pairs [communities]
  (reduce
   (fn [ll vec]
     (apply conj ll (cartesian-product (repeat 2 vec))))
   ()
   communities))

(defn- calc-vals [g [u v]]
  (let [m (count (alg/distinct-edges g))
        norm (if (directed? g) (/ 1 m) (/ 1 (* 2 m)))
        out-deg-v (out-degree g v)
        in-deg-u (if (directed? g) (in-degree g u) (out-degree g u))
        scale (if (and (not= u v) (directed? g)) 1 2)
        w (if (some #(= [u v] %) (alg/distinct-edges g)) scale 0)]
    (- w (* in-deg-u out-deg-v norm))))

(defn- create-adjacency [g communities]
  (reduce
   (fn [ll pair]
     (conj ll (calc-vals g pair)))
   ()
   (create-pairs communities)))

(defn modularity [g communities]
  (let [m (count (alg/distinct-edges g))
        norm (if (directed? g) (/ 1 m) (/ 1 (* 2 m)))]
    (float (* norm (reduce + (create-adjacency g communities))))))
