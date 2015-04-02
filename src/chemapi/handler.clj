(ns chemapi.handler
  (:require [compojure.api.sweet :refer :all]
            [compojure.handler :as handler]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [chemapi.mol :as mol]))

(defapi api
  (swagger-ui)
  (swagger-docs
    :title "Chemistry API")
  (swaggered "molfile"
    :description "MOL/MDL endpoints"
    (POST* "/api/molfile" []
           :summary "parses a molfile"
           (fn [request]
             (let [molfile (get-in request [:params :file :tempfile])]
               (when molfile
                 (ok (mol/parse-mol molfile))))))))

(def app
  (handler/site api))
