(ns ledger-chart-front.data
  (:require [reagent.core :as r :refer [atom]]))

(def constants
  {:app-title "Ledger Chart"})

(def initial-state
  {})

(defonce state (atom initial-state))
