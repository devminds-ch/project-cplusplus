#!/bin/bash

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

# Loop over files and format them
STATUS=0
while read FILEPATH; do
    # Pass additional arguments to clang-format, e.g. --dry-run
    clang-format -i -style=file "$FILEPATH" --Werror $1
    if [ $? -ne 0 ]; then
        STATUS=1
    fi
done <<< "$(find ${REPO_ROOT}/src ${REPO_ROOT}/tests \( -name '*.hpp' -o -name '*.cpp' \))"

exit $STATUS