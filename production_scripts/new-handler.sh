#!/bin/bash

COMPANY_NAME=$1
EMAIL_ADDRESS=$2
PASSWORD=$3

if ! [[ -z $COMPANY_NAME ]]; then
  if ! [[ -z $EMAIL_ADDRESS ]]; then

    if [[ -z $PASSWORD ]]; then
      PASSWORD="dummy_password"
    fi

    curl \
      --header "Content-type: application/json" \
      --header "X-ADMIN-TOKEN: development-use-only" \
      --request POST \
      --data "{ \"company_name\" : \"$COMPANY_NAME\", \"email_address\" : \"$EMAIL_ADDRESS\", \"password\" : \"$PASSWORD\" }" \
      http://server.agrity.net/admin/handlers

  else
    echo "ERROR: Please provide a email address to create."
  fi

else
  echo "ERROR: Please provide a company name to create."
fi


