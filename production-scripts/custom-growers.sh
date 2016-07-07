#!/bin/bash

if ! [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request POST \
    --data "{ \"handler_id\" : $1 , \"first_name\" : \"Jack\", \"last_name\" : \"McCarthy\", \"email_addresses\" :  [\"jackmcc@stanford.edu\"], \"phone_numbers\" : [\"3174450448\"] }" \
    localhost:9000/admin/growers

  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request POST \
    --data "{ \"handler_id\" : $1 , \"first_name\" : \"Larsen\", \"last_name\" : \"Jensen\", \"email_addresses\" :  [\"larsenj@stanford.edu\"], \"phone_numbers\" : [\"2132680235\"] }" \
    localhost:9000/admin/growers

  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request POST \
    --data "{ \"handler_id\" : $1 , \"first_name\" : \"Ryan\", \"last_name\" : \"Davies\", \"email_addresses\" :  [\"ryscot@gmail.com\"], \"phone_numbers\" : [\"5592702013\"] }" \
    localhost:9000/admin/growers
else
  echo "ERROR: Please give handler id."
fi


