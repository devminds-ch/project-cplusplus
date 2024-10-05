# https://github.com/devcontainers/images/tree/main/src/cpp
FROM mcr.microsoft.com/devcontainers/cpp:1-debian-12

# [Optional] Uncomment this section to install additional packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
        clang-format \
        clang-tidy \
        doxygen \
        gcovr \
        graphviz \
        iwyu \
    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*
