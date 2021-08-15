#!/usr/bin/env bash

docker rm -f redis
docker run --name redis -p 6379:6379 -d redis:6.2.4-alpine

docker rm -f openvidu
docker run --name openvidu -p 4443:4443 -e OPENVIDU_SECRET=MY_SECRET -d openvidu/openvidu-server-kms:2.19.0
