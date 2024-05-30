#!/bin/bash

# save current IFS value
oldIFS="$IFS"

# Change IFS to split by comma
IFS=","

# Split the string and iterate over each part
for CONNECTOR_NAME in $CONNECTOR_NAMES; do
  deploy_payload=$(jq -s '.[0] * .[1] * .[2] * .[3]' /setup/connectors/cdc/config.json /setup/connectors/cdc/$CONNECTOR_NAME/config.json /setup/connectors/cdc/$CLUSTER_ENV.json /setup/connectors/cdc/$CONNECTOR_NAME/$CLUSTER_ENV.json)
  curl -f -X POST $CONNECT_CLUSTER_HOST:8083/connectors -H "Content-Type: application/json" -d "$deploy_payload"
  echo "$CONNECTOR_NAME deployed"
done

IFS="$oldIFS"
