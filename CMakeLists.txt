cmake_minimum_required(VERSION 3.14)

# Add custom CMake files
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} ${CMAKE_SOURCE_DIR}/cmake)
include(GitProjectVersion)

project(cplusplus_training_project)

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

# GoogleTest requires at least C++14
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Enable include-what-you-use (IWYU) if available
find_program(IWYU_PATH NAMES include-what-you-use)
if (IWYU_PATH)
  message(STATUS "Enabling: include-what-you-use")
  set(CMAKE_CXX_INCLUDE_WHAT_YOU_USE "${IWYU_PATH}")
endif()

# Enable link-what-you-use (LWYU)
message(STATUS "Enabling: link-what-you-use")
set(CMAKE_LINK_WHAT_YOU_USE TRUE)

# Enable cppcheck if available
find_program(CPPCHECK_PATH NAMES cppcheck)
if (CPPCHECK_PATH)
  message(STATUS "Enabling: cppcheck")
  set(CMAKE_CXX_CPPCHECK "${CPPCHECK_PATH}")
endif()

# Enable clang-tidy if available
find_program(CLANG_TIDY_PATH NAMES clang-tidy)
if (CLANG_TIDY_PATH)
  message(STATUS "Enabling: clang-tidy")
  set(CMAKE_CXX_CLANG_TIDY "${CLANG_TIDY_PATH}")
endif()

# Enable code coverage for GCC and Clang if PROFILE build type is selected
if(CMAKE_BUILD_TYPE STREQUAL "PROFILE")
  if((CMAKE_CXX_COMPILER_ID STREQUAL "GNU") OR (CMAKE_CXX_COMPILER_ID STREQUAL "Clang"))
    message(STATUS "Enabling code coverage")
    set(CMAKE_CXX_FLAGS_PROFILE --coverage)
    if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
      set(GCOV_LINK gcov)
    endif()
  endif()
endif()

add_subdirectory(external/CLI11)
add_subdirectory(src)

# Enable testing
enable_testing()
add_subdirectory(tests)
