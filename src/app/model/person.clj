(ns app.model.person
  (:require
    [com.wsscode.pathom.connect :as pc]))

(def people
  (atom {1 {::id   1
            ::name "Bob"
            ::age  22
            ::cars #{2}}
         2 {::id   2
            ::name "Sally"
            ::age  26
            ::cars #{1}}}))

(comment
  (swap! people assoc-in [1 ::age] 22)
  (swap! people assoc-in [1 ::name] "Bob")
  (swap! people assoc-in [1 ::age] 99)
  (swap! people assoc-in [1 ::name] "Tony")
  (swap! people update 1 dissoc ::age)
  @people
  )

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

(pc/defmutation
  select-person [env {::keys [id]}]
  {::pc/input [::id]}
  {::id id})

(def resolvers [person-resolver all-people-resolver make-older select-person])
