//
// Created by pleshkanev.a.a on 02/10/2019.
//

#include "IperfTest.h"

IperfTest::IperfTest(const std::string &host, int port, VerboseEnum verbose, Role role) {
    test = iperf_new_test();
    iperf_defaults(test);
    iperf_set_test_server_port(test, port);
    iperf_set_verbose(test, verboseMode.at(verbose));
    iperf_set_test_role(test, role_map.at(role));

    char *hostPointer = const_cast<char *>(host.c_str());
    iperf_set_test_server_hostname(test, hostPointer);
}

IperfTest::~IperfTest() {
    iperf_free_test(test);
}

void IperfTest::run() {
    if (iperf_run_client(test) < 0) {
        std::string info = "host:" +
                           std::string(test->server_hostname) + " port:" +
                           std::to_string(test->bind_port);
        std::string error;
        std::sprintf(const_cast<char *>(error.c_str()), "%s: ошибка - %s\n", info.c_str(),
                     iperf_strerror(i_errno));
        throw std::runtime_error(error);
    }
}

void IperfTest::set_omit(int omit) {
    if (omit < 0 || omit > 60) {
        throw std::invalid_argument("Omit параметр должен быть в диапазоне от 0 до 60 не включая");
    }
    iperf_set_test_omit(test, 3);
}

void IperfTest::set_duration(int duration) {
    if (duration > MAX_TIME) {
        throw std::invalid_argument(
                "Duration параметр должен быть не более " + std::to_string(MAX_TIME));
    }
    iperf_set_test_duration(test, duration);
}

void IperfTest::set_num_streams(int streams) {
    if (streams > MAX_STREAMS) {
        throw std::invalid_argument(
                "Streams параметр должен быть не более " + std::to_string(MAX_STREAMS));
    }
    iperf_set_test_num_streams(test, streams);
}

void IperfTest::set_reporter_interval(int interval) {
    if ((interval < MIN_INTERVAL || interval > MAX_INTERVAL) && interval != 0) {
        throw std::invalid_argument("Interval параметр должен быть не менее " +
                                    std::to_string(MIN_INTERVAL) + " и не быть более " +
                                    std::to_string(MAX_INTERVAL));
    }
    iperf_set_test_reporter_interval(test, interval);
}

void IperfTest::print_statistics(std::function<void(std::string)> callback) {
    if (iperf_get_test_json_output_string(test)) {
        char *out = nullptr;
        std::sprintf(out, "%zd bytes of JSON emitted\n",
                     strlen(iperf_get_test_json_output_string(test)));
        callback(std::string(out));
    }
}
