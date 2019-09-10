#!/bin/bash

build_detector_add_file() {
	sed -e "s/\${host}/$1/" -e "s/\${port}/$2/" e1detector-template.add.xml  > e1detector.add.xml
}

build_detector_add_file ${1:-localhost} ${2:-9000}
