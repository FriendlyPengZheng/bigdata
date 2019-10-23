/**
 * =====================================================================================
 *       @file  main.cpp
 *      @brief  
 *
 *     Created  2014-09-25 16:41:00
 *    Revision  1.0.0.0
 *    Compiler  g++
 *     Company  TaoMee.Inc, ShangHai.
 *   Copyright  Copyright (c) 2011, TaoMee.Inc, ShangHai.
 *
 *     @author  ping, ping@taomee.com
 * =====================================================================================
 */

#include "stat_calc.hpp"

int main(int args, char *argv[]) {
    if(args == 1) {
        printf("Try `%s -h` for more information.\n", argv[0]);
        exit(0);
    }
    for(int i=0; i<args; i++) {
        if(strcmp(argv[i], "-h") == 0) {
            StatCalc::print_usage();
            exit(0);
        }
    }
    StatCalc calc;
    return calc.run(args, argv);
}
