#!/bin/bash

ID=$1

if ! [[ -z $ID ]]; then
  curl \
    --header 'X-AUTH-TOKEN : "fb5f5b0a-b7df-4679-abac-7629b5ed1041"' \
    --request GET \
    localhost:9000/handlers/$ID

else
  echo "ERROR: Please provide a handler id to create."
fi


