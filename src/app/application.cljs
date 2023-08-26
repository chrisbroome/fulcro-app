(ns app.application
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.networking.http-remote :as http]
    [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]))


(defn mount-fn [app]
  #_(df/load! app :all-people PersonListItem
              {:target [:component/id ::person-list :person-list/people]}))

(defonce app
         (-> (app/fulcro-app
               {:remotes          {:remote (http/fulcro-http-remote {})}
                :client-did-mount mount-fn})
             (with-react18)))
