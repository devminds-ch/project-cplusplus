include(FetchContent)
FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/609281088cfefc76f9d0ce82e1ff6c30cc3591e5.zip
)
# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

# Setup code coverage for GCC
if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU")
  message(STATUS "Enabling code coverage for gcc")
  add_compile_options("--coverage")
  set(GCOV_LINK gcov)
endif()

add_executable(${PROJECT_NAME}_GoogleTest
  ../src/calculate.cpp
  calculate_test.cpp
)
target_link_libraries(${PROJECT_NAME}_GoogleTest PRIVATE
  ${GCOV_LINK}
  gtest
  gmock
  gmock_main
)

include(GoogleTest)
gtest_discover_tests(${PROJECT_NAME}_GoogleTest)
