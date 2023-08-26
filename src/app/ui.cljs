(ns app.ui
  (:require [app.model.person :refer [make-older select-person]]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :refer [a button div h3 label li ul]]
            ["react-dom/client" :as dom-client]))

(defsc Car [_ {:app.model.car/keys [model]}]
  {:query [:app.model.car/id :app.model.car/model]
   :ident :app.model.car/id}
  (div "Model: " model))

(def ui-car (comp/factory Car {:keyfn :app.model.car/id}))

(defsc PersonDetail [this {:app.model.person/keys [id name age cars]}]
  {:query [:app.model.person/id
           :app.model.person/name
           :app.model.person/age
           {:app.model.person/cars (comp/get-query Car)}]
   :ident :app.model.person/id}
  (let [onClick (comp/get-state this :onClick)
        onButtonClick (fn [] (comp/transact! this
                                             [(make-older {:app.model.person/id id})]
                                             {:refresh [:person-list/people]}))]
    (div :.ui.segment
         (h3 :.ui.header "Selected Person")
         (when id
           (div :.ui.form
                (div :.field
                     (label {:onClick onClick} "Name: " name))
                (div :.field
                     (label "Age: " age)
                     (button :.ui.button {:onClick onButtonClick} "Make Older"))
                (h3 {} "Cars")
                (ul {}
                    (map ui-car cars)))))))

(def ui-person-detail (comp/factory PersonDetail {:keyfn :app.model.person/id}))

(defsc PersonListItem [this {:app.model.person/keys [id name]}]
  {:query [:app.model.person/id
           :app.model.person/name]
   :ident :app.model.person/id}
  (li :.item
      (a {:href    "#"
          :onClick (fn []
                     (comp/transact! this [{(select-person {:app.model.person/id id})
                                            (comp/get-query PersonDetail)}]))}
         name)))

(def ui-person-list-item (comp/factory PersonListItem {:keyfn :app.model.person/id}))

(defsc PersonList [_ {:person-list/keys [people]}]
  {:query         [{:person-list/people (comp/get-query PersonListItem)}]
   :ident         (fn [_ _] [:component/id ::person-list])
   :initial-state {:person-list/people []}}
  (div :.ui.segment
       (h3 :.ui.header "People")
       (ul
         (map ui-person-list-item people))))

(def ui-person-list (comp/factory PersonList))

(defsc PersonPicker [_ {:person-picker/keys [list selected-person]}]
  {:query         [{:person-picker/list (comp/get-query PersonList)}
                   {:person-picker/selected-person (comp/get-query PersonDetail)}]
   :initial-state {:person-picker/list {}}
   :ident         (fn [] [:component/id :person-picker])}
  (div :.ui.two.column.container.grid
       (div :.column
            (ui-person-list list))
       (div :.column
            (when selected-person
              (ui-person-detail selected-person)))))

(def ui-person-picker (comp/factory PersonPicker {:keyfn :person-picker/people}))

(defsc Root [this {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (div :.ui.container.segment
       (h3 :.ui.header "Application")
       (div :.ui.two.column.container.grid
            (div :.column
                 (button :.ui.button
                         {:onClick (fn [e] (js/console.log e))}
                         "Load"
                         )))
       #_(ui-person-picker person-picker)))
