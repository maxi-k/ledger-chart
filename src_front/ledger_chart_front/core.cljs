(ns ledger-chart-front.core
  (:require  [reagent.core :as r :refer [atom]]
             [ledger-chart-front.util :as util]))

(def initial-state
  {:app {:title "Ledger Chart"}})
(defonce state (atom initial-state))

(defn sidebar []
  [:section#sidebar
   [:h1 (get-in @state [:app :title])]
   ])

(defn content []
  [:section#content
   [:h1 "lol"]])

(defn root-component []
  [:div#app {:style {:width (str (get-in @state [:window :width]) "px")
                     :height (str (get-in @state [:window :height]) "px")}}
   [sidebar]
   [content]])

(defn mount-root [setting]
  (r/render [root-component]
            (.getElementById js/document "app-wrapper")))

(defn init-listeners! []
  (letfn [(update-window-size [] (swap! state update :window merge {:width (.-innerWidth js/window)
                                                                    :height (.-innerHeight js/window)}))]
    (util/event-listen js/window :resize update-window-size)
    (update-window-size)))

(defn init! [setting]
  (init-listeners!)
  (mount-root setting))
