(ns ledger-chart-front.chart
  (:require [reagent.core :as reagent]
            [cljsjs.recharts]
            [ledger-chart-front.data :as data]))

(def example-data
  {:monthly [{:name "2018-01"
              :account/income 100
              :account/expenses 100
              :account/assets 1098
              :account/liabilities 50}
             {:name "2018-02"
              :account/income 301
              :account/expenses 182
              :account/assets 1034
              :account/liabilities 108}
             {:name "2018-03"
              :account/income 1982
              :account/expenses 102}]
   :category []})

;; reagent wrappers for rechart
;; from https://github.com/billrobertson42/ninthodds/blob/master/src/ninthodds/rc.cljs

(defn- adopt-rechart
  [name]
  (reagent/adapt-react-class
   (aget js/Recharts name)))

(def ResponsiveContainer (adopt-rechart "ResponsiveContainer"))
(def CartesianGrid (adopt-rechart "CartesianGrid"))
(def Tooltip (adopt-rechart "Tooltip"))
(def Legend (adopt-rechart "Legend"))
(def Line (adopt-rechart "Line"))
(def XAxis (adopt-rechart "XAxis"))
(def YAxis (adopt-rechart "YAxis"))

(def PieChart (adopt-rechart "PieChart"))
(def Pie (adopt-rechart "Pie"))
(def LineChart (adopt-rechart "LineChart"))
(def BarChart (adopt-rechart "BarChart"))
(def Bar (adopt-rechart "Bar"))

(def account-colors
  "Colors used for commonly named accounts per default."
  {:expenses "#DD2C00"
   :income "#558B2F"
   :assets "#01579B"
   :liabilities "#7B1FA2"
   :default "#263238"})

(def sign-colors
  {:negative "#E64A19"
   :positive "#7CB342"})

(def default-account-color
  "Color used for account-names which do not have a
  predefined color set."
  (:default account-colors))

(defmulti preprocess-data
  "Preprocess the chart data for a given chart type,
  so it can be displayed by recharts."
  identity)

(defmethod preprocess-data :monthly
  [type data]
  data)

(defmethod preprocess-data :category
  [type data]
  (let [acc-list (-> data data/get-accounts first :accounts)]
    (reduce
     (fn [coll acc-data]
       (if (< (:amount acc-data) 0)
         (update coll :negative conj {:name (:name acc-data)
                                      :amount (- (js/parseFloat (:amount acc-data)))})
         (update coll :positive conj {:name (:name acc-data)
                                      :amount (js/parseFloat (:amount acc-data))})))
     {:negative []
      :positive []}
     acc-list)))

(defn- responsive-chart
  [chart]
  [ResponsiveContainer {:minWidth 500 :minHeight 500}
   chart])

(defmulti draw-chart
  "Reagent component for drawing a chart,
  which dispatches based on the given chart type."
  identity)

(defmethod draw-chart :monthly
  [type data]
  [responsive-chart
   [LineChart {:data (type example-data)}
    [XAxis {:dataKey :name}]
    [YAxis]
    [CartesianGrid {:strokeDasharray "3 3"}]
    [Tooltip]
    [Legend]
    (for [acc-name [:income :expenses :liabilities :assets]]
      [Line {:key acc-name
             :type "monotone"
             :dataKey (keyword :account acc-name)
             :stroke (get account-colors acc-name default-account-color)}])]])

(defmethod draw-chart :category
  [type data]
  (let [pdata (preprocess-data type data)]
    [responsive-chart
     [PieChart
      [Tooltip]
      [Pie {:data (:negative pdata)
            :dataKey :amount
            :innerRadius 150
            :outerRadius 200
            :fill (:negative sign-colors)
            :paddingAngle 5}]
      [Pie {:data (:positive pdata)
            :dataKey :amount
            :innerRadius 0
            :outerRadius 120
            :fill (:positive sign-colors)
            :paddingAngle 5}]]]))

(defmethod draw-chart :default
  [type data]
  [:p
   "No valid chart type selected: "
   (str type)])
