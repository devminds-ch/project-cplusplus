cmake_minimum_required(VERSION 3.14)

# Add custom CMake files
set(CMAKE_MODULE_PATH ${CMAKE_MODULE_PATH} ${CMAKE_SOURCE_DIR}/cmake)
include(GitProjectVersion)

project(demo)

# GoogleTest requires at least C++14
set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_subdirectory(src)

# Enable testing
enable_testing()
add_subdirectory(tests)