#!/bin/bash
#
# Build server extension API standard library.
# 


version=0.10.0
root_dir=$(cd $(dirname $0)/..; pwd)

cd $root_dir

# Build and package.
mvn clean package

# Unpackage the JAR and add the /db directory to it.
# This way these scripts to be easily discoverable by server tests.
jar -uf target/variant-extapi-standard-${version}.jar $(find db)

# Copy the new buid into the server's distr directory.
# assuming the location of the variant local repo.
cp target/variant-extapi-standard-${version}.jar ../../variant/SERVER/distr/ext