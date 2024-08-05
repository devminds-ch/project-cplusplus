#include "lib/calculate.hpp"

#include <gtest/gtest.h>

TEST(CalculateTest, Sum) {
    EXPECT_DOUBLE_EQ(2, Calculate::sum(1, 1));
    EXPECT_DOUBLE_EQ(3, Calculate::sum(1, 2));
    EXPECT_DOUBLE_EQ(3, Calculate::sum(2, 1));
    EXPECT_DOUBLE_EQ(4, Calculate::sum(2, 2));
}

TEST(CalculateTest, Subtract) {
    EXPECT_DOUBLE_EQ(0, Calculate::subtract(1, 1));
    EXPECT_DOUBLE_EQ(-1, Calculate::subtract(1, 2));
    EXPECT_DOUBLE_EQ(1, Calculate::subtract(2, 1));
    EXPECT_DOUBLE_EQ(0, Calculate::subtract(2, 2));
}

TEST(CalculateTest, Multiply) {
    EXPECT_DOUBLE_EQ(1, Calculate::multiply(1, 1));
    EXPECT_DOUBLE_EQ(2, Calculate::multiply(1, 2));
    EXPECT_DOUBLE_EQ(2, Calculate::multiply(2, 1));
    EXPECT_DOUBLE_EQ(4, Calculate::multiply(2, 2));
}

TEST(CalculateTest, Divide) {
    EXPECT_DOUBLE_EQ(1, Calculate::divide(1, 1));
    EXPECT_DOUBLE_EQ(0.5, Calculate::divide(1, 2));
    EXPECT_DOUBLE_EQ(2, Calculate::divide(2, 1));
    EXPECT_DOUBLE_EQ(1, Calculate::divide(2, 2));
}
