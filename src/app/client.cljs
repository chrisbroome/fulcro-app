(ns app.client
  (:require
    [app.model.person :refer [make-older]]
    ["react-number-format" :as NumberFormat]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]
    [com.fulcrologic.fulcro.dom :as dom :refer [div h3 ul li label button]]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [com.fulcrologic.fulcro.networking.http-remote :as http]
    [com.fulcrologic.fulcro.rendering.ident-optimized-render :as ior]
    [com.fulcrologic.fulcro.rendering.keyframe-render :as keyframe]))

(def ui-number-format (interop/react-factory NumberFormat))


(defsc Car [this {:app.model.car/keys [id model] :as props}]
  {:query         [:app.model.car/id :app.model.car/model]
   :ident         :app.model.car/id}
  (div "Model: " model))

(def ui-car (comp/factory Car {:keyfn :app.model.car/id}))

(defsc Person [this {:app.model.person/keys [id name age cars] :as props}]
  {:query         [:app.model.person/id
                   :app.model.person/name
                   :app.model.person/age
                   {:app.model.person/cars (comp/get-query Car)}]
   :ident         :app.model.person/id}
  (let [onMakeOlderButtonClick (fn []
                                 (comp/transact!
                                   this
                                   [(make-older {:app.model.person/id id})]
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

(def ui-person (comp/factory Person {:keyfn :app.model.person/id}))

(defsc PersonList [this {:person-list/keys [people]}]
  {:query         [{:person-list/people (comp/get-query Person)}]
   :ident         (fn [_ _] [:component/id ::person-list])}
  (let [cnt (reduce
              (fn [c {:app.model.person/keys [age]}]
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

(defonce APP
         (app/fulcro-app
           {:optimized-render! ior/render!
            :remotes           {:remote (http/fulcro-http-remote {})}
            :client-did-mount  (fn [app]
                                 (df/load! app :all-people Person
                                           {:target [:component/id ::person-list :person-list/people]})
                                 )}))

(defsc Sample [this {:root/keys [list]}]
  {:query         [{:root/list (comp/get-query PersonList)}]
   :initial-state {:root/list {}}}
  (div
    (h3 :.ui.header "Application")
    (ui-person-list list)))

(defn ^:export init []
  (app/mount! APP Sample "app"))

(comment
  (reset! (::app/state-atom APP) {})

  (app/current-state APP)
  (comp/transact! APP [(make-older {:person/id 1})])
  (comp/prop->classes APP :person/age)
  (comp/class->all APP Person)
  )
