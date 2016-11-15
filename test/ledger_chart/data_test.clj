(ns ledger-chart.data-test
  (:require [ledger-chart.data :refer :all]
            [clojure.test :refer :all]))


(deftest walk-structure-test
  (testing "Walking a structure like result of parsing ledger xml"
    (let [data [{:tag :account
                 :content [{}
                           ""
                           {:tag :account
                            :content [{}]}]}
                {}
                {:tag :notbelonging
                 :content [{:tag :account
                            :content "should not get here"}]}
                {:tag :account
                 :content [{:tag :account
                            :content [{:tag :account
                                       :content []}]}
                           nil
                           "teststring"
                           {:tag :account
                            :content []}]}]
          result [{:content [{:content []}]}
                {:content [{:content [{:content []}]}
                           {:content []}]}]]
      (is (= result
             (walk-structure
              (fn [elem]
                (when (and (map? elem)
                           (= :account (elem :tag)))
                  {:result {:content (elem :content)}
                   :recur-on :content}
                  ))
              data))))))
