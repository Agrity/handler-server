#!/bin/bash

COMPANY_NAME=$1
FIRST_NAME=$2
LAST_NAME=$3
PHONE_NUMBER=$4
EMAIL_ADDRESS=$5
PASSWORD=$6

if [[ -z $COMPANY_NAME ]]; then
  echo "ERROR: Please provide a company name to create."
  exit
fi

if [[ -z $FIRST_NAME ]]; then
  echo "ERROR: Please provide a first name to create."
  exit
fi

if [[ -z $LAST_NAME ]]; then
  echo "ERROR: Please provide a last name to create."
  exit
fi

if [[ -z $PHONE_NUMBER ]]; then
  echo "ERROR: Please provide a phone number to create."
  exit
fi

if [[ -z $EMAIL_ADDRESS ]]; then
  echo "ERROR: Please provide an email address to create."
  exit
fi

if [[ -z $PASSWORD ]]; then
  echo "ERROR: Please provide a password to create."
  exit
fi

    curl \
      --header "Content-type: application/json" \
      --header "X-ADMIN-TOKEN: development-use-only" \
      --request POST \
      --data "{ \"company_name\" : \"$COMPANY_NAME\", \"first_name\" : \"$FIRST_NAME\", \"last_name\" : \"$LAST_NAME\", \"email_address\" : \"$EMAIL_ADDRESS\", \"phone_number\" : \"$PHONE_NUMBER\", \"password\" : \"$PASSWORD\" }" \
      server.agrity.net/admin/handlers
