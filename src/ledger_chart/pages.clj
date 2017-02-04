(ns ledger-chart.pages
  (:require [hiccup.page :as h]))

(def styles ["css/normalize.css"
             "css/core.css"])

(def javascripts
  ["js/jquery-3.1.1.min.js"
   "js/Chart.bundle.min.js"
   "js/core.js"])

(defn index
  "Index page."
  []
  (h/html5
   {:lang :en}
   [:head
    [:title "Finance Chart"]
    (map h/include-css styles)
    (map h/include-js javascripts)]
   [:body
    [:div#content
     [:div#sidebar
      [:div#title [:h1 "Ledger-Chart"]]
      [:div#chart-selector]
      [:div#navigation]]
     [:div#charts
      [:div#charts-js-wrapper]]
     [:div.clearfloat]
     ]]))
