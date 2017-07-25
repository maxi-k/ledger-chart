(ns ledger-chart-front.init
    (:require [ledger-chart-front.core :as core]
              [ledger-chart-front.conf :as conf]))

(enable-console-print!)

(defn start-descjop! []
  (core/init! conf/setting))

(start-descjop!)
