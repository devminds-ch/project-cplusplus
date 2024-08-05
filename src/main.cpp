#include <iostream>

#include "lib/calculate.hpp"

int main(int argc, char* argv[]) {
    // TODO: parse CLI arguments using https://github.com/CLIUtils/CLI11

    std::cout << "Result = " << Calculate::sum(1, 1);
    return 0;
}
