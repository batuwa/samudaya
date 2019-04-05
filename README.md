# Samudāya

Clojure-based Community Detection library.

> Noun (english community)
>
> Samudāya (समुदाय) - Sanskrit word meaning **community**, aggregate, totality or collection of individual members


![Samudaya logo](https://raw.githubusercontent.com/batuwa/samudaya/master/doc/samudaya_logo.png "Samudaya")

## Description

This library provides [Community Detection](https://en.wikipedia.org/wiki/Community_structure) algorithms for small to medium sized networks. It is built on the [Loom](https://github.com/aysylu/loom) clojure library for the graph data structure and basic graph algorithms.

So far, the [Girvan-Newman](https://en.wikipedia.org/wiki/Betweenness_centrality) algorithm has been implemented.

## Usage

Load library core and namescape.

```clojure
(load "samudaya/core")
(in-ns 'samudaya.core)
```

Example: Run community detection on the [karate club](https://en.wikipedia.org/wiki/Zachary%27s_karate_club) network.

```clojure
(def karate-club (create-graph "<path-to-edge-list-file>"))
(girvan-newman karate-club)
```

Output

```clojure
[["9" "3" "34" "33" "31" "28" "10" "29" "30" "21" "19" "15" "27" "24" "32" "16" "23" "25" "26"]
 ["22" "1" "2" "4" "8" "14" "20" "7" "5" "18" "12" "13" "6" "11" "17"]]
```

## TODO

- Implement more algorithms like louvain, stochastic block models etc
- Add more tests
- Clean up code and API
- Publish to clojars and maven

## License

Copyright © 2019 Saurav Dhungana

Distributed under the Eclipse Public License.