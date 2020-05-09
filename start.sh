#!/bin/sh


# FIND JAR (INDEPENDENTLY FROM VERSION)

#--exclude original[...].jar
#--include files ending with .jar
# -l only print filename
# -r recursive search
# -e regular expression
JAR="$(grep --exclude=original* --include=*.jar -lr ./sumo-runner/base/target/ -e pattern)"


if [ -z "$1" -o -z "$2" ]
  then
    echo "Arguments missing. Use -s /dir/to/*.sumocfg to specify a sumo config file."
  else
    java -jar $JAR $1 $2 $3 $4 $> sumo-runner.out.txt
fi


