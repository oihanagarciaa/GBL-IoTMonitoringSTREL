cmake_minimum_required(VERSION 3.20.0)

set(BOARD "thingy52_nrf52832")

find_package(Zephyr REQUIRED HINTS $ENV{ZEPHYR_BASE})
project(Thingy52OfficeMonitoring)

FILE(GLOB app_sources src/*.c)
target_sources(app PRIVATE ${app_sources})
