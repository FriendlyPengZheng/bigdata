#!/bin/bash

target_dir="/home/andy/stat-writer"
rm -rf "$target_dir/bin/*"
cp ./bin/* "$target_dir/bin/"
cd $target_dir
./startup.sh
cd -

