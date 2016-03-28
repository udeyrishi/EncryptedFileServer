#pragma once

// v has size 2, k has size 4
// k is read only, v's contents will be swapped with the result.
void encrypt (long *v, long *k);
void decrypt (long *v, long *k);