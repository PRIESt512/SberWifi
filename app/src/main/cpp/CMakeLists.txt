# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_STANDARD 14)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

if ("${ANDROID_ABI}" MATCHES "arm64-v8a|x86_64")
    set(HAVE_SO_MAX_PACING_RATE 1)
endif ()

set(IPERF_SRC ${CMAKE_SOURCE_DIR}/iperf)

configure_file(${CMAKE_SOURCE_DIR}/../assets/iperf_config.h.in ${CMAKE_BINARY_DIR}/src/iperf_config.h)

add_library( # Sets the name of the library.
        iperf

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${IPERF_SRC}/cjson.c
        ${IPERF_SRC}/dscp.c
        ${IPERF_SRC}/iperf_api.c
        ${IPERF_SRC}/iperf_auth.c
        ${IPERF_SRC}/iperf_client_api.c
        ${IPERF_SRC}/iperf_error.c
        ${IPERF_SRC}/iperf_locale.c
        ${IPERF_SRC}/iperf_sctp.c
        ${IPERF_SRC}/iperf_server_api.c
        ${IPERF_SRC}/iperf_tcp.c
        ${IPERF_SRC}/iperf_time.c
        ${IPERF_SRC}/iperf_udp.c
        ${IPERF_SRC}/iperf_util.c
        ${IPERF_SRC}/net.c
        ${IPERF_SRC}/t_api.c
        ${IPERF_SRC}/tcp_info.c
        ${IPERF_SRC}/timer.c
        ${IPERF_SRC}/units.c
        ${CMAKE_SOURCE_DIR}/iperfJava.cpp
        ${CMAKE_SOURCE_DIR}/IperfTest.cpp
        )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        iperf

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})