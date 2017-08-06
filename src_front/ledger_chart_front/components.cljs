(ns ledger-chart-front.components
  (:require [clojure.string :as str]
            [reagent.core :as r]
            [soda-ash.core :as sa]
            [goog.dom.forms :as forms]
            [goog.dom :as dom]
            [ledger-chart-front.data :as data]
            [ledger-chart-front.client :as client]))

(defn icon [i & options]
  (let [ops (if (empty? options) {} (first options))
        cls-str (str (or (ops :class) "") " icon " (name i))]
    [:i (merge ops {:class cls-str})]))

(defn search [options]
  [sa/Input (merge {:type "search"
                    :placeholder "Search..."
                    :action {:icon "search"}}
                   options)])

(defn ledger-options-menu []
  [:div#ledger-options-menu
   [sa/Input {:id :ledger-options-input
              :action {:content "Run" :color :blue :icon :refresh
                       :on-click #(client/ledger-xml-store!)}
              :value (:ledger-options @data/state)
              :on-key-up #(swap! data/state assoc :ledger-options (.-value %2))
              :label (r/as-element
                      [sa/Dropdown {:id :ledger-command-dropdown
                                    :options data/ledger-commands
                                    :selection true
                                    :compact true
                                    :header "Command"
                                    :on-change #(swap! data/state assoc :ledger-command (.-value %2))
                                    :value (:ledger-command @data/state)}])
              :label-position :left}]])

(defn file-chooser []
  [sa/Button {:on-click (fn [e] (client/choose-file))}
   "Open File..."])

(defn header []
  [:section#header.fxbox
   [:div#title-wrapper
    [:div#title-logo " "]
    [:h2#title
     (data/constants :app-title)]]
   [ledger-options-menu]
   [file-chooser]])

(defn sidebar []
  [:section#sidebar
   [search {:id :sidebar-filter
            :placeholder "Filter..."
            :fluid true
            :value (:view-filter @data/state)
            :on-change #(swap! data/state assoc :view-filter (.-value %2))
            :action {:icon "filter"}}]])

(defn content []
  [:section#content
   [:p {:style {:color "red"}} @data/ledger-error]
   [:p (str @data/ledger-data)]])
