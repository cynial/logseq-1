(ns frontend.modules.outliner.pipeline
  (:require [frontend.modules.datascript-report.core :as ds-report]
            [frontend.modules.outliner.file :as file]
            [frontend.db :as db]
            [frontend.util :as util]
            [frontend.debug :as debug]))

(defn updated-page-hook
  [page]
  (let [page (db/entity (:db/id page))
        path (:file/path (:block/file page))
        page-title (or (:block/original-name page)
                       (:block/name page))]
    (when (util/electron?)
      (debug/set-ack-step! path :start-writing)
      (debug/wait-for-write-ack! page-title path)))
  (file/sync-to-file page))

(defn invoke-hooks
  [tx-report]
  (let [{:keys [pages]} (ds-report/get-blocks-and-pages tx-report)]
    (doseq [p (seq pages)] (updated-page-hook p))
    ;; TODO: Add blocks to hooks
    #_(doseq [b (seq blocks)] )))
