#!/bin/bash

COMPANY_NAME=$1
EMAIL_ADDRESS=$2

if ! [[ -z $COMPANY_NAME ]]; then
  if ! [[ -z $EMAIL_ADDRESS ]]; then
    curl \
      --header "Content-type: application/json" \
      --request POST \
      --data "{ \"company_name\" : \"$COMPANY_NAME\", \"email_address\" : \"$EMAIL_ADDRESS\", \"password\" : \"dummy_password\" }" \
      localhost:9000/handlers

  else
    echo "ERROR: Please provide a email address to create."
  fi

else
  echo "ERROR: Please provide a company name to create."
fi


