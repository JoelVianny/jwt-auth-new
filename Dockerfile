FROM ubuntu:latest
LABEL authors="jvmpe"

ENTRYPOINT ["top", "-b"]