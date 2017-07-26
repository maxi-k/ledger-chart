(ns ledger-chart.util
  (:require [cljs.nodejs :as nodejs]))

(def path (nodejs/require "path"))

(defn app-path [suffix]
  (.resolve path (js* "__dirname") suffix))
