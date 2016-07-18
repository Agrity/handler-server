#!/bin/bash

TRADER_BID_ID=$1

if ! [[ -z $TRADER_BID_ID ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request PUT \
    --data '{ "trader_id" : 1, "handlerSeller_ids" : [1], "almond_variety" : "NP", "almond_pounds" :  500000,  "price_per_pound" : "2.15", "management_type" : { "type" : "FCFSService", "delay" : 10}, "comment" : "This is an awesome test comment that was changed!", "email_address" : "ryscot@gmail.com" }' \
    localhost:9000/admin/traderBids/$TRADER_BID_ID
  echo # Insert Blank Line
else
  echo "ERROR: Please provide a handler bid id to update."
fi
