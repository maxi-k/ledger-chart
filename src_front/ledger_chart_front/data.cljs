(ns ledger-chart-front.data
  (:require [reagent.core :as r :refer [atom]]))

(def constants
  {:app-title "Ledger Chart"})

(def ledger-commands
  (mapv (fn [[s h]] {:key s :text h :value s})
        [["bal" "Balance"]
         ["reg" "Register"]]))

(def initial-state
  {:window-title (constants :app-title)
   :view-filter ""
   :ledger-options ""
   :ledger-command (get-in ledger-commands [0 :value])})

(defonce state (atom initial-state))

(def current-file
  (r/cursor state [:current-file]))
(def window-title
  (r/cursor state [:window-title]))

(add-watch window-title :window-title
           (fn [name atom old new]
             (set! (.-title js/document) new)))
