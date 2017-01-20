#!/bin/bash
./backup.sh
cd ..
while read p; do
   merge -A $p $p utouch/$p
done <utouch/changed_files.txt
