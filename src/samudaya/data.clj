(ns samudaya.data
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [loom.graph :refer [graph digraph]]))


(defn line->edge
  "read graph in edge list format"
  [line]
  (let [[src dest] (str/split line #" ")]
    {:src src :dest dest}))

(defn load-edges
  "read the graph file"
  [file]
  (->> (io/reader file)
       (line-seq)
       (map line->edge)))

(defn create-graph
  "create loom graph from input"
  [file]
  (->> (load-edges file)
       (map #(into [] (vals (select-keys % [:src :dest]))))
       (apply graph)))

(def barbell3 (graph [0 1] [0 2] [1 2] [2 3] [3 4] [3 5] [4 5]))

(def grp3 (graph [0 1] [0 2] [0 3] [1 3] [1 6] [2 3] [2 11] [4 5] [4 6] [5 6] [5 7]
                 [7 8] [7 9] [8 9] [8 10] [8 11] [9 10] [9 11] [10 11]))

(def karate-club (create-graph "resources/karate/karate.txt"))
