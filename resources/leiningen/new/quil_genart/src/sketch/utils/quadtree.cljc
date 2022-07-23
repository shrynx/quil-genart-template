(ns sketch.utils.quadtree)

;; data structures

(defrecord Point
           [x y data])

(defrecord QuadTreeNode
           [boundary points
            northWest northEast
            southWest southEast
            maxPoints
            data])


;; private

(defn- q-contains?
  "Returns true if boundary contains given point."
  [boundary point]
  (cond (< (:x point) (-> boundary :nw :x)) false
        (> (:x point) (-> boundary :se :x)) false
        (< (:y point) (-> boundary :nw :y)) false
        (> (:y point) (-> boundary :se :y)) false
        :else true))

(defn- q-intersects?
  "Returns true if given bounding boxes intersect."
  [box1 box2]
  (not (or  (> (-> box1 :nw :x) (-> box2 :se :x))
            (< (-> box1 :se :x) (-> box2 :nw :x))
            (> (-> box1 :nw :y) (-> box2 :se :y))
            (< (-> box1 :se :y) (-> box2 :nw :y)))))

(defn- leaf?
  "Returns true if given node is a leaf node."
  [node]
  (if (-> node :northWest)
    false
    true))

(defn- get-child-boundaries [boundary]
  (let [mid-x (/ (+ (-> boundary :nw :x) (-> boundary :se :x)) 2)
        mid-y (/ (+ (-> boundary :nw :y) (-> boundary :se :y)) 2)]
    {:northWest {:nw {:x (-> boundary :nw :x)
                      :y (-> boundary :nw :y)}
                 :se {:x mid-x
                      :y mid-y}}
     :northEast {:nw {:x mid-x
                      :y (-> boundary :nw :y)}
                 :se {:x (-> boundary :se :x)
                      :y mid-y}}
     :southWest {:nw {:x (-> boundary :nw :x)
                      :y mid-y}
                 :se {:x mid-x
                      :y (-> boundary :se :y)}}
     :southEast {:nw {:x mid-x
                      :y mid-y}
                 :se {:x (-> boundary :se :x)
                      :y (-> boundary :se :y)}}}))

(defn- find-points-to-insert
  "Returns a map with :included and :rest points to insert.
The :included one will be inserted. This is needed for deduplicating
of fringe-points."
  [points boundary]
  (reduce (fn [acc p]
            (if (q-contains? boundary p)
              (update-in acc [:included] conj p)
              (update-in acc [:rest] conj p))) {:included [] :rest []} points))

(defn- subdivide
  "Divides given node into 4 subnodes and returns them."
  [{:keys [:boundary :points] :as node}]
  (let [boundaries (get-child-boundaries boundary)
        points-northWest (find-points-to-insert
                          points (-> boundaries :northWest))
        points-northEast (find-points-to-insert
                          (-> points-northWest :rest) (-> boundaries :northEast))
        points-southWest (find-points-to-insert
                          (-> points-northEast :rest) (-> boundaries :southWest))
        points-southEast (find-points-to-insert
                          (-> points-southWest :rest) (-> boundaries :southEast))
        northWest (QuadTreeNode. (-> boundaries :northWest)
                                 (-> points-northWest :included)
                                 nil
                                 nil
                                 nil
                                 nil
                                 (-> node :maxPoints)
                                 (-> node :data))
        northEast (QuadTreeNode. (-> boundaries :northEast)
                                 (-> points-northEast :included)
                                 nil
                                 nil
                                 nil
                                 nil
                                 (-> node :maxPoints)
                                 (-> node :data))
        southWest (QuadTreeNode. (-> boundaries :southWest)
                                 (->  points-southWest :included)
                                 nil
                                 nil
                                 nil
                                 nil
                                 (-> node :maxPoints)
                                 (-> node :data))
        southEast (QuadTreeNode. (-> boundaries :southEast)
                                 (-> points-southEast :included)
                                 nil
                                 nil
                                 nil
                                 nil
                                 (-> node :maxPoints)
                                 (-> node :data))]
    {:northWest northWest :northEast northEast
     :southWest southWest :southEast southEast}))


;; public

;; creation

(defn make-quadtree
  "Creates a quadtree with the given boundary and set the maximum
  number of points in a leaf to the given value. If maxPoints not
  given use 100 as default."
  ([boundary]
   (make-quadtree boundary 100 nil))
  ([boundary data]
   (make-quadtree boundary 100 data))
  ([boundary maxPoints data]
   (QuadTreeNode. boundary [] nil
                  nil
                  nil
                  nil
                  maxPoints
                  data)))


;; insertion

(defn insert
  "Inserts the given point into given quadtree. Returns a newly
   quadtree and does not mutate the given tree."
  ([tree point] (insert tree point nil))
  ([tree point data]
   (cond
     (not (q-contains? (-> tree :boundary) point)) tree
     (and (leaf? tree)
          (< (count (-> tree :points)) (-> tree :maxPoints)))
     (QuadTreeNode. (-> tree :boundary) (conj (-> tree :points) point)
                    nil
                    nil
                    nil
                    nil
                    (-> tree :maxPoints)
                    data)
     (not (leaf? tree))
     (QuadTreeNode. (-> tree :boundary) []
                    (insert (-> tree :northWest) point)
                    (insert (-> tree :northEast) point)
                    (insert (-> tree :southWest) point)
                    (insert (-> tree :southEast) point)
                    (-> tree :maxPoints)
                    data)
     :else
     (let [child-nodes (subdivide tree)]
       (cond (q-contains? (-> child-nodes :northWest :boundary) point)
             (QuadTreeNode. (-> tree :boundary) []
                            (insert (-> child-nodes :northWest) point)
                            (-> child-nodes :northEast)
                            (-> child-nodes :southWest)
                            (-> child-nodes :southEast)
                            (-> tree :maxPoints)
                            data)
             (q-contains? (-> child-nodes :northEast :boundary) point)
             (QuadTreeNode. (-> tree :boundary) []
                            (-> child-nodes :northWest)
                            (insert (-> child-nodes :northEast) point)
                            (-> child-nodes :southWest)
                            (-> child-nodes :southEast)
                            (-> tree :maxPoints)
                            data)
             (q-contains? (-> child-nodes :southWest :boundary) point)
             (QuadTreeNode. (-> tree :boundary) []
                            (-> child-nodes :northWest)
                            (-> child-nodes :northEast)
                            (insert (-> child-nodes :southWest) point)
                            (-> child-nodes :southEast)
                            (-> tree :maxPoints)
                            data)
             :else
             (QuadTreeNode. (-> tree :boundary) []
                            (-> child-nodes :northWest)
                            (-> child-nodes :northEast)
                            (-> child-nodes :southWest)
                            (insert (-> child-nodes :southEast) point)
                            (-> tree :maxPoints)
                            data))))))

(defn insert-points
  "Convenient function in order to insert multiple points at once."
  ([tree points]
   (insert-points tree points nil))
  ([tree points data]
   (reduce #(insert %1 %2 data) tree points)))

;; querying

(defn query
  "Returns all values which are contained in the given bounding box."
  [node bounding-box]
  (cond (not (q-intersects? (-> node :boundary) bounding-box))
        []
        (not (leaf? node))
        (concat (query (-> node :northWest) bounding-box)
                (query (-> node :northEast) bounding-box)
                (query (-> node :southWest) bounding-box)
                (query (-> node :southEast) bounding-box))
        :else
        (filter (fn [point]
                  (q-contains? bounding-box point)) (-> node :points))))

(defn delete
  "Returns a quadtree without points which match the given predicate
  function. Not considering geo-spatial index. Hence deletion has O(n)
  complexity."
  [node predicate-fn]
  (cond (leaf? node)
        (QuadTreeNode. (-> node :boundary)
                       (remove predicate-fn (-> node :points))
                       nil nil nil nil (-> node :maxPoints)
                       (-> node :data))
        :else (QuadTreeNode. (-> node :boundary)
                             []
                             (delete (-> node :northWest) predicate-fn)
                             (delete (-> node :northEast) predicate-fn)
                             (delete (-> node :southWest) predicate-fn)
                             (delete (-> node :southEast) predicate-fn)
                             (-> node :maxPoints)
                             (-> node :data))))

;; other functions

(defn number-of-nodes
  "Returns the number of all nodes in the given quadtree."
  [node]
  (cond (leaf? node) 1
        :else (+ 1
                 (number-of-nodes (-> node :northWest))
                 (number-of-nodes (-> node :northEast))
                 (number-of-nodes (-> node :southWest))
                 (number-of-nodes (-> node :southEast)))))

(defn all-values
  "Returns all values of the given quadtree node recusivly (include
  all child nodes)"
  [node]
  (cond
    (leaf? node) (-> node :points)
    :else (concat (all-values (-> node :northWest))
                  (all-values (-> node :northEast))
                  (all-values (-> node :southWest))
                  (all-values (-> node :southEast)))))

(defn all-boundries
  "Returns all boundries of the given quadtree node recusivly (include
  all child nodes)"
  [node]
  (cond
    (leaf? node) (-> node :boundary)
    :else (flatten (list (all-boundries (-> node :northWest))
                         (all-boundries (-> node :northEast))
                         (all-boundries (-> node :southWest))
                         (all-boundries (-> node :southEast))))))