(ns app.model.person
  (:require
    [com.fulcrologic.fulcro.mutations :refer [defmutation]]))

(defn picker-path [k] [:component/id :person-picker k])

(defmutation make-older [{::keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [::id id ::age] inc))
  (remote [_env] true))

(defmutation select-person [{::keys [id]}]
  (action [{:keys [app state]}]
          (swap! state assoc-in (picker-path :person-picker/selected-person) [::id id]))
  (remote [_] true))
