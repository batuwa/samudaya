(ns samudaya.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [loom.graph :refer [graph digraph
                                weighted-graph weighted-digraph]]))


;; Functions for loading graph data
(defn line->edge
  "read graph in edge list format"
  [delim line]
  (let [[src dest] (str/split line delim)]
    {:src src :dest dest}))

(defn line->edge-wt
  "read graph in edge list format"
  [delim line]
  (let [[src dest wt] (str/split line delim)]
    {:src src :dest dest :wt wt}))

(defn load-edges
  "read the graph file"
  [file delim weight]
  (if (= weight 1)
    (->> (io/reader file)
         (line-seq)
         (map (partial line->edge-wt delim)))
    (->> (io/reader file)
         (line-seq)
         (map (partial line->edge delim)))))

(defn create-graph
  "create loom graph from input"
  [file & {:keys [delim weight header] :or {delim #" " weight 0 header 0}}]
  (let [loaded-edges (load-edges file delim weight)]
    (if (= weight 1)
      (->> (if (= header 1) (rest loaded-edges) loaded-edges)
           (map #(into [] (vals (select-keys % [:src :dest :wt]))))
           (apply weighted-graph))
      (->> (if (= header 1) (rest loaded-edges) loaded-edges)
           (map #(into [] (vals (select-keys % [:src :dest]))))
           (apply graph)))))


;; Define example graphs
(def barbell3 (graph [0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]))

(def grp3 (graph [0 1] [0 2] [0 3] [1 3] [1 6] [2 3] [2 11] [4 5] [4 6] [5 6] [5 7]
                 [7 8] [7 9] [8 9] [8 10] [8 11] [9 10] [9 11] [10 11]))
