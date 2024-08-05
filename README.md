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

* Add support for llvm coverage
* Add docs-as-code (Sphinx?)
* Add pipelines
  * GitHub: docs/format/build/test
  * GitLab: docs/format/build/test/coverage
  * Jenkins: docs/format/todo|fixme|hack/build/test/coverage
