;; TODO
;; Communities detection algorithms
;;  - Non-overlapping
;;     - Classic louvain, General louvain (other quality functions)
;;     - Stochastic Block Models
;;     - Hierarchical Random Graphs
;;  - Overlapping
;;     - Clique percolation
;;     - Link Clustering

(ns samudaya.core
  (:require [loom.graph :refer [remove-edges]]
            [loom.alg :as alg]
            [samudaya.data :refer [barbell3 grp3 create-graph]]
            [samudaya.metrics.centrality :refer [edge-betweenness-centrality]]
            [samudaya.utils.max-modularity :refer [init-partition]]))


;; Centrality-based algorithms
(defn max-edge-betweenness [g]
  (key (apply max-key val (edge-betweenness-centrality g))))

(defn girvan-newman
  "Finds communities in a graph using the Girvan–Newman method."
  [g]
  (loop [graph g
         orig-components (alg/connected-components g)
         new-components orig-components]
    (if (or (empty? (alg/distinct-edges graph)) (> (count new-components) (count orig-components)))
      new-components
      (recur (remove-edges graph (max-edge-betweenness graph)) new-components (alg/connected-components graph)))))


;; Modularity-based algorithms
(defn louvain
  "Finds communities in a graph using the Louvain algorithm"
  [g]
  (init-partition g))
