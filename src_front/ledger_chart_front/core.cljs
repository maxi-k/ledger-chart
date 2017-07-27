(ns ledger-chart-front.core
  (:require  [reagent.core :as r :refer [atom]]
             [ledger-chart-front.components :as components]
             [ledger-chart-front.data :as data]
             [ledger-chart-front.util :as util]))

(defn root-component []
  [:div#app {:style {:width (str (get-in @data/state [:window :width]) "px")
                     :height (str (get-in @data/state [:window :height]) "px")}}
   [components/header]
   [components/sidebar]
   [components/content]])

(defn mount-root [setting]
  (r/render [root-component]
            (.getElementById js/document "app-wrapper")))

(defn init-listeners! []
  (letfn [(update-window-size [] (swap! data/state update :window merge {:width (.-innerWidth js/window)
                                                                         :height (.-innerHeight js/window)}))]
    (util/event-listen js/window :resize update-window-size)
    (update-window-size)))

(defn init! [setting]
  (init-listeners!)
  (mount-root setting))
