(ns app.model.person
  (:require
    [clojure.spec.alpha :as s]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(def people
  (atom {1 {::id   1
            ::name "Bob"
            ::age  22
            ::cars #{2}}
         2 {::id   2
            ::name "Sally"
            ::age  26
            ::cars #{1}}}))

(pc/defresolver
  person-resolver [env {::keys [id]}]
  {::pc/input  #{::id}
   ::pc/output [::name ::age {::cars [:app.model.car/id]}]}
  (let [person (-> @people
                   (get id)
                   (update ::cars
                           (fn [ids]
                             (mapv
                               (fn [id] {:app.model.car/id id})
                               ids))))]
    person))

(pc/defresolver
  all-people-resolver [env {}]
  {::pc/output [{:all-people [::id]}]}
  {:all-people
   (mapv (fn [i] {::id i}) (keys @people))})

(pc/defmutation
  make-older [env {::keys [id]}]
  {::pc/input  [::id]
   ::pc/output []}
  (swap! people update-in [id ::age] inc)
  {})

(def resolvers [person-resolver all-people-resolver make-older])
