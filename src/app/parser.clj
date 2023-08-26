(ns app.parser
  (:require
    [app.model.car :as car]
    [app.model.person :as person]
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))


(def my-resolvers (into [] (concat car/resolvers person/resolvers)))

;; setup for a given connect system
(def pathom-parser
  (p/parallel-parser
    {::p/env     {::p/reader                 [p/map-reader
                                              pc/async-reader2
                                              pc/parallel-reader
                                              pc/open-ident-reader]
                  ::pc/mutation-join-globals [:tempids]}
     ::p/mutate  pc/mutate-async
     ::p/plugins [(pc/connect-plugin {::pc/register my-resolvers})
                  p/error-handler-plugin
                  ;; or p/elide-special-outputs-plugin
                  (p/post-process-parser-plugin p/elide-not-found)]}))

(defn api-parser [query]
  (log/info "Process" query)
  (pathom-parser {} query))
