(ns ledger-chart-front.chart
  (:require [reagent.core :as reagent]
            [cljsjs.recharts]))

(def example-data
  {:monthly [{:name "2018-01"
              :income 100
              :expenses 100}
             {:name "2018-02"
              :income 301
              :expenses 182}
             {:name "2018-03"
              :income 1982
              :expenses 102}]
   :category []})

;; reagent wrappers for rechart
;; from https://github.com/billrobertson42/ninthodds/blob/master/src/ninthodds/rc.cljs
(def ResponsiveContainer (reagent/adapt-react-class (aget js/Recharts "ResponsiveContainer")))
(def CartesianGrid (reagent/adapt-react-class (aget js/Recharts "CartesianGrid")))
(def Tooltip (reagent/adapt-react-class (aget js/Recharts "Tooltip")))
(def Legend (reagent/adapt-react-class (aget js/Recharts "Legend")))
(def Line (reagent/adapt-react-class (aget js/Recharts "Line")))
(def XAxis (reagent/adapt-react-class (aget js/Recharts "XAxis")))
(def YAxis (reagent/adapt-react-class (aget js/Recharts "YAxis")))

(def LineChart (reagent/adapt-react-class (aget js/Recharts "LineChart")))
(def BarChart (reagent/adapt-react-class (aget js/Recharts "BarChart")))
(def Bar (reagent/adapt-react-class (aget js/Recharts "Bar")))

(defmulti draw-chart
  "Reagent component for drawing a chart,
  which dispatches based on the given chart type."
  identity)

(defmethod draw-chart :monthly
  [type data]
  [ResponsiveContainer {:minWidth 500 :minHeight 500}
   [LineChart {:data (type example-data)}
    [XAxis {:dataKey :name}]
    [YAxis]
    [CartesianGrid {:strokeDasharray "3 3"}]
    [Tooltip]
    [Legend]
    [Line {:type "monotone" :dataKey :expenses}]
    [Line {:type "monotone" :dataKey :income}]]]
  )

(defmethod draw-chart :category
  [type data]
  [:p "Category"])

(defmethod draw-chart :default
  [type data]
  [:p
   "No valid chart type selected: "
   (str type)])
