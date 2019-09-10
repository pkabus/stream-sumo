#!/bin/bash

build_tls_add_file() {
	sed -e "s/\${host}/$1/" -e "s/\${port}/$2/" tls-template.add.xml  > tls.add.xml
}

build_tls_add_file ${1:-localhost} ${2:-9001}
