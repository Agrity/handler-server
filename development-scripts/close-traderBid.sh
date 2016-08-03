#!/bin/bash

TRADER_BID_ID=$1

if ! [[ -z $TRADER_BID_ID ]]; then
  curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request GET \
    localhost:9000/admin/traderBids/$TRADER_BID_ID/close
  echo # Insert Blank Line
else
  echo "ERROR: Please provide a traderBid id to close."
fi
