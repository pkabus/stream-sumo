# Installation
Maven and Java are required. Use the build script like
```sh
./build.sh
```
to install the necessary modules.

# Run
At least you need to specify one of the installed sumocfg scenarios with the ``` -s ``` argument. For example:
```sh
./start.sh -s ./sumo-runner/t-junction/config/t-junction.sumocfg
```

Additionally you can specify the ``` -m ``` argument to specify the data source. The data source is responsible for traffic management decisions. By default ```LANE_BASED``` is set.

## Data source
Use ``` -m STATIC ``` to set statically traffic light switches.

Use ``` -m LANE_BASED ``` to set vision based traffic management.

Use ``` -m E1DETECTOR_BASED ``` to take into account data recorded by induction loop detectors in front of traffic lights.

## Run scenarios like
```sh
./start.sh -s ./sumo-runner/t-junction/config/t-junction.sumocfg
```
```sh
./start.sh -s ./sumo-runner/block-junctions/config/block-junctions.sumocfg
```
```sh
./start.sh -s ./sumo-runner/grid-stretched/config/grid-stretched.sumocfg
```
```sh
./start.sh -s ./sumo-runner/single-big-junction/config/single-big-junction.sumocfg
```
