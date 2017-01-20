#!/bin/bash
mkdir BACKUP
cd ..
while read p; do
   cp --parents $p utouch/BACKUP
done <utouch/changed_files.txt
