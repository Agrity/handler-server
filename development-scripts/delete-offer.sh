#!/bin/bash

OFFER_ID=$1

if ! [[ -z $OFFER_ID ]]; then
 curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request DELETE \ 
    localhost:9000/admin/offers/:$OFFER_ID

else 
  echo "ERROR: Please provide an offer id to delete."
fi