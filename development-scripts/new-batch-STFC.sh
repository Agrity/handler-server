#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request POST \
    --data '[{ "trader_id" : 1, "handlerSeller_ids" : [1], "almond_variety" : "NP", "almond_pounds" :  100000, "almond_size" : "23/25", "price_per_pound" : "2.23", "grade" : "US Fancy", "management_type" : { "type" : "STFCService", "delay" : 10}, "comment" : "This is an awesome test comment", "email_address" : "ryscot@gmail.com" }, { "trader_id" : 1, "handlerSeller_ids" : [1], "almond_variety" : "CR", "almond_pounds" :  50000, "almond_size" : "23/25", "price_per_pound" : "2.45", "grade" : "US Fancy", "management_type" : { "type" : "STFCService", "delay" : 10}, "comment" : "This is a second awesome test comment", "email_address" : "ryscot@gmail.com" }]' \
    localhost:9000/admin/traderBids/1
  echo # Insert Blank Line
else
  echo "ERROR: do not use any arguements."
fi

