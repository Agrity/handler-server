#!/bin/bash

if [[ -z $1 ]]; then
  echo "Creating Grower..."
  echo # Insert Blank Line

  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data "{ \"handler_id\" : 1, \"grower_ids\" : [1, 2], \"almond_variety\" : \"NP\", \"almond_pounds\" :  100, \"payment_date\" : \"testing\" }" \
    localhost:9000/offers
else
  echo "ERROR: do not use any arguements."
fi


