#!/bin/bash

if [[ -z $1 ]]; then
  echo "Creating Grower..."
  echo # Insert Blank Line

  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data '{ "handler_id" : 1, "first_name" : "Jack", "last_name" : "McCarthy", "email_addresses" :  [ "jackmmc@stanford.edu" ] }' \
    server.test.agrity.net/growers
else
  echo "ERROR: do not use any arguements."
fi


