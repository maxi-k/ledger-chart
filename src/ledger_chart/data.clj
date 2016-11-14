(ns ledger-chart.data
  (:require [clojure.java.shell :as shell]
            [clojure.data.json :as json]
            [clojure.xml :as xml]))

(declare get-accounts)

(def data-store
  "The xml data from ledger parsed as clojure data structures."
  (atom {}))

(defn fetch-xml
  "Returns the xml representation of the ledger file given by the argument"
  [file & other-args]
  (let [args (into ["ledger" "xml" "-f" file] other-args)]
    (:out (apply shell/sh args))))

(defn parse-xml
  "Parses given xml string into clojure data."
  [xml-str]
  (let [xml (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-str)))
        lol (print (:tag xml))
        accounts (get-accounts xml)]
    (print (:tag accounts))
    accounts
    ))

(defn store-data
  "Stores the given clojure data in the data-store atom."
  [data]
  (reset! data-store data))

(defn lens-data
  "'Zooms' into the given data with given lens."
  [data lens]
  (if (empty? lens)
    data
    (get-in data lens)))

(defn jsonify-data
  "Converts the given data into json
  that can be sent back to the client
  for display."
  [data]
  (json/write-str data))

;; TODO: Not working yet.
(defn walk-structure
  ([f s] (walk-structure f f s))
  ;; (not empty? keys) => (map? result)
  ;; fnv :: vector -> {:result (maybe result) :recur-on (maybe keys)}
  ;; fnm :: map -> {:result (maybe result) :recur-on (maybe keys)}
  ([fnm fnv s]
   (let [handler ;; handle a result from one of the callbacks
         (fn [item]
           (if (nil? (item :result))
             ;; if the result was nil return nil
             nil
             ;; if the result has something to recur on, do that
             ;; otherwise just result it
             (if-let [rec (item :recur-on)]
               (if (vector? rec)
                 ;; if there's multiple things to recur on, reduce over the keys,
                 ;; associng the recursive result onto the result-map as key
                 (reduce (fn [col k]
                           (assoc col k (walk-structure fnv fnm (col k))))
                         (item :result)
                         rec)
                 ;; Otherwise assoc the resursive result onto the result map directly
                 (assoc (item :result) rec (walk-structure fnv fnm (-> item :result rec))))
               (item :result))))]
     (if (vector? s)
       ;; If the structure is a vector, apply the callback to each element
       (let [res (map s fnv)]
         (map handler res))
       ;; Otherwise apply the handler to the result directly
       (handler (fnm s))))))

(defn get-accounts [xml]
  (let [accounts (->> xml
                      :content
                      (filter #(and (map? %) (= (% :tag) :accounts))))

        ]
    accounts))
