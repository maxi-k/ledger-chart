(ns ledger-chart-front.client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [ledger-chart-front.data :as data]
            [clojure.string :as str]
            [cljs.core.async :as async :refer [chan <! >!]]))

(def electron (js/require "electron"))
(def remote (.-remote electron))

(defn choose-file []
  (let [files (js->clj (.showOpenDialog (.-dialog remote) {:properties ["openFile"]}))
        file (nth files 0)
        file-name (last (str/split file #"/"))]
    (when (some? file)
      (reset! data/current-file file)
      (reset! data/window-title (str file-name " - " (:app-title data/constants))))))
