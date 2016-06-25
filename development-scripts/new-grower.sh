#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "F_NAME", "last_name" : "L_NAME", "email_addresses" :  [ "ryscot@gmail.com" ], "phone_numbers" : [ "+18155926350" ] }' \
    localhost:9000/growers
else
  echo "ERROR: do not use any arguements."
fi


