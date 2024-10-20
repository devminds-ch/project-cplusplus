#!/bin/bash -e

# Check if test binary path is specified
if [ -z "$1" ]; then
    echo "ERROR: no test binary path specified"
    exit 1
fi
# Check if output directory is specified
if [ -z "$2" ]; then
    echo "ERROR: no output directory specified"
    exit 1
fi

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

# Check if binary was compiled using clang - otherwise we assume GCC
GCOV_EXECUTABLE=gcov
if objdump --full-contents --section .comment $1 | grep -q "clang"; then
    GCOV_EXECUTABLE="llvm-cov gcov"
fi

mkdir -p "$2/html"
# Execute test binary
./$1 --gtest_output="xml:$2/test-report.xml"
# Create Cobertura coverage report
gcovr --gcov-executable "${GCOV_EXECUTABLE}" -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o "$2/test-coverage.xml"
# Create HTML coverage report
gcovr --gcov-executable "${GCOV_EXECUTABLE}" -f src . --root ./ --exclude-unreachable-branches --html --html-details -o "$2/html/index.html"

popd 2>&1 > /dev/null

exit $STATUS
