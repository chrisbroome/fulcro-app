(ns app.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]))

(defsc Car [this {:car/keys [id model] :as props}]
  {:query [:car/id :car/model]
   :ident :car/id}
  (dom/div "Model: " model))

(def ui-car (comp/factory Car {:keyfn :car/id}))

(defsc Person [this {:person/keys [id name age cars] :as props}]
  {:query [:person/id :person/name :person/age {:person/cars (comp/get-query Car)}]
   :ident :person/id}
  (dom/div
    (dom/div "Name: " name)
    (dom/div "Age: " age)
    (dom/h3 "Cars")
    (dom/ul (map ui-car cars))))

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defonce APP (app/fulcro-app))

(defsc Sample [this {:root/keys [person]}]
  {:query [{:root/person (comp/get-query Person)}]}
  (dom/div
    (ui-person person)))

(defn ^:export init []
  (app/mount! APP Sample "app"))

(comment
  (reset! (::app/state-atom APP) {})

  (merge/merge-component! APP Person {:person/id 3
                                      :person/age 25})
  (app/current-state APP)
  (app/schedule-render! APP)
)
