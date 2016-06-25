#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "Brian", "last_name" : "Rossi", "email_addresses" :  [ "brossi15621@gmail.com" ], "PHONE_NUMBERS" : [ "+18155926350" ] }' \
    radiant-cove-44181.herokuapp.com/growers
else
  echo "ERROR: do not use any arguements."
fi