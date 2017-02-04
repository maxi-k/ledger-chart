(ns ledger-chart.serve-test
  (:require [ledger-chart.serve :refer :all]
            [ledger-chart.common-test :as tc]
            [clojure.test :refer :all ]
            [clojure.data.json :as json]
            [ring.mock.request :as mock]))

(defn request [resource method & params]
  (service-routes {:request-method method
                   :uri resource
                   :params (first params)}))

(deftest data-handler-test
  (tc/set-test-data)
  (is (= (-> (request "/data" :post
                      {:content-type "application/json"
                       :lenses ["Assets:Cash" "Liabilities:Credit"]})
             :body
             (json/read-str :key-fn keyword))
         [{:name "Cash"
           :fullname "Assets:Cash"
           :amount 500
           :accounts []}
          {:name "Credit"
           :fullname "Liabilities:Credit"
           :amount 1000
           :accounts []}])))
