#!/bin/bash

if [[ -z $1 ]]; then
  echo "Creating Grower..."
  echo # Insert Blank Line

  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "Larsen", "last_name" : "Jensen", "email_addresses" :  [ "larsenj@stanford.edu" ] }' \
    localhost:9000/growers
else
  echo "ERROR: do not use any arguements."
fi


