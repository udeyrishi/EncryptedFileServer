#include "tea.h"

#define DELTA (long)0x9e3779b9

// TEA encryption algorithm
void encrypt (long *v, long *k) {
    unsigned long y = v[0], z = v[1], sum = 0, n = 32;

    while (n-- > 0) {
        sum += DELTA;
        y += (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
        z += (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
    }

    v[0] = y;
    v[1] = z;
}

// TEA decryption routine
void decrypt (long *v, long *k){
    unsigned long n = 32, sum = 0, y = v[0], z = v[1];

    sum = DELTA<<5;
    while (n-- > 0) {
        z -= (y<<4) + k[2] ^ y + sum ^ (y>>5) + k[3];
        y -= (z<<4) + k[0] ^ z + sum ^ (z>>5) + k[1];
        sum -= DELTA;
    }
    v[0] = y;
    v[1] = z;
}

