#!/bin/bash

COMPANY_NAME=$1

if ! [[ -z $COMPANY_NAME ]]; then
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data "{\"company_name\": \"$COMPANY_NAME\"}" \
    radiant-cove-44181.herokuapp.com/handlers
else
  echo "ERROR: Please provide a company name to create."
fi