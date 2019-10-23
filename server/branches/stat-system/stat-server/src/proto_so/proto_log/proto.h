/**
 * Copyright (c) 2008 - 2012 TaoMee Inc. All Rights Reserved.
 * Use of this source code is governed by 2nd Team of Back-end Development, ODD.
 * Hansel(hanzhou87@gmail.com) 2012-03-21
 * File proto.h 
 */

#ifndef _PROTO_H_
#define _PROTO_H_

#include <stdint.h>

#define STAT_LOG 0xFFF1

typedef struct {
	uint16_t msg_len;
	char content[0];
} __attribute__((__packed__)) log_msg_t;

typedef struct {
	uint16_t msg_len;
	uint16_t operation;
	uint32_t report_id;
	char content[0];
} __attribute__((__packed__)) request_msg_t;

typedef struct {
	uint16_t msg_len;
	uint16_t result;
} __attribute__((__packed__)) response_msg_t;

#endif//_PROTO_H_
