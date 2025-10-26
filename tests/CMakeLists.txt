# GoogleTest requires at least C++17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

include(FetchContent)
FetchContent_Declare(
  googletest
  GIT_REPOSITORY https://github.com/google/googletest.git
  # https://github.com/google/googletest/releases
  GIT_TAG        52eb8108c5bdec04579160ae17225d66034bd723 # v1.17.0
)

# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

add_executable(calculate_test
  calculate_test.cpp
)

target_link_libraries(calculate_test
  PRIVATE
    calculate
    gtest
    gmock
    gmock_main
)

include(GoogleTest)
gtest_discover_tests(calculate_test)
