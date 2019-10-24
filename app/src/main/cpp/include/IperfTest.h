//
// Created by pleshkanev.a.a on 02/10/2019.
//

#ifndef SBERWIFI_IPERFTEST_H
#define SBERWIFI_IPERFTEST_H


#include <unordered_map>
#include <string>
#include "../iperf/iperf_api.h"
#include "../iperf/iperf.h"
#include <functional>
#include <sstream>
#include <iostream>
#include "RoleEnum.h"
#include "../iperf/Verbose.h"
#include "JsonReport.h"


class IperfTest {

public:
    IperfTest(const std::string &host, int port, Verbose verbose, Role role, JsonReport jsonReport,
              bool reverse);

    ~IperfTest();

    void run_client();

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
    void print_statistics(const std::function<void(std::string)> &);

private:
    std::unordered_map<Verbose, int> verboseMode = {
            {Verbose::YES, 1},
            {Verbose::NO,  0}
    };

    std::unordered_map<Role, char> role_map = {
            {Role::CLIENT, 'c'},
            {Role::SERVER, 's'},

    };

    std::unordered_map<JsonReport, unsigned> json_report = {
            {JsonReport::YES, 1},
            {JsonReport::NO,  0}
    };

    struct iperf_test *test;

};


#endif //UNTITLED1_IPERFTEST_H
