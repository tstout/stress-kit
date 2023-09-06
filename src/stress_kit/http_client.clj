(ns stress-kit.http-client
  (:require [clojure.edn :as edn])
  (:import [java.net.http
            HttpClient
            HttpRequest
            HttpResponse$BodyHandlers
            HttpClient$Redirect]
           [java.net URI]))

(defn get-request
  "Generic http GET request utilizing java11 http client."
  [uri]
  (-> (HttpRequest/newBuilder)
      .GET
      (.uri (URI/create uri))
      (.setHeader "User-Agent" "Java 11+")
      #_(.followRedirects HttpClient$Redirect/ALWAYS)
      .build))

(defn  http-tx-raw
  "Transmit an http request. The response is a byte array."
  [req]
  (-> (HttpClient/newBuilder)
      (.followRedirects HttpClient$Redirect/ALWAYS)
      .build
      (.send req (HttpResponse$BodyHandlers/ofByteArray))))

(defn http-tx
  "Transmist an http request. The response is a string. See http-tx-raw if you want
   the raw byte array."
  [req]
  (-> req
      http-tx-raw
      .body
      String.))

(defn http-get
  "Perform and http GET operation. The response is a string."
  [uri]
  (-> uri
      get-request
      http-tx))

(defn http-get-edn
  "Perform an http GET operation. The response from the server is expected to a string in EDN
   format. The string rep of the EDN is deserialized into clojure data structures."
  [uri]
  (-> uri
      http-get
      edn/read-string))

(comment
  *e
  (http-get "http://localhost:8080/v1/config/account/bank.boa")

  (http-get-edn "http://localhost:8080/v1/config/account/bank.boa")

  (class (http-get-edn "http://localhost:8080/v1/config/account/bank.boa"))


  ;;
  )

