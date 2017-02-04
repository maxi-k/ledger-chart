(ns ledger-chart.common-test
  (:require  [clojure.test :as t]
             [ledger-chart.data :as data]))

(def test-data
  [{:amount nil :name nil :fullname nil
    :accounts [{:name "Assets"
                :fullname "Assets"
                :amount 5000
                :accounts [{:name "Bank"
                            :fullname "Assets:Bank"
                            :amount 4500
                            :accounts []}
                           {:name "Cash"
                            :fullname "Assets:Cash"
                            :amount 500
                            :accounts []}]}
               {:name "Liabilities"
                :fullname "Liabilities"
                :amount 1000
                :accounts [{:name "Credit"
                            :fullname "Liabilities:Credit"
                            :amount 1000
                            :accounts []}]}]}])

(defn set-test-data []
  (data/store-data test-data))
