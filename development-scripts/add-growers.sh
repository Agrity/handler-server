#!/bin/bash

if [[ -z $1 ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request PUT \
    --data '[2, 3]' \
    localhost:9000/admin/handlerBids/1/addGrowers
else
  echo "ERROR: do not use any arguements."
fi