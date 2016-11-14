(ns ledger-chart.serve
  (:use compojure.core)
  (:require [clojure.java.browse :as browse]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :refer :all] ;; <-- need a server!
            [ledger-chart.pages :as pages]
            [ledger-chart.data :as data]))

(declare parse-lens)

(defroutes service-routes
  (GET "/data/:lens" [lens]
       (-> @data/data-store
           (data/lens-data (parse-lens lens))
           (data/jsonify-data))
   )
  (GET "/" [] (pages/index))
  (route/resources "/")
  (route/not-found "There's nothing here. Move along!"))

(def service
  (handler/site service-routes))

(defn serve
  "Starts a webserver which serves a website
  used to visualize data. Opens a browser to the adress."
  []
  (let [port 8765]
    (browse/browse-url (str "http://localhost:" port))
    (run-jetty service {:port port})))

(defn- parse-lens
  "Parses given url data request into a 'lens'
  used to zoom into the ledger data."
  [url]
  (if (empty? url)
    []
    (map keyword
         (clojure.string/split url ":"))))
