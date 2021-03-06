(ns chemapi.mol
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [chemapi.tables :as t]))

(defn- parse-decimal [v]
  (BigDecimal. v))

(defn- parse-integer [v]
  (Integer. v))

(defn- find-atom [value]
  (filter (fn [{:keys [symbol number]}]
            (or (= value symbol)
                (= value (str number)))) t/periodic-table))

(defn- find-bond [value]
  (filter (fn [{:keys [type]}]
            (= value type)) t/bond-table))

(defn- map-segment [line [k si sj & fcoll]]
  (let [fmod (apply comp fcoll)]
    [k (fmod (subs line si sj))]))

(defn- map-segments [line scoll & fcoll]
  (let [fmod (apply comp fcoll)
        data (for [s scoll]
               (map-segment line (concat s fcoll)))]
    (into {} data)))

(defn- map-table [line]
  (map-segments line
                [[:atom-count 0 3]
                 [:bond-count 3 6]]
                parse-integer s/trim))

(defn- map-atom [line]
  (map-segments line
                [[:x 0 10 parse-decimal]
                 [:y 10 20 parse-decimal]
                 [:z 20 30 parse-decimal]
                 [:atom 30 34 first find-atom]]
                s/trim))

(defn- map-bond [line]
  (map-segments line
                [[:a 0 3]
                 [:b 3 6]
                 [:bond 6 9 first find-bond]]
                parse-integer s/trim))

(defn parse-mol [f]
  (with-open [fr (io/reader
                  (io/as-file f))]
    (let [lines (vec
                 (line-seq fr))
          moltitle (nth lines 0)
          software (nth lines 1)
          table (map-table
                 (nth lines 3))
          {:keys [atom-count
                  bond-count]} table
          atom-min 4
          atom-max (+ atom-min
                      atom-count)
          bond-min atom-max
          bond-max (+ bond-min
                      bond-count)
          atoms (map map-atom
                     (subvec lines atom-min atom-max))
          bonds (map map-bond
                     (subvec lines bond-min bond-max))]
      {:title moltitle
       :software software
       :table table
       :atoms atoms
       :bonds bonds})))
