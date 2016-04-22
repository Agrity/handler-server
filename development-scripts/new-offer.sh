#!/bin/bash

if [[ -z $1 ]]; then
  echo "Creating Offer..."
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "grower_ids" : [1], "almond_variety" : "NP", "almond_pounds" :  100,  "price_per_pound" : "2.23", "payment_date" : "testing" }' \
    localhost:9000/offers
  echo # Insert Blank Line
else
  echo "ERROR: do not use any arguements."
fi


