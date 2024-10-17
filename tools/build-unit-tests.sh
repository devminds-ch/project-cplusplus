#!/bin/bash

# Save the current directory
pushd .

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

cd "$REPO_ROOT" || exit 1

# Build the project
mkdir -p build
cmake --version
cmake --preset gcc-coverage
cmake --build --preset gcc-coverage --target calculate_test

# Restore the directory
popd || exit 1

