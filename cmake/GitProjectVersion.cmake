find_package(Git)

if (Git_FOUND)
    # Helper function to execute Git commands
    function(execute_git)
        execute_process(
            COMMAND "${GIT_EXECUTABLE}" ${ARGV}
            WORKING_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}"
            RESULT_VARIABLE GIT_RESULT
            OUTPUT_VARIABLE GIT_STDOUT
            ERROR_VARIABLE GIT_STDERR
        )
        if( NOT ${GIT_RESULT} EQUAL 0 )
            message(FATAL_ERROR
                "Failed to execute: '${GIT_EXECUTABLE} ${ARGV}' => ${GIT_STDERR}"
            )
        endif()
        # Set function return value
        set(GIT_EXECUTE_STDOUT ${GIT_STDOUT} PARENT_SCOPE)
    endfunction()

    # Set defaul project version
    set(GIT_PROJECT_VERSION 0.0.0)

    # Make sure tags are available, otherwise 'describe --tags' will fail
    execute_git(tag)
    if( NOT "${GIT_EXECUTE_STDOUT}" STREQUAL "" )
        # Get Git base tag and offset
        execute_git(describe --tags)
        set(GIT_TAG_DESCRIBE ${GIT_EXECUTE_STDOUT})
        # Get latest git tag
        execute_git(describe --tags --abbrev=0)
        set(GIT_TAG_LATEST ${GIT_EXECUTE_STDOUT})

        # Set project version to git tag if we are on a git tag
        if( "${GIT_TAG_DESCRIBE}" STREQUAL "${GIT_TAG_LATEST}")
            if( ${GIT_TAG_LATEST} MATCHES "([0-9]+)\.([0-9]+)\.([0-9]+)" )
                set(GIT_PROJECT_VERSION_MAJOR "${CMAKE_MATCH_1}")
                set(GIT_PROJECT_VERSION_MINOR "${CMAKE_MATCH_2}")
                set(GIT_PROJECT_VERSION_PATCH "${CMAKE_MATCH_3}")
                set(GIT_PROJECT_VERSION ${GIT_PROJECT_VERSION_MAJOR}.${GIT_PROJECT_VERSION_MINOR}.${GIT_PROJECT_VERSION_PATH})
            else()
                message(FATAL_ERROR
                    "Failed to parse version tag: ${GIT_TAG_LATEST}"
                )
            endif()
        endif()
    endif()
    message(STATUS
        "Project version = ${GIT_PROJECT_VERSION}"
    )
endif()
