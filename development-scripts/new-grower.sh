#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "F_NAME", "last_name" : "L_NAME", "email_address" : "rdavies8@stanford.edu", "phone_number" : "+18155926350"}' \
    localhost:9000/admin/growers
else
  echo "ERROR: do not use any arguements."
fi


