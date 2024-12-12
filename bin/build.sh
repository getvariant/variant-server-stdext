#!/bin/bash
#
# Build the server SPI standard library and copy to SERVER.
#
set -e
# set -x
cd $(dirname $0)/..
mvn clean install
rm -rf ../variant/SERVER/src/universal/spi/variant-spi-stdlib-*.jar
cp target/variant-spi-stdlib-*.jar ../variant/SERVER/src/universal/spi/