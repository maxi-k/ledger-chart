(ns ledger-chart.serve
  (:use compojure.core)
  (:require [clojure.java.browse :as browse]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.adapter.jetty :refer :all] ;; <-- need a server!
            [ledger-chart.pages :as pages]
            [ledger-chart.data :as data]
            [clojure.data.json :as json]))

(defn handle-lens-request [lenses]
  (data/jsonify-data
   (map (fn [lens]
          (-> @data/data-store
              (data/lens-data lens))) lenses)))

(defroutes service-routes
  (POST "/data" {{lenses :lenses} :params :as req} (handle-lens-request lenses))
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
