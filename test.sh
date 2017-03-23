#!/bin/bash

cd karaf/target
rm -rf odlmaple-karaf-2.0.0-SNAPSHOT
unzip odlmaple*.zip
cd odlmaple*
./bin/karaf
