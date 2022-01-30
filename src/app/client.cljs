(ns app.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation make-older [{:person/keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [:person/id id :person/age] inc)))

(defsc Car [this {:car/keys [id model] :as props}]
  {:query         [:car/id :car/model]
   :ident         :car/id
   :initial-state {:car/id    :param/id
                   :car/model :param/model}}
  (dom/div "Model: " model))

(def ui-car (comp/factory Car {:keyfn :car/id}))

(defsc Person [this {:person/keys [id name age cars] :as props}]
  {:query         [:person/id :person/name :person/age {:person/cars (comp/get-query Car)}]
   :ident         :person/id
   :initial-state {:person/id   :param/id
                   :person/name :param/name
                   :person/age  20
                   :person/cars [{:id 40 :model "Tacoma"}
                                 {:id 41 :model "Escort"}
                                 {:id 42 :model "F-150"}]}}
  (dom/div
    (dom/div "Name: " name)
    (dom/div "Age: " age)
    (dom/button {:onClick #(comp/transact! this [(make-older {:person/id id})])}
                "Make Older")
    (dom/h3 "Cars")
    (dom/ul (map ui-car cars))))

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defonce APP (app/fulcro-app))

(defsc Sample [this {:root/keys [person]}]
  {:query         [{:root/person (comp/get-query Person)}]
   :initial-state {:root/person {:id 1 :name "Bob"}}}
  (dom/div
    (when person
      (ui-person person))))

(defn ^:export init []
  (app/mount! APP Sample "app"))

(comment
  (reset! (::app/state-atom APP) {})
  (merge/merge-component! APP Person {:person/id  1
                                      :person/age 20})
  (comp/get-initial-state Sample)

  (app/current-state APP)
  (comp/transact! APP [(make-older {:person/id 1})])
  )
