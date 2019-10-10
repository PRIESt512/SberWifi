//
// Created by pleshkanev.a.a on 2019-10-02.
//

#include "../include/iperfJava.h"
#include "../include/IperfTest.h"
#include <android/log.h>
#include "../util/utf8.h"

#define APPNAME "SberWiFi"

extern "C" JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_IperfFragment_initTempPath(JNIEnv *env, jobject thisObject) {
    jclass cls2 = env->GetObjectClass(thisObject);

    if (cls2 == nullptr) {
        __android_log_print(ANDROID_LOG_FATAL, APPNAME, "ERROR: class not found !");
    } else {
        jmethodID initContext = env->GetMethodID(cls2, "getTempPath", "()Ljava/lang/String;");
        if (initContext != nullptr) {
            auto path = env->CallObjectMethod(thisObject, initContext);
            tempPath = const_cast<char *>(env->GetStringUTFChars(static_cast<jstring>(path),
                                                                 nullptr));

            __android_log_print(ANDROID_LOG_INFO, APPNAME, "Nice!");
        } else {
            __android_log_print(ANDROID_LOG_FATAL, APPNAME, "ERROR: getTempPath not found !");
        }
    }
}

extern "C" JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_IperfFragment_start(JNIEnv *env, jobject thisObject, jstring host,
                                                   jint port, jint duration, jint streams) {

    const char *hostPointer = env->GetStringUTFChars(host, nullptr);
    __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "Start host %s", hostPointer);

    jmethodID callback = nullptr;
    jclass cls2 = env->GetObjectClass(thisObject);
    if (cls2 == nullptr) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "ERROR: class not found !");
    } else {
        callback = env->GetMethodID(cls2, "reportCallback", "(Ljava/lang/String;)V");
        if (callback == nullptr) {
            __android_log_print(ANDROID_LOG_ERROR, APPNAME, "ERROR: reportCallback not found !");
            return;
        }
    }
    try {
        IperfTest test = IperfTest(hostPointer, port, Verbose::YES, Role::CLIENT, JsonReport::YES);
        test.set_duration(duration);
        test.set_num_streams(streams);
        test.run_client();

        test.print_statistics([=](const std::string &info) {
            std::string temp;
            utf8::replace_invalid(info.begin(), info.end(), back_inserter(temp));
            auto out_valid = temp;
            jstring out = env->NewStringUTF(out_valid.c_str());
            env->CallVoidMethod(thisObject, callback, out);
        });
    } catch (const std::exception &e) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "Error %s", e.what());
    }
}