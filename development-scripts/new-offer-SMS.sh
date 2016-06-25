#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "grower_ids" : [1], "almond_variety" : "NP", "almond_pounds" :  100000,  "price_per_pound" : "2.23", "payment_date" : "testing", "management_type" : { "type" : "FCFSService", "delay" : 10}, "comment" : "This is an awesome test comment", "email_addresses" : "brossi15621@gmail.com" }' \
    radiant-cove-44181.herokuapp.com/offers
  echo # Insert Blank Line
else
  echo "ERROR: do not use any arguements."
fi
