FROM linuxserver/openssh-server:latest

RUN apk add --no-cache git

ARG USER_NAME
ARG PUBLIC_KEY
ARG REPO_PATH

ENV USER_NAME=${USER_NAME}
ENV PUBLIC_KEY=${PUBLIC_KEY}
ENV SUDO_ACCESS=true

RUN mkdir -p /config/${REPO_PATH} && \
    git init --bare /config/${REPO_PATH} && \
    chown -R 1000:1000 /config/

EXPOSE 2222