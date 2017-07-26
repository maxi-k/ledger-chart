(ns ledger-chart-front.util
  (:require [goog.events :as events]))

(defn kw-to-str [kw]
  (if (keyword? kw) (name kw) kw))

(defn event-listen [target type callback]
  (events/listen target (kw-to-str type)
                 callback))

(defn event-trigger [target type]
  (events/dispatchEvent target (kw-to-str type)))
