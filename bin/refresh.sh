#!/bin/bash
#
# Copy the latest SPI jar locally.
# Will fail if there's no build in variant/SERVER/target/universal
# TODO: Figure out how to deal with this circular dependency
#

set -e
root_dir=$(cd $(dirname $0)/..; pwd)
# SPI jar is packaged alongside server directory inside of the distribution ZIP archive.
tmpdir="/tmp/$RANDOM"
mkdir $tmpdir
unzip $root_dir/../variant/SERVER/target/universal/variant-server*.zip -d $tmpdir
cp $tmpdir/variant-spi*.jar $root_dir/lib