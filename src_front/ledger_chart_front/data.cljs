(ns ledger-chart-front.data
  (:require [reagent.core :as r :refer [atom]]
            [clojure.string :as str]
            [tubax.core :as tubax]))

;; Data management

(def constants
  {:app-title "Ledger Chart"})

(def ledger-commands
  (mapv (fn [[s h]] {:key s :text h :value s})
        [["bal" "Balance"]
         ["reg" "Register"]]))

(def chart-types
  (mapv (fn [[s h]] {:key s :text h :value s})
        [[:monthly "Monthly"]
         [:category "Categories"]]))

(def initial-state
  {:window-title (constants :app-title)
   :view-filter ""
   :ledger-data {}
   :ledger-error ""
   :ledger-options ""
   :ledger-command (get-in ledger-commands [0 :value])
   :selected-categories []
   :chart-type (get-in chart-types [0 :value])})

(defonce state (r/atom initial-state))

(def current-file (r/cursor state [:current-file]))
(def window-title (r/cursor state [:window-title]))
(def ledger-data (r/cursor state [:ledger-data]))
(def ledger-error (r/cursor state [:ledger-error]))

(add-watch window-title :window-title
           (fn [name atom old new]
             (set! (.-title js/document) new)))

;; Utility Functions for data

(def from-xml tubax/xml->clj)

(defn lens-from-str [s]
  (str/split s #":"))

(defn lens-data
  "'Zooms' into the given data with given lens."
  [data lens]
  (if (empty? lens)
    data
    (let [orig-path (if (string? lens) (lens-from-str lens) lens) ]
      (loop [path orig-path
             cursor data
             idx 0]
        (if (or (>= idx (count cursor)) (nil? (cursor idx)))
          nil
          (let [account (cursor idx)
                name (:name account)]
            (if (nil? name)
              (recur path (:accounts account) 0)
              (if (= (first path) name)
                (if (= 1 (count path))
                  account
                  (recur (rest path)
                         (:accounts account)
                         0))
                (recur path cursor (inc idx))))))))))

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

(defn get-accounts [xml-data]
  (->> xml-data
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
                        :full-name (-> content second :content first)
                        :accounts content}
               :recur-on :accounts}))))))
