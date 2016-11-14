(ns ledger-chart.pages
  (:require [hiccup.page :as h]))

(defn index
  "Index page."
  []
  (h/html5
   {:lang :en}
   [:head
    [:title "Finance Chart"]
    (h/include-css "css/core.css")
    (h/include-js "js/core.js")]
   [:body
    [:div#content
     "This is content."
     ]]))
