(ns ledger-chart-front.components
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [ledger-chart-front.data :as data]
            [ledger-chart-front.logic :as logic]))

(defn icon [i & options]
  (let [ops (if (empty? options) {} (first options))
        cls-str (str (or (ops :class) "") " icon " (name i))]
    [:i (merge ops {:class cls-str})]))

(defn search [options]
  [sa/Input (merge {:type "search"
                    :placeholder "Search..."
                    :action {:icon "search"}}
                   options)])

(defn header []
  [:section#header.fxbox
   [:h2#title (data/constants :app-title)]
   [sa/Button {:compact true
               :on-click (fn [e] (js/alert "open!"))}
    "Open File..."]])

(defn sidebar []
  [:section#sidebar
   [search {:id "sidebar-filter"
            :placeholder "Filter..."
            :fluid true
            :action {:icon "filter"}}]])

(defn content []
  [:section#content
   [:h3 "Charts"]])
