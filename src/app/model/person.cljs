(ns app.model.person
  (:require
    [com.fulcrologic.fulcro.mutations :refer [defmutation]]))

(defmutation make-older [{::keys [id]}]
  (action [{:keys [state]}]
          (swap! state update-in [::id id ::age] inc))
  (remote [env] true))
