(ns samudaya.core-test
  (:require [clojure.test :refer :all]
            [samudaya.core :refer :all]
            [samudaya.data :refer [barbell3 grp3 karate-club]]))


(deftest compute-max-edge-betweenness
  (testing "calculate max edge betweenness of a graph"
    (is (= [3 2] (max-edge-betweenness barbell3)))
    (is (= [2 11] (max-edge-betweenness grp3)))
    (is (= ["1" "32"] (max-edge-betweenness karate-club)))))

(deftest compute-girvan-newman
  (testing "create communities of a graph"
    (is (= [[0 1 2]
            [4 3 5]]
           (girvan-newman barbell3)))
    (is (= [[0 1 3 2 6 4 5]
            [7 9 8 11 10]]
           (girvan-newman grp3)))
    (is (= [["9" "3" "34" "33" "31" "28" "10" "29" "30" "21" "19" "15" "27" "24" "32" "16" "23" "25" "26"]
            ["22" "1" "2" "4" "8" "14" "20" "7" "5" "18" "12" "13" "6" "11" "17"]]
           (girvan-newman karate-club)))))

