(ns app.model.car
  (:require
    [com.wsscode.pathom.connect :as pc]))

(def cars
  (atom {1 {::id    1
       ::make  "Honda"
       ::model "Accord"}
    2 {::id    2
       ::make  "Ford"
       ::model "F-150"}}))

(pc/defresolver car-resolver [env {::keys [id]}]
                {::pc/input  #{::id}
                 ::pc/output [::id ::make ::model]}
                (get @cars id))

(def resolvers [car-resolver])
