#!/bin/bash

# Save the current directory
pushd .

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

cd "$REPO_ROOT" || exit 1

# Build the project
mkdir -p build
cd build || exit 1
cmake ..
make cplusplus_training_project

# Restore the directory
popd || exit 1

