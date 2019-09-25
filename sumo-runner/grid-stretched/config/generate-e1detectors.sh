#!/bin/bash
python $SUMO_HOME/tools/output/generateTLSE1Detectors.py -n grid-stretched.net.xml -d 2.5 -f 10 -o e1detector.add.xml -r localhost:9000
