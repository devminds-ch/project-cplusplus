#include <iostream>

#include "CLI/App.hpp"
#include "CLI/Config.hpp"
#include "CLI/Formatter.hpp"
#include "lib/calculate.hpp"

/**
 * Main function.
 * The entry point to the CLI application.
 * @param argc Number of command line arguments.
 * @param argv Array of command line arguments.
 */
int main(int argc, char* argv[]) {
    // Parse CLI arguments using https://github.com/CLIUtils/CLI11
    CLI::App app{"C++ Training Project"};
    argv = app.ensure_utf8(argv);
    app.require_subcommand(1);

    auto sum = app.add_subcommand("sum", "Sum two doubles");
    double a;
    sum->add_option("a", a, "First number")->required();
    double b;
    sum->add_option("b", b, "Second number")->required();
    sum->callback([&]() {
        std::cout << "Sum of " << a << " and " << b << " is "
                  << Calculate::sum(a, b) << std::endl;
    });

    CLI11_PARSE(app, argc, argv);
    return 0;
}
