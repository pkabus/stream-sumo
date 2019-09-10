#!/bin/bash
python $SUMO_HOME/tools/output/generateTLSE1Detectors.py -n ${scenario-name}.net.xml -d 2.5 -f 1 -o e1detector.add.xml -r localhost:9000
