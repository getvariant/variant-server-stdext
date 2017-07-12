#!/bin/bash
#
# Generate Javadoc for this repo.
# 


version=0.7.1
root_dir=$(cd $(dirname $0)/..; pwd)
javadoc_dir=${root_dir}/docs

rm -rf ${javadoc_dir}/*

ls ${root_dir}/server-extensions-demo/src/main/java

javadoc -d ${javadoc_dir}  \
   -sourcepath ${root_dir}/server-extensions-demo/src/main/java \
   -windowtitle "Variant ${version}" \
   -doctitle "Variant Experiment Server Extensions API" \
   -header "<a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\"> <img style=\"margin-bottom:5px;\" src=\"http://getvariant.com/wp-content/uploads/2016/05/VariantLogoSmall.png\"/> \</a>" \
   -bottom "Release $version. Updated $(date +"%d %b %Y").<br/> Copyright &copy; 2017 <a onclick=\"window.top.location.href='http://getvariant.com';\" href=\"#\">Variant Inc.</a>" \
   com.variant.server.ext.demo
