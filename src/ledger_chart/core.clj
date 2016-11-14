(ns ledger-chart.core
  (:require [ledger-chart.serve :as serve]
            [ledger-chart.data :as data])
  (:gen-class))

(defn -main
  "Entrypoint to the program."
  [& args]
  (println args)
  (if (zero? (count args))
    (do
      (println "Too few arguments: [path-to-ledger-file]")
      (System/exit 0))
    (do
      (let [xml (-> (nth args 0)
                    (data/fetch-xml)
                    (data/parse-xml)
                    (data/store-data))]
        (print xml))
      (serve/serve)
      )))
