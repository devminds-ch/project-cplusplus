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

Build the doxygen documentation:

```bash
cd docs
doxygen
```

Execute static code analysis:

```bash
./tools/clang-format.sh
cppcheck src/ --xml --xml-version=2 2> cppcheck.xml
```


### Instructions for GCC

Build the application `release` config:

```bash
cmake --preset gcc-release
cmake --preset gcc-release --build --target cplusplus_training_project
```

Build the tests for code coverage analysis:

```bash
cmake --preset gcc-coverage
cmake --preset gcc-coverage --build --target calculate_test
```

Execute the tests:

```bash
./build/gcc-coverage/bin/calculate_test --gtest_output="xml:test-report-gcc.xml"
```

Convert the code coverage to Cobertura format:

```bash
gcovr -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o "coverage-gcc.xml"
```

Create an HTML report of the code coverage:

```bash
mkdir -p build/gcov-html-gcc
gcovr -f src . --root ./ --exclude-unreachable-branches --html --html-details -o "build/gcov-html-gcc/index.html"
```


### Instructions for Clang

Build the application `release` config:

```bash
cmake --preset clang-release
cmake --preset clang-release --build --target cplusplus_training_project
```


Build the tests for code coverage analysis:

```bash
cmake --preset clang-coverage
cmake --preset clang-coverage --build --target calculate_test
```

Execute the tests:

```bash
./build/clang-coverage/bin/calculate_test --gtest_output="xml:test-report-clang.xml"
```

Convert the code coverage to Cobertura format:

```bash
gcovr --gcov-executable "llvm-cov gcov" -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o "coverage-clang.xml"
```

Create an HTML report of the code coverage:

```bash
mkdir -p build/gcov-html-clang
gcovr --gcov-executable "llvm-cov gcov" -f src . --root ./ --exclude-unreachable-branches --html --html-details -o "build/gcov-html-clang/index.html"
```
