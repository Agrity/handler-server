#!/bin/bash

HANDLER_BID_ID=$1

if ! [[ -z $HANDLER_BID_ID ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request PUT \
    --data '{ "handler_id" : 1, "grower_ids" : [1], "almond_variety" : "NP", "almond_size" : "23/25", "almond_pounds" :  500000,  "price_per_pound" : "2.15", "start_payment_date" : "June 2015", "end_payment_date" : "June 2016", "management_type" : { "type" : "FCFSService", "delay" : 10}, "comment" : "This is an awesome test comment that was changed!", "email_addresses" : "ryscot@gmail.com" }' \
    localhost:9000/admin/bids/$HANDLER_BID_ID
  echo # Insert Blank Line
else
  echo "ERROR: Please provide an bid id to update."
fi
