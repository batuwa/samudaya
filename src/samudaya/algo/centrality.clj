(ns samudaya.algo.centrality
  (:require [loom.graph :refer (nodes edges successors)]
            [loom.alg-generic :as gen]
            [loom.alg :as alg]))

;; Based on Newman-Girvan (2004) Shortest-path edge betweenness method
;; This is based on the "Algorithm 1" and "Algorithms 7" from Brandes (2008)


(defn ss-shortest-path
  "Returns a list of maps with metrics like predecessors, no. of shortest paths,
  list of nodes by distance"
  [g start]
  (let [nodeset (disj (nodes g) start)
        pred (apply assoc {} (interleave (nodes g) (repeat nil)))
        dist (apply assoc {start 0} (interleave nodeset (repeat -1)))
        sigma (apply assoc {start 1} (interleave nodeset (repeat 0)))
        stack []]
    (loop [queue (conj clojure.lang.PersistentQueue/EMPTY start)]
      (if (empty? queue)
        {:sigma sigma
         :pred pred
         :stack stack}
        (let [v (peek queue)
              stack (conj stack v)]
          (doseq [w (successors g v)]
            (when (= (dist w) -1)
              (do
                (conj queue w)
                (assoc dist w (+ 1 (dist v)))))
            (when (= (dist w) (+ 1 (dist v)))
                  (do
                    (assoc sigma w (+ (sigma w) (sigma v)))
                    (assoc pred w v))))
            (recur (pop queue)))))))

(defn- accumulate
  [state]
  state) 

(defn- finalize-betweenness
  [betweenness])

(defn betweenness-centrality
  [g]
  (let [nodeset (nodes g)
        betweenness (apply assoc {} (interleave nodeset (repeat 0.0)))]
    (->> nodeset
         (pmap #(vector % (ss-shortest-path g %)))
         (reduce accumulate betweenness)
         ;(finalize-betweenness)
    )))
