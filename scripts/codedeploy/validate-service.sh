#!/bin/bash

if [[ $(docker inspect --format="{{ .State.Running }}" babbage) == "false" ]]; then
  exit 1;
fi
