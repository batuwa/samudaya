(ns samudaya.core
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [loom.graph :refer (graph
                                digraph)]
            [loom.alg :as alg]
            [loom.io :as lio]
            [samudaya.algo.centrality :refer :all]))


(defn line->edge-wisemd
  "read graph in wisemd format"
  [line]
  (let [[src x1 rel origin dest x2] (str/split line #"[,]")]
    {:src src :dest dest :rel (str/replace rel #"[\"]" "")}))

(defn load-edges-wisemd
  "read the graph file"
  [file]
  (->> (io/reader file)
       (line-seq)
       (map line->edge-wisemd)
       (filter (comp #{"PAR" "CHD" "RB" "RO"} :rel))))

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
  "create ubergraph from input"
  []
  (->> ;(load-edges-wisemd "resources/wisemd/diseases.csv")
       (load-edges "resources/karate/karate.txt")
       (map #(into [] (vals (select-keys % [:src :dest]))))
       (apply graph)))


(defn max-edge-betweenness [g]
  (key (apply max-key val (edge-betweenness-centrality))))

(defn max-betweenness [g]
  (key (apply max-key val (betweenness-centrality))))
