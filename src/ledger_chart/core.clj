(ns ledger-chart.core
  (:require [ledger-chart.serve :as serve]
            [ledger-chart.data :as data])
  (:gen-class))

(defn initialize-data [path]
  (-> path
      (data/fetch-xml)
      (data/parse-xml)
      (data/store-data)))

(defn -main
  "Entrypoint to the program."
  [& args]
  (if (zero? (count args))
    (do
      (println "Too few arguments: [path-to-ledger-file]")
      (System/exit 0))
    (do
      (initialize-data (nth args 0))
      (serve/serve))))
