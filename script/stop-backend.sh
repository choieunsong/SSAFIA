#!/usr/bin/env bash
BASEDIR=$(dirname "$0")
cd "$BASEDIR"/..
docker-compose -f docker-compose-for-front.yml down

