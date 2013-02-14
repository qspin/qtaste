#!/bin/bash

pushd doc
./buildDocumentation.sh || exit 1
popd
