#!/bin/bash

TRADER_BID_ID=$1

if ! [[ -z $TRADER_BID_ID ]]; then
 curl \
    --header "Content-type: application/json" \
    --header "X-ADMIN-TOKEN: development-use-only" \
    --request DELETE \
    server.test.agrity.net/admin/traderBids/$TRADER_BID_ID
  echo # Insert Blank Line
else 
  echo "ERROR: Please provide a bid id to delete."
fi
