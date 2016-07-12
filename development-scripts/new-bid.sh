#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request POST \
    --data '{ "handler_id" : 1, "grower_ids" : [1], "almond_variety" : "NP", "almond_size" : "23/25", "almond_pounds" :  100000,  "price_per_pound" : "2.23", "start_payment_date" : "June 2015", "end_payment_date" : "June 2016", "management_type" : { "type" : "FCFSService", "delay" : 10}, "comment" : "This is an awesome test comment", "email_addresses" : "ryscot@gmail.com" }' \
    localhost:9000/admin/handlerBids
  echo # Insert Blank Line
else
  echo "ERROR: do not use any arguements."
fi


