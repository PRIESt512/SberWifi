//
// Created by pleshkanev.a.a on 02/10/2019.
//

#ifndef SBERWIFI_IPERFTEST_H
#define SBERWIFI_IPERFTEST_H

#include <string>
#include "iperf/iperf_api.h"
#include "iperf/iperf.h"
#include <functional>
#include <unordered_map>
#include "RoleEnum.h"
#include "VerboseEnum.h"

class IperfTest {

public:
    IperfTest(const std::string &host, int port, VerboseEnum verbose, Role role);

    ~IperfTest();

    void run();

    /**
     * Установка ограничение первых n секунд теста
     * @param omit секунды
     */
    void set_omit(int omit);

    /**
     * Установка длительности тестирования в секундах
     * @param duration секунды
     */
    void set_duration(int duration);

    /**
     * Установить количество одновременных подключений к сети
     * @param streams
     */
    void set_num_streams(int streams);

    /**
     * Установить интервал, в течении которого будет генерироваться отчет
     * @param interval
     */
    void set_reporter_interval(int interval);

    /**
     * Вызов callback-функции для вывода статистики
     */
    void print_statistics(std::function<void(std::string)>);

private:
    std::unordered_map<VerboseEnum, int> verboseMode = {
            {VerboseEnum::YES, 1},
            {VerboseEnum::NO,  0}
    };

    std::unordered_map<Role, char> role_map = {
            {Role::CLIENT, 'c'},
            {Role::SERVER, 's'}
    };

    struct iperf_test *test;

};


#endif //UNTITLED1_IPERFTEST_H
