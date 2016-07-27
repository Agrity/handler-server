#!/bin/bash

ID=$1

if ! [[ -z $ID ]]; then
  curl \
    --header 'X-AUTH-TOKEN : deca2584-0bea-44ad-a420-af90fefba1b0' \
    --request GET \
    server.agrity.net/traders/$ID

else
  echo "ERROR: Please provide a trader id to create."
fi
