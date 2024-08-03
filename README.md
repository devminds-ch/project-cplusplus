# [devminds](https://devminds.ch) C++ Project Template

## Directory structure

```
├── build       Reserved folder for build artifacts
├── cmake       Custom CMake files
├── docs        Documentation folder
├── external    3rdparty libraries as Git submodules
├── src         Source code
├── tests       Tests
└── tools       Scripts
```

## TODO

* Add dummy project incl. submodule (e.g. Eigen)
* Add devcontainer
* Add GoogleTest via CMake
* Add Git semver
* Add clang-format
* Add docs-as-code (Sphinx?)
* Add pipelines
  * GitHub: docs/format/build/test
  * GitLab: docs/format/build/test/coverage
  * Jenkins: docs/format/todo|fixme|hack/build/test/coverage
