#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

STATUS=0
set +e

echo "Running clang-format..."
while read FILEPATH; do
    # Pass additional arguments to clang-format, e.g. --dry-run
    clang-format -i -style=file "$FILEPATH" --Werror $1
    if [ $? -ne 0 ]; then
        STATUS=1
    fi
done <<< "$(find ${REPO_ROOT}/src ${REPO_ROOT}/tests \( -name '*.hpp' -o -name '*.cpp' \))"

set -e
popd 2>&1 > /dev/null

exit $STATUS