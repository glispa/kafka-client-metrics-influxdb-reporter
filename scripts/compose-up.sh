#!/bin/sh
echo "Starting compose..."
docker-compose -f src/test/resources/docker-compose.yml pull
docker-compose -f src/test/resources/docker-compose.yml up -d