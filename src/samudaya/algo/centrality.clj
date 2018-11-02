(ns samudaya.algo.centrality
  (:require [loom.graph :refer (nodes edges successors directed?)]
            [loom.alg :as alg]))

;; Based on Newman-Girvan (2004) Shortest-path edge betweenness method
;; This is based on the "Algorithm 1" and "Algorithms 7" from Brandes (2008)


;; Single source shortest path functions
(defn- initialize-state
  "Represents the initial algorithmic state as an immutable map"
  [g start]
  (let [nodeset (disj (nodes g) start)]
    {:graph g
     :start start
     :nodeset nodeset
     :predecessors (apply assoc {} (interleave (nodes g) (repeat [])))
     :distance (apply assoc {start 0} (interleave nodeset (repeat -1)))
     :path-count (apply assoc {start 1} (interleave nodeset (repeat 0)))
     :stack []
     :queue (conj clojure.lang.PersistentQueue/EMPTY start)
     :current-node nil}))

(defn- next-node [state]
  (let [v (peek (:queue state))]
    (-> state
        (update :stack conj v)
        (assoc :current-node v))))

(defn- process-successor [state w]
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

(defn- process-successors [state]
  (reduce
   process-successor
   state
   (successors (:graph state) (:current-node state))))

(defn- pop-queue [state]
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


;; Accumulate functions
(defn- update-betweenness [cbet state]
  (let [current-node (:current-node state)
        cbet-w (cbet current-node)
        delta-w ((:delta state) current-node)]
    (if (not= (:current-node state) (:start state))
      (-> cbet
          (assoc current-node (+ cbet-w delta-w)))
      cbet)))


(defn- next-acc-node [state]
  (let [w (peek (:stack state))]
    (-> state
        (assoc :current-node w))))

(defn- calculate-dependency [state v]
  (let [current-node (:current-node state)
        delta-w ((:delta state) current-node)
        delta-v ((:delta state) v)
        sigma-w ((:path-count state) current-node)
        sigma-v ((:path-count state) v)
        coeff (/ (inc delta-w) sigma-w)]
    (-> state
        (update-in [:delta] assoc v (+ delta-v (* coeff sigma-v))))))

(defn- calculate-dependencies [state]
    (reduce
     calculate-dependency
     state
     ((:predecessors state) (:current-node state))))

(defn- pop-stack
  "Get nodes from the furthest one from node first"
  [state]
  (update state :stack pop))

(def next-acc-state (comp pop-stack calculate-dependencies next-acc-node))

(defn accumulate
  [betweenness state]
  (loop [state (merge state {:delta (apply assoc {} (interleave (nodes (:graph state)) (repeat 0)))})
         cbet betweenness]
    (if (empty? (:stack state))
      cbet
      (recur (next-acc-state state) (update-betweenness cbet state)))))


;; Rescale betweenness scores
(defn finalize-betweenness
  "Normalize betweenness as it scales with the number of pairs of nodes"
  [directed betweenness]
  (let [n (count betweenness)]
    (cond
      directed (apply merge (map (fn [[k v]] {k (* v (/ 1 (* (- n 1) (- n 2))))}) betweenness))
      :else    (apply merge (map (fn [[k v]] {k (* v (/ 2 (* (- n 1) (- n 2))))}) betweenness)))))


;; Main function
(defn betweenness-centrality
  [g]
  (let [nodeset (nodes g)
        directed (directed? g)
        betweenness (apply assoc {} (interleave nodeset (repeat 0.0)))]
    (->> nodeset
         (pmap #(ss-shortest-path g %))
         (reduce accumulate betweenness)
         (finalize-betweenness directed))))
