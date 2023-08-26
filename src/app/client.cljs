(ns app.client
  (:require
    [app.application :refer [app]]
    [app.ui :as ui]
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.data-fetch :as df]))

(defn ^:export init []
  (app/mount! app ui/Root "app")
  (js/console.log "Loaded"))

(defn ^:export refresh []
  ; re-mounting will cause forced UI refresh
  (app/mount! app ui/Root "app")
  ; 3.3.0+ Make sure dynamic queries are refreshed
  (comp/refresh-dynamic-queries! app)
  (js/console.log "Hot reload")
  )

(comment
  (-> app
      (::app/state-atom)
      deref)

  (df/load! app :friends ui/PersonList)
  (df/load! app [:app.model.person/id 1] PersonDetail)

  (df/load! app :app.model.person/id PersonDetail)

  (comp/get-query PersonListItem)
  )
