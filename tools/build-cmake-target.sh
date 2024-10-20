#!/bin/bash -e

# Check if CMake preset is specified
if [ -z "$1" ]; then
    echo "ERROR: no CMake preset specified"
    exit 1
fi
# Check if CMake target is specified
if [ -z "$2" ]; then
    echo "ERROR: no CMake target specified"
    exit 1
fi

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

mkdir -p build
echo "Building target $2 with CMake preset $1..."
cmake --preset "$1"
cmake --build --preset "$1" --target "$2"

popd 2>&1 > /dev/null
