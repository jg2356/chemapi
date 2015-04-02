(ns chemapi.mol
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

(defn- parse-segment [line [k si sj & fcoll]]
  (let [fmod (apply comp fcoll)]
    [k (fmod (subs line si sj))]))

(defn- parse-segments [line scoll & fcoll]
  (let [fmod (apply comp fcoll)]
    (for [s scoll]
      (parse-segment line (concat s fcoll)))))

(defn- parse-table [line]
  (let [m (parse-segments line [[:atom-count 0 3]
                                [:bond-count 3 6]]
                          #(Long/parseLong %)
                          s/trim)]
    (into {} m)))

(defn- parse-atom [line]
  (let [m (parse-segments line [[:x 0 10]
                                [:y 10 20]
                                [:z 20 30]
                                [:atom 30 34]]
                          s/trim)]
    (into {} m)))

(defn- parse-bond [line]
  (let [m (parse-segments line [[:a 0 3]
                                [:b 3 6]
                                [:bond 6 9]]
                          s/trim)]
    (into {} m)))

(defn parse-mol [f]
  (with-open [fr (io/reader (io/as-file f))]
    (let [lines (->> (line-seq fr)
                     (vec))
          moltitle (-> (nth lines 0))
          software (-> (nth lines 1))
          table (-> (nth lines 3)
                    (parse-table))
          {:keys [atom-count bond-count]} table
          atom-min 4
          atom-max (+ atom-min atom-count)
          bond-min atom-max
          bond-max (+ bond-min bond-count)
          atoms (->> (subvec lines atom-min atom-max)
                     (map parse-atom))
          bonds (->> (subvec lines bond-min bond-max)
                     (map parse-bond))]
      {:title moltitle
       :software software
       :table table
       :atoms atoms
       :bonds bonds})))

(let [mol (parse-mol "Amfetamine.mol")]
  mol)
