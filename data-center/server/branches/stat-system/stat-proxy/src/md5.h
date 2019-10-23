#include <stdint.h>

typedef struct {
	uint32_t state[4];      
	uint32_t count[2];       
	unsigned char buffer[64];
} MD5_CTX;

void MD5Init(MD5_CTX *);
void MD5Update(MD5_CTX *, unsigned char *, unsigned int);
void MD5Final(unsigned char [16], MD5_CTX *);

