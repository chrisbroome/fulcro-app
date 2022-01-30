(ns app.client
  (:require
    ["react-number-format" :as NumberFormat]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.rendering.ident-optimized-render :as ior]
    [com.fulcrologic.fulcro.rendering.keyframe-render :as keyframe]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom :refer [div h3 ul li label button]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]))

(defmutation make-older [{:person/keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [:person/id id :person/age] inc)))

(def ui-number-format (interop/react-factory NumberFormat))


(defsc Car [this {:car/keys [id model] :as props}]
  {:query         [:car/id :car/model]
   :ident         :car/id
   :initial-state {:car/id    :param/id
                   :car/model :param/model}}
  (js/console.log "Render Car " id)
  (div "Model: " model))

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
  (js/console.log "Render Person " id)
  (let [onMakeOlderButtonClick (fn []
                                 (comp/transact!
                                   this
                                   `[(make-older ~{:person/id id})]
                                   {:refresh [:person-list/people]}))]
    (div :.ui.segment
         (div :.ui.form
              (div :.field
                   (label "Name: ")
                   name)
              (div :.field
                   (label "Age: ")
                   age)
              (div :.field
                   (dom/button :.ui.button
                               {:onClick onMakeOlderButtonClick}
                               "Make Older"))
              #_(div :.field
                   (label "Amount: ")
                   (ui-number-format {:thousandSeparator true
                                      :prefix            "$"}))
              (h3 "Cars")
              (ul (map ui-car cars))))))

(def ui-person (comp/factory Person {:keyfn :person/id}))

(defsc PersonList [this {:person-list/keys [people]}]
  {:query         [{:person-list/people (comp/get-query Person)}]
   :ident         (fn [_ _] [:component/id ::person-list])
   :initial-state {:person-list/people [{:id 1 :name "Bob"}
                                        {:id 2 :name "Sally"}]}}
  (js/console.log "Render PersonList ")
  (let [cnt (reduce
              (fn [c {:person/keys [age]}]
                (if (> age 30)
                  (inc c)
                  c))
              0
              people)]
    (div :.ui.segment
         (h3 :.ui.header "People")
         (div "Over 30: " cnt)
         (map ui-person people))))

(def ui-person-list (comp/factory PersonList))

(defonce APP (app/fulcro-app {:optimized-render! ior/render!}))

(defsc Sample [this {:root/keys [people]}]
  {:query         [{:root/people (comp/get-query PersonList)}]
   :initial-state {:root/people {}}}
  (js/console.log "Render Sample ")
  (div
    (h3 :.ui.header "Application")
    (when people
      (ui-person-list people))))

(defn ^:export init []
  (app/mount! APP Sample "app"))

(comment
  (reset! (::app/state-atom APP) {})

  (app/current-state APP)
  (comp/transact! APP [(make-older {:person/id 1})])
  (comp/prop->classes APP :person/age)
  (comp/class->all APP Person)
  )
