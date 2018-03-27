#!/bin/sh
echo 'Shutting down services'
docker-compose -f src/test/resources/docker-compose.yml down