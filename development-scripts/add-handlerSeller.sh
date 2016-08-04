#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request PUT \
    --data '[2]' \
    localhost:9000/admin/traderBids/1/addHandlerSellers
else
  echo "ERROR: do not use any arguements."
fi