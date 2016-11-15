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
  (get-accounts
   (xml/parse (java.io.ByteArrayInputStream. (.getBytes xml-str)))))

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
   (letfn [(handler [item] ;; handle a result from one of the callbacks
             (if-let [item-result (and item (item :result))]
               ;; if the result has something to recur on, do that
               ;; otherwise just return it
               (if-let [rec (item :recur-on)]
                 (if (vector? rec)
                   ;; if there's multiple things to recur on, reduce over the keys,
                   ;; associng the recursive result onto the result-map as key
                   (apply vector
                          (reduce (fn [col k]
                                    (assoc col k (walk-structure fnm fnv (col k))))
                                  item-result
                                  rec))
                   ;; Otherwise assoc the resursive result onto the result map directly
                   (assoc item-result rec (walk-structure fnm fnv (-> item :result rec))))
                 item-result)))]
     ;; If the structure is a vector, apply the callback to each element
     ;; Otherwise apply the handler to the result directly
     (if (vector? s)
       (apply vector
              (reduce (fn [col v]
                        (if-let [res (handler (fnv v))]
                          (conj col res)
                          col))
                      [] s))
       (handler (fnm s))))))

(defn tap [f x]
  (println (f x))
  x)

(defn get-accounts [xml]
  (->> xml
       :content
       (filter #(and (map? %) (= (% :tag) :accounts)))
       first
       :content
       (walk-structure
        (fn [elem]
          (when (and (map? elem) (= :account (:tag elem)))
            (let [content (:content elem)]
              {:result {:amount (-> content (nth 2) :content first :content second :content first)
                        :name (-> content first :content first)
                        :accounts content}
               :recur-on :accounts}))))))
