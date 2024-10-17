#!/bin/bash

# Save the current directory
pushd .

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

cd "$REPO_ROOT" || exit 1

# Build the documentation
mkdir -p build/docs
cd docs || exit 1
doxygen

# Restore the directory
popd || exit 1

