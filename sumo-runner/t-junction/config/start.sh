#!/bin/bash

TRAAS_VERSION=1.0
SOCKET_VERSION=1.0
SCENARIO_VERSION=1.0.0-SNAPSHOT
TRAAS_JAR="/home/peter/.m2/repository/sumo/Traas/$TRAAS_VERSION/Traas-$TRAAS_VERSION.jar"
SOCKET_JAR="/home/peter/.m2/repository/net/pk/socket-server/$SOCKET_VERSION/socket-server-$SOCKET_VERSION.jar"
BASE_JAR="/home/peter/.m2/repository/net/pk/scenario-base/$SCENARIO_VERSION/scenario-base-$SCENARIO_VERSION.jar"
TCROSS_JAR="/home/peter/.m2/repository/net/pk/scenario-tcross/$SCENARIO_VERSION/scenario-tcross-$SCENARIO_VERSION.jar"
TCROSS_DIR="/home/peter/master/sumo-runner/tcross/config"

# call config scripts to set output target (host:port)
bash $TCROSS_DIR/conf-detectors.sh localhost 9000
bash $TCROSS_DIR/conf-tls.sh localhost 9001

# kill socat processes
#if [ nc -z localhost 9000 ]
#  then
#    kill $(lsof -t -i:9000)
#fi

#if [ nc -z localhost 9001 ]
#  then
#   kill $(lsof -t -i:9001)
#fi

#if [ nc -z localhost 10000 ]
#  then
#    kill $(lsof -t -i:10000)
#fi

# generate sumo traffic using sumo's randomTrips python script
bash $TCROSS_DIR/generate.sh &

# start socket server for detectors and tls output
#socat tcp-listen:9000 tcp-listen:9100,fork,reuseaddr &
#socat tcp-listen:9001 tcp-listen:9101,fork,reuseaddr &
#socat tcp-listen:10000 tcp-listen:10100,fork,reuseaddr &

#while ! lsof -t -i:9000; do
# sleep 1
# echo "Wait for port 9000..."
#done

# run sumo
java -cp $BASE_JAR:$TRAAS_JAR:$TCROSS_JAR net.pk.traas.server.tcross.Main -h localhost -p 9000 &

#s_len=$(lsof -t -i:9000)
#while [ ${#s_len} -lt 6 ] ; do
#  echo "Wait for Sumo..."
#  sleep 3
#  s_len=$(lsof -t -i:9000)
#done
