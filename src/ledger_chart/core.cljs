(ns ledger-chart.core
  (:require [cljs.nodejs :as nodejs]
            [ledger-chart.api :as api]
            [ledger-chart.util :as util]))

(def Electron (nodejs/require "electron"))

(def BrowserWindow (.-BrowserWindow Electron))

(def crash-reporter (.-crashReporter Electron))

(def Os (nodejs/require "os"))

(def *win* (atom nil))

(def app (.-app Electron))

(defn -main []
  (.start crash-reporter (clj->js {:companyName "Maximilian Kuschewski"
                                   :submitURL   "maximilian-kuschewski.de"}))

  ;; error listener
  (.on nodejs/process "error"
       (fn [err] (.log js/console err)))

  ;; window all closed listener
  (.on app "window-all-closed"
       (fn [] (if (not= (.-platform nodejs/process) "darwin")
                (.quit app))))

  ;; ready listener
  (.on app "ready"
       (fn []
         (reset! *win* (BrowserWindow. (clj->js {:width 800
                                                 :height 600
                                                 :minWidth 700
                                                 :minHeight 420})))
         ;; when no optimize comment out
         (.loadURL @*win* (str "file://" (util/app-path "../index.html")))
         ;; when no optimize uncomment
         ;; (.loadURL @*win* (str "file://" (util/app-path "../../../index.html")))

         (.on @*win* "closed" (fn [] (reset! *win* nil))))))

(nodejs/enable-util-print!)

;;; "Linux" or "Darwin" or "Windows_NT"
(.log js/console (str "Start descjop application on " (.type Os) "."))

(set! *main-cli-fn* -main)
