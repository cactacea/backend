#!/usr/bin/env bash

docker run -d -p 8000:9000 -e MINIO_ACCESS_KEY=${AWS_ACCESS_KEY_ID} -e MINIO_SECRET_KEY=${AWS_SECRET_ACCESS_KEY} minio/minio server /data

curl https://dl.minio.io/client/mc/release/linux-amd64/mc -o ./mc
chmod +x ./mc

./mc config host add minio http://127.0.0.1:8000 ${AWS_ACCESS_KEY_ID} ${AWS_SECRET_ACCESS_KEY}
./mc mb minio/${S3_BUCKET_NAME}
./mc policy public minio/${S3_BUCKET_NAME}