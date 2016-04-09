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

/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_udeyrishi_encryptedfileserver_common_tea_TEANative */

#ifndef _Included_com_udeyrishi_encryptedfileserver_common_tea_TEANative
#define _Included_com_udeyrishi_encryptedfileserver_common_tea_TEANative
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_udeyrishi_encryptedfileserver_common_tea_TEANative
 * Method:    encrypt
 * Signature: ([B[J)V
 */
JNIEXPORT void JNICALL Java_com_udeyrishi_encryptedfileserver_common_tea_TEANative_encrypt
  (JNIEnv *env, jobject obj, jbyteArray data, jlongArray key);

/*
 * Class:     com_udeyrishi_encryptedfileserver_common_tea_TEANative
 * Method:    decrypt
 * Signature: ([B[J)V
 */
JNIEXPORT void JNICALL Java_com_udeyrishi_encryptedfileserver_common_tea_TEANative_decrypt
  (JNIEnv *env, jobject obj, jbyteArray data, jlongArray key);

#ifdef __cplusplus
}
#endif
#endif
