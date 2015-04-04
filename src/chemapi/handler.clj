(ns chemapi.handler
  (:require [compojure.api.sweet :refer :all]
            [compojure.handler :as handler]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [chemapi.mol :as mol]))

(s/defschema Upload
  {:type "file"})

(defapi api
  (swagger-ui)
  (swagger-docs
    :title "Chemistry API")
  (swaggered "molfile"
    :description "MOL/MDL endpoints"
    (POST* "/api/molfile" []
           :summary
           "
           <form class=\"sandbox\" method=\"POST\" action=\"/api/molfile\" enctype=\"multipart/form-data\">
             <label for=\"molfile_file\">file: <label><input id=\"molfile_file\" name=\"file\" type=\"file\" />
             <input class=\"submit\" name=\"commit\" type=\"button\" value=\"Try it out!\" onclick=\"$('#molfile_postApiMolfile_content').slideDown();\">
           </form>
           <style>
             #molfile_postApiMolfile_content form {
               display: none!important;
             }
           </style>
           "
           (fn [request]
             (let [molfile (get-in request [:params :file :tempfile])]
               (when molfile
                 (ok (mol/parse-mol molfile))))))))

(def app
  (handler/site api))
