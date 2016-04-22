#!/bin/bash

if [[ -z $1 ]]; then
  echo "Creating Grower..."
  echo # Insert Blank Line

  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "F_NAME", "last_name" : "L_NAME", "email_addresses" :  [ "ryscot@gmail.com" ] }' \
    localhost:9000/growers
else
  echo "ERROR: do not use any arguements."
fi


