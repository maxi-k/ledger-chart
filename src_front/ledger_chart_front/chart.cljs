(ns ledger-chart-front.chart
  (:require [reagent.core :as reagent]
            [cljsjs.recharts]))

;; reagent wrappers for rechart
;; from https://github.com/billrobertson42/ninthodds/blob/master/src/ninthodds/rc.cljs
(def XAxis (reagent/adapt-react-class (aget js/Recharts "XAxis")))
(def YAxis (reagent/adapt-react-class (aget js/Recharts "YAxis")))
(def CartesianGrid (reagent/adapt-react-class (aget js/Recharts "CartesianGrid")))
(def Tooltip (reagent/adapt-react-class (aget js/Recharts "Tooltip")))
(def Legend (reagent/adapt-react-class (aget js/Recharts "Legend")))

(def BarChart (reagent/adapt-react-class (aget js/Recharts "BarChart")))
(def Bar (reagent/adapt-react-class (aget js/Recharts "Bar")))
