(ns chemapi.handler
  (:require [compojure.api.sweet :refer :all]
            [compojure.handler :as handler]
            [ring.util.http-response :refer :all]
            [ring.middleware.cors :refer [wrap-cors]]
            [schema.core :as s]
            [chemapi.mol :as mol]))

(s/defschema Upload
  {:type "file"})

(defapi api
  (swagger-ui)
  (swagger-docs
    :title "Chemistry API")
  (swaggered "molfile"
    :description "molecular structure file endpoint"
    (POST* "/api/molfile" []
           :summary
           "<form class=\"sandbox\" method=\"POST\" action=\"api/molfile\" enctype=\"multipart/form-data\">
              <label>file: <label><input name=\"file\" type=\"file\" />
              <input class=\"submit\" name=\"commit\" type=\"button\" value=\"Try it out!\" onclick=\"$('#molfile_postApiMolfile_content').slideDown();\">
            </form>
            <style>
              #molfile_postApiMolfile_content form {
                display: none!important;
              }
            </style>"
           (fn [request]
             (let [molfile (get-in request [:params :file :tempfile])]
               (when molfile
                 (ok (mol/parse-mol molfile))))))))

(def app
  (-> (routes api)
      (handler/site)
      (wrap-cors #".*")))
