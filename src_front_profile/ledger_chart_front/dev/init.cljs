(ns ledger-chart-front.init
  (:require [figwheel.client :as fw :include-macros true]
            [ledger-chart-front.core :as core]
            [ledger-chart-front.conf :as conf]))

(enable-console-print!)

(fw/watch-and-reload
 :websocket-url   "ws://localhost:3449/figwheel-ws"
 :jsload-callback 'start-descjop!)

(defn start-descjop! []
  (core/init! conf/setting))

(start-descjop!)
