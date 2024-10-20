# C++ Training Project by [devminds GmbH](https://devminds.ch)

This C++ Project is used for DevOps CI/CD trainings.

The project contains an application providing a CLI to calculate the sum of two numbers:

```bash
C++ Training Project
Usage: ./cplusplus_training_project [OPTIONS] SUBCOMMAND

Options:
  -h,--help                   Print this help message and exit

Subcommands:
  sum                         Sum two doubles
```


## Toolchain

The C++ application is based on the following toolchain:

* [CMake](https://cmake.org/) as build system
* [doxygen](https://doxygen.ln/) for documentation
* [GCC](https://gcc.gnu.org/) and [Clang](https://clang.llvm.org/) for compilation
* [GoogleTest](https://github.com/google/googletest) for test creation and execution
* [ClangFormat](https://clang.llvm.org/docs/ClangFormat.html) for code formatting
* [Clang-Tidy](https://clang.llvm.org/extra/clang-tidy/) for static code analysis
* [Cppcheck](https://cppcheck.sourceforge.io/) for static code analysis
* [include-what-you-use](https://include-what-you-use.org/) for #include analysis
* [gcovr](https://gcovr.com/en/stable/) for code coverage processing


## Directory structure

```
├── build       Reserved folder for build artifacts
├── cmake       Custom CMake files
├── docs        Doxygen documentation
├── external    3rdparty libraries as Git submodules
├── src         C++ source code
├── tests       GoogleTest tests
└── tools       Scripts
```


## Build and test instructions

All steps required to build or test the application are wrapped in separate shell scripts.

Check the content of the corresponding scripts for details.

### Build documentation

Build the doxygen documentation:

```bash
./tools/build-docs.sh
```

### Run clang-format

Run clang-format:

```bash
./tools/clang-format.sh
```

### Run static code analysis

**IMPORTANT:** the following tools are automatically executed using CMake during project compilation:

* [include-what-you-use](https://include-what-you-use.org/)
* [Clang-Tidy](https://clang.llvm.org/extra/clang-tidy/)
* [Cppcheck](https://cppcheck.sourceforge.io/)

If Cppcheck should be executed manually, run the following:

```bash
cppcheck src/ --xml --xml-version=2 2> cppcheck.xml
```

### Instructions for GCC

Build the application `release` config:

```bash
./tools/build-cmake-target.sh gcc-release cplusplus_training_project
```

Build the tests for code coverage analysis:

```bash
./tools/build-cmake-target.sh gcc-coverage calculate_test
```

Execute the tests:

```bash
./tools/run-test.sh build/gcc-coverage/bin/calculate_test build/gcc
```

Executing the tests will create the following artifacts:

* `build/gcc/test-report.xml`: test results in Junit XML format
* `build/gcc/test-coverage.xml`: code coverage report in Cobertura XML format
* `build/gcc/html/index.html`: HTML report of code coverage from gcovr


### Instructions for Clang


Build the application `release` config:

```bash
./tools/build-cmake-target.sh clang-release cplusplus_training_project
```

Build the tests for code coverage analysis:

```bash
./tools/build-cmake-target.sh clang-coverage calculate_test
```

Execute the tests:

```bash
./tools/run-test.sh build/clang-coverage/bin/calculate_test build/clang
```

Executing the tests will create the following artifacts:

* `build/clang/test-report.xml`: test results in Junit XML format
* `build/clang/test-coverage.xml`: code coverage report in Cobertura XML format
* `build/clang/html/index.html`: HTML report of code coverage from gcovr
