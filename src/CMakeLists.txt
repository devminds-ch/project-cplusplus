add_library(calculate
    lib/calculate.cpp
)
target_include_directories(calculate
    PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}
)

add_executable(${PROJECT_NAME}
    main.cpp
)
target_link_libraries(${PROJECT_NAME}
    PRIVATE calculate
)
