#!/bin/bash

cd karaf/target
rm -rf odlmaple-karaf-2.0.0
unzip odlmaple*.zip
cd odlmaple*
sed -i 's/suspend=n/suspend=y/g' bin/karaf
export KARAF_DEBUG=true
./bin/karaf
