//
// Created by pleshkanev.a.a on 2019-10-02.
//

#ifndef SBERWIFI_IPERFJAVA_H
#define SBERWIFI_IPERFJAVA_H
#include <jni.h>

#include "../iperf/iperf_api.h"
#include <string>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Старт клиента iperf. Передаем параметры для удаленного подключения к серверу iperf.
 * @param env - переменная окружения JVM-машины
 * @param thisObject - объект, который произвел вызов этого нативного метода
 * @param host - удаленная машина, на которой работает удаленный сервер iperf
 * @param port - порт, на котором сервер iperf слушает клиента
 * @param duration длительность тестирования в сек
 * @param streams количество одновременных потоков данных для тестирования
 */
JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_IperfFragment_start(JNIEnv *env, jobject thisObject, jstring host,
                                                   jint port, jint duration, jint streams,
                                                   jboolean reverse);

/**
 * Получаем абсолютный путь до директории, которую iperf будет использовать для создания временных файлов
 * на период тестирования сети
 * @param env - переменная окружения JVM-машины
 * @param thisObject - объект, который произвел вызов этого нативного метода
 */
JNIEXPORT void JNICALL
Java_ru_sbrf_sberwifi_fragment_IperfFragment_initTempPath(JNIEnv *env, jobject thisObject);
#ifdef __cplusplus
}
#endif

#endif //SBERWIFI_IPERFJAVA_H
