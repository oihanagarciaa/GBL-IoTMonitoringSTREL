#!/bin/sh

cmake -B build -G Ninja
west build -p auto -b thingy52_nrf52832 .