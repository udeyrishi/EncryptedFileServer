/**
 Copyright 2016 Udey Rishi
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

#include "com_udeyrishi_encryptedfileserver_common_tea_TEANative.h"

#define DELTA (long)0x9e3779b9


// v has size 2, k has size 4
// k is read only, v's contents will be swapped with the result.
void encrypt (long *v, long *k);
void decrypt (long *v, long *k);

JNIEXPORT void JNICALL Java_com_udeyrishi_encryptedfileserver_common_tea_TEANative_encrypt
  (JNIEnv *env, jobject object, jbyteArray data, jlongArray key) {
    jbyte *data_c = (*env)->GetByteArrayElements(env, data, 0);
    jlong *data_c_key = (*env)->GetLongArrayElements(env, key, 0);

    // Safe cast, because proper multiples
    // jlong is just an alias for long
    encrypt((long *)data_c, (long *)data_c_key);
    (*env)->ReleaseByteArrayElements(env, data, data_c, 0);

  }

/*
 * Class:     com_udeyrishi_encryptedfileserver_common_tea_TEANative
 * Method:    decrypt
 * Signature: ([B[J)V
 */
JNIEXPORT void JNICALL Java_com_udeyrishi_encryptedfileserver_common_tea_TEANative_decrypt
  (JNIEnv *env, jobject object, jbyteArray data, jlongArray key) {
    jsize length_data = (*env)->GetArrayLength(env, data);
        jbyte *data_c = (*env)->GetByteArrayElements(env, data, 0);
        jlong *key_c = (*env)->GetLongArrayElements(env, key, 0);

        // Safe cast, because proper multiples
        // jlong is just an alias for long
        decrypt((long *)data_c, (long *)key_c);
        (*env)->ReleaseByteArrayElements(env, data, data_c, 0);
}



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

