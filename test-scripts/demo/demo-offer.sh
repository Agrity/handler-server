#!/bin/bash

if [[ -z $1 ]]; then
  echo "Creating Offer..."
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "grower_ids" : [1, 2], "almond_variety" : "NP", "almond_pounds" :  88000,  "price_per_pound" : "2.23", "payment_date" : "testing", "comment" : "This is a good offer fucking jackasses" }' \
    server.test.agrity.net/offers
  echo # Insert Blank Line
else
  echo "ERROR: do not use any arguements."
fi


