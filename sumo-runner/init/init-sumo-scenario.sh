#!/bin/bash

SUMO_RUNNER_HOME=/home/peter/master/modules/sumo-runner/
INIT_HOME=$SUMO_RUNNER_HOME/init/

create_dirs() {
	cd $SUMO_RUNNER_HOME && mkdir $1 && cd $1 && mkdir config && cd config &&
	touch $1.net.xml && touch tls-template.add.xml && touch e1detector-template.add.xml && touch $1.rou.xml &&
	cd $INIT_HOME &&
	cp conf-tls.sh $SUMO_RUNNER_HOME/$1/config/conf-tls.sh &&
	cp conf-detectors.sh $SUMO_RUNNER_HOME/$1/config/conf-detectors.sh &&
	cp settings.xml $SUMO_RUNNER_HOME/$1/config/settings.xml &&
	sed -e "s|\${scenario-name}|$1|g" $INIT_HOME/template.sumocfg  > $SUMO_RUNNER_HOME/$1/config/$1.sumocfg &&
	sed -e "s|\${scenario-name}|$1|g" $INIT_HOME/generate.sh  > $SUMO_RUNNER_HOME/$1/config/generate.sh
	sed -e "s|\${scenario-name}|$1|g" $INIT_HOME/generate-e1detectors.sh > $SUMO_RUNNER_HOME/$1/config/generate-e1detectors.sh
}


if [ -z "${1}" ]
then
	echo "No argument for scenario name. Abort"
elif [ -d "$SUMO_RUNNER_HOME/${1}/config" ]
then
	echo "Directory $SUMO_HOME_RUNNER/${1}/config already exists. Abort!"
else
	create_dirs ${1}
fi
