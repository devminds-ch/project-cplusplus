#include "lib/calculate.hpp"

#include <gtest/gtest.h>

TEST(CalculateTest, Sum) {
    EXPECT_DOUBLE_EQ(2, Calculate::sum(1, 1));
    EXPECT_DOUBLE_EQ(3, Calculate::sum(1, 2));
    EXPECT_DOUBLE_EQ(3, Calculate::sum(2, 1));
    EXPECT_DOUBLE_EQ(4, Calculate::sum(2, 2));
}

TEST(CalculateTest, SumNegative) {
    EXPECT_DOUBLE_EQ(0, Calculate::sum(-1, 1));
    EXPECT_DOUBLE_EQ(0, Calculate::sum(1, -1));
    EXPECT_DOUBLE_EQ(-2, Calculate::sum(-1, -1));
}

TEST(CalculateTest, SumFail) {
    EXPECT_DOUBLE_EQ(3, Calculate::sum(1, 1));
    EXPECT_DOUBLE_EQ(4, Calculate::sum(1, 2));
    EXPECT_DOUBLE_EQ(4, Calculate::sum(2, 1));
}
