#!/bin/bash

EMAIL_ADDRESS=$1
PASSWORD=$2

if ! [[ -z $EMAIL_ADDRESS ]]; then
  if [[ -z $PASSWORD ]]; then
    PASSWORD="dummy_password"
  fi
  curl \
    --header "Content-type: application/json" \
    --request POST \
    --data "{ \"email_address\" : \"$EMAIL_ADDRESS\", \"password\" : \"$PASSWORD\" }" \
    server.agrity.net/handler/login

else
  echo "ERROR: Please provide a email address."
fi


