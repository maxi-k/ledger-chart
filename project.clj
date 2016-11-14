(defproject ledger-chart "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.5.1"]
                 [hiccup "1.0.4"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/data.xml "0.0.8"]]
  :plugins [[lein-ring "0.9.7"]]
  :main ^:skip-aot ledger-chart.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies
                   [[javax.servlet/servlet-api "2.5"]
                    [ring/ring-mock "0.3.0"]]}}

  :ring {:handler restful-clojure.handler/app
         :nrepl {:start? true
                 :port 9998}}
  )
