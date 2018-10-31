(ns samudaya.algo.centrality
  (:require [loom.graph :refer (nodes edges successors)]
            [loom.alg :as alg]))

;; Based on Newman-Girvan (2004) Shortest-path edge betweenness method
;; This is based on the "Algorithm 1" and "Algorithms 7" from Brandes (2008)

(defn initialize-state
  "Represents the initial algorithmic state as an immutable map"
  [g start]
  (let [nodeset (disj (nodes g) start)]
    {:graph g
     :nodeset nodeset
     :predecessors (apply assoc {} (interleave (nodes g) (repeat [])))
     :distance (apply assoc {start 0} (interleave nodeset (repeat -1)))
     :path-count (apply assoc {start 1} (interleave nodeset (repeat 0)))
     :stack []
     :queue (conj clojure.lang.PersistentQueue/EMPTY start)
     :current-node nil}))

(defn next-node [state]
  (let [v (peek (:queue state))]
    (-> state
        (update :stack conj v)
        (assoc :current-node v))))

(defn process-successor [state w]
  (let [current-node (:current-node state)
        dist-w ((:distance state) w)
        dist-v ((:distance state) current-node)
        sigma-w ((:path-count state) w)
        sigma-v ((:path-count state) current-node)]
    (cond
      (= dist-w -1) (-> state
                        (update :queue conj w)
                        (update-in [:distance] assoc w (inc dist-v))
                        (update-in [:path-count] assoc w (+ sigma-w sigma-v))
                        (update-in [:predecessors] update w conj current-node))
      (= dist-w (inc dist-v)) (-> state
                                  (update-in [:path-count] assoc w (+ sigma-w sigma-v))
                                  (update-in [:predecessors] update w conj current-node))
      :else state)))

(defn process-successors [state]
  (reduce
   process-successor
   state
   (successors (:graph state) (:current-node state))))

(defn pop-queue [state]
  (update state :queue pop))

(def next-state (comp pop-queue process-successors next-node))

(defn ss-shortest-path
  "Returns a list of maps with metrics like predecessors, no. of shortest paths,
  list of nodes by distance"
  [g start]
  (loop [state (initialize-state g start)]
    (if (empty? (:queue state))
      state
      (recur (next-state state)))))

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
