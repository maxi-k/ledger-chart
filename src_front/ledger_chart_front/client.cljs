(ns ledger-chart-front.client
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [ledger-chart-front.data :as data]
            [clojure.string :as str]
            [cljs.core.async :as async :refer [chan <! >! alts!]]))

(def electron (js/require "electron"))
(def remote (.-remote electron))
(def exec (.-exec (js/require "child_process")))

(defn choose-file []
  (let [files (js->clj (.showOpenDialog (.-dialog remote) {:properties ["openFile"]}))
        file (nth files 0)
        file-name (last (str/split file #"/"))]
    (when (some? file)
      (reset! data/current-file file)
      (reset! data/window-title (str file-name " - " (:app-title data/constants))))))

(defn exec-ledger
  "Executes ledger with given command (bal, reg etc.) and given parameters.
  params has to be a list of parameters.
  out, err, exit should be callbacks for output, error and exit respectively."
  [cmd params out error exit]
  (let [ledger (exec (str "ledger " cmd " --no-pager " params)
                     (clj->js {:maxBuffer (.-MAX_VALUE js/Number)}))]
    (.. ledger -stdout (on "data" out))
    (.. ledger -stderr (on "data" error))
    (.. ledger (on "exit" exit))
    ledger))

(defn ledger-xml-async
  "Executes ledger with the xml option asynchronously.
  Returns a channel in which the entire xml string will be put once ready. "
  [file params]
  (let [data-chan (async/chan)
        err-chan (async/chan)
        exit-chan (async/chan)
        result-chan (go-loop [result ""]
                      (let [[data c] (alts! [data-chan err-chan exit-chan]
                                            :priority true)]
                        (condp identical? c
                          data-chan (recur (str result data))
                          [data result])))]
    (exec-ledger "xml" (str "-f " file " " params)
                 #(async/put! data-chan %)
                 #(async/put! err-chan %)
                 #(async/put! exit-chan %))
    result-chan))

(defn ledger-xml
  "Executes ledger with the xml option asyncrhonously.
  Calls 'callback' with a vector [exit-code or error, result] once ready."
  [file params callback]
  (async/take! (ledger-xml-async file params) callback))
