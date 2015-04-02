(ns chemapi.mol
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(defn- parse-line [l]
  (-> (s/trim l)
      (s/replace #"(\s)+" " ")
      (s/split  #"\s")))

(defn- parse-atom [[x y z t & more]]
  {:x (Float/parseFloat x)
   :y (Float/parseFloat y)
   :z (Float/parseFloat z)
   :type t})

(defn- parse-bond [[s e t & more]]
  {:a (Long/parseLong s)
   :b (Long/parseLong e)
   :type t})

(defn- parse-table [[a b & more]]
  (let [atom-count (Long/parseLong a)
        bond-count (Long/parseLong b)
        atom-min 4
        atom-max (+ atom-min atom-count)
        bond-min atom-max
        bond-max (+ bond-min bond-count)]
    {:atom-min atom-min
     :atom-max atom-max
     :bond-min bond-min
     :bond-max bond-max}))

(defn parse-mol [f]
  (with-open [fr (io/reader (io/as-file f))]
    (let [data (->> (line-seq fr)
                    (map parse-line)
                    (vec))
          moltitle (-> (nth data 0)
                       (get 0))
          software (-> (nth data 1)
                       (get 0))
          table (-> (nth data 3)
                    (parse-table))
          {:keys [atom-min atom-max bond-min bond-max]} table
          atoms (->> (subvec data atom-min atom-max) (map parse-atom))
          bonds (->> (subvec data bond-min bond-max) (map parse-bond))]
      {:title moltitle
       :software software
       :atoms atoms
       :bonds bonds})))

;(let [mol (parse-mol "Amfetamine.mol")]
;  mol)
