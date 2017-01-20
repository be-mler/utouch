#!/bin/bash
cd ..
while read p; do
   cp --parents $p utouch
done <utouch/changed_files.txt
