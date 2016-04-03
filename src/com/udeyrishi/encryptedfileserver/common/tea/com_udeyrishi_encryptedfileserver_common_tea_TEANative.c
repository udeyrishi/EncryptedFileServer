#include "com_udeyrishi_encryptedfileserver_common_tea_TEANative.h"
#include <assert.h>
#include <stdio.h>

JNIEXPORT void JNICALL Java_com_udeyrishi_encryptedfileserver_common_tea_TEANative_encrypt
  (JNIEnv *env, jobject object, jbyteArray data, jlongArray key) {
    jsize length_data = (*env)->GetArrayLength(env, data);
    jint *data_c = (*env)->GetIntArrayElements(env, data, 0);

    jsize length_key = (*env)->GetArrayLength(env, key);
    jlong *data_c_key = (*env)->GetLongArrayElements(env, key, 0);

    assert(length_data == 16);
    assert(length_key == 4);
    printf("encrypt called\n");
  }

/*
 * Class:     com_udeyrishi_encryptedfileserver_common_tea_TEANative
 * Method:    decrypt
 * Signature: ([B[J)V
 */
JNIEXPORT void JNICALL Java_com_udeyrishi_encryptedfileserver_common_tea_TEANative_decrypt
  (JNIEnv *env, jobject object, jbyteArray data, jlongArray key) {
    jsize length_data = (*env)->GetArrayLength(env, data);
        jint *data_c = (*env)->GetIntArrayElements(env, data, 0);

        jsize length_key = (*env)->GetArrayLength(env, key);
        jlong *data_c_key = (*env)->GetLongArrayElements(env, key, 0);

        assert(length_data == 16);
        assert(length_key == 4);
        printf("decrypt called\n");
}