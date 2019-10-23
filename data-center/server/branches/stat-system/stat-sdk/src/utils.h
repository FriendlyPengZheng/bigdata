#ifndef STATSDK_UTILS_H_
#define STATSDK_UTILS_H_

#include <stdint.h>

#define YYMMDD      (0)
#define YYMMDDHHII  (1)
#define YYYYMMDD    (2)

uint32_t get_string_time(uint32_t timestamp, uint8_t type);
uint32_t get_time_of_string(uint32_t time);

#endif // STATSDK_UTILS_H_

