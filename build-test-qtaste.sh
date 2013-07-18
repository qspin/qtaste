#! /bin/bash

echo "Executing script build-test-qtaste"

# Configurations:
export DISPLAY=:99.0
Xvfb :99.0 &

# Build:
./buildAll.sh || exit 1

# Execute qtaste demo after build success:
./executeDemo.sh || exit 1

