//
// Created by pleshkanev.a.a on 2019-10-02.
//

#include "iperfJava.h"
#include <android/log.h>

#define APPNAME "SberWiFi"


extern "C" JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_WiFiFragment_initTempPath(JNIEnv *env, jobject thisObject) {
    jclass cls2 = env->GetObjectClass(thisObject);

    if (cls2 == nullptr) {
        __android_log_print(ANDROID_LOG_FATAL, APPNAME, "ERROR: class not found !");
    } else {
        jmethodID initContext = env->GetMethodID(cls2, "getTempPath", "()Ljava/lang/String;");
        //if (initContext != nullptr) {
        auto path = env->CallObjectMethod(thisObject, initContext);
        tempPath = const_cast<char *>(env->GetStringUTFChars(static_cast<jstring>(path), nullptr));

        __android_log_print(ANDROID_LOG_INFO, APPNAME, "Nice!");
        //  } else {
        __android_log_print(ANDROID_LOG_FATAL, APPNAME, "ERROR: getTempPath not found !");
        // }
    }
}


extern "C" JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_WiFiFragment_start(JNIEnv *env, jobject thisObject, jstring host,
                                                  jint port) {

    const char *hostPointer = env->GetStringUTFChars(host, nullptr);
    struct iperf_test *test;

    __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "Start host %s", hostPointer);

    test = iperf_new_test();

    if (test == nullptr) {
        fprintf(stderr, "usage: %s [host] %s [port]\n", hostPointer, std::to_string(port).c_str());
        return;
    }

    const int duration = 5;
    iperf_defaults(test);
    iperf_set_verbose(test, 1);
    iperf_set_test_role(test, 'c');
    iperf_set_test_server_hostname(test, const_cast<char *>(hostPointer));
    iperf_set_test_server_port(test, port);
    iperf_set_test_omit(test, 3);
    iperf_set_test_duration(test, duration);
    iperf_set_test_reporter_interval(test, 1);
    iperf_set_test_stats_interval(test, 1);

    __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "Run client");
    if (iperf_run_client(test) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, APPNAME, "%s: error - %s\n", hostPointer,
                            iperf_strerror(i_errno));
        iperf_free_test(test);
        return;
    }

    __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "Run test");
    if (iperf_get_test_json_output_string(test)) {
        __android_log_print(ANDROID_LOG_DEBUG, APPNAME, "%zd bytes of JSON emitted\n",
                            strlen(iperf_get_test_json_output_string(test)));
    }
    iperf_free_test(test);

    return;
}