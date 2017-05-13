#!/bin/bash

cd karaf/target
cd odlmaple-karaf-2.0.0-SNAPSHOT

rm -rf data instances journal lock snapshots

./bin/karaf
