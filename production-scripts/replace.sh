#!/bin/bash

FIND_TERM=$1
REPLACE_TERM=$2

if [[ -z $FIND_TERM ]]; then
  echo "ERROR: Please give find term."
  exit
fi

if [[ -z $REPLACE_TERM ]]; then
  echo "ERROR: Please give replace term."
  exit
fi


find . -type f -exec sed -i "" "s/$FIND_TERM/$REPLACE_TERM/g" {} \;
