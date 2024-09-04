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

if(CMAKE_BUILD_TYPE STREQUAL "PROFILE")
  # Setup code coverage for GCC
  if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
    message(STATUS "Enabling code coverage for gcc")
    set(CMAKE_CXX_FLAGS_PROFILE --coverage)
    set(GCOV_LINK gcov)
  endif()
endif()

add_subdirectory(external/CLI11)
add_subdirectory(src)

# Enable testing
enable_testing()
add_subdirectory(tests)
