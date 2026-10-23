#!/bin/sh
# Minimal gradlew — uses system gradle if available, otherwise gradle from setup-gradle
exec gradle "$@"
