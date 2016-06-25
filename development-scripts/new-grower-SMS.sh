#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "Brian", "Rossi" : "L_NAME", "email_addresses" :  [ "brossi15621@gmail.com" ], "phone_numbers" : [ "+18155926350" ] }' \
    radiant-cove-44181.herokuapp.com/growers
else
  echo "ERROR: do not use any arguements."
fi