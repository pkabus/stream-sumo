#!/usr/bin/env python
# Eclipse SUMO, Simulation of Urban MObility; see https://eclipse.org/sumo
# Copyright (C) 2009-2019 German Aerospace Center (DLR) and others.
# This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v2.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v20.html
# SPDX-License-Identifier: EPL-2.0

# @file    runner.py
# @author  Lena Kalleske
# @author  Daniel Krajzewicz
# @author  Michael Behrisch
# @author  Jakob Erdmann
# @date    2009-03-26
# @version $Id$

from __future__ import absolute_import
from __future__ import print_function
import time

import os
import sys
import random
import optparse

# we need to import python modules from the $SUMO_HOME/tools directory
if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools)
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")

from sumolib import checkBinary  # noqa
import traci  # noqa
from generator import Generator

class Runner:
    def __init__(self):
        pass

    def run(self):
        """execute the TraCI control loop"""
        traci.start([sumoBinary, "-c", "tcross.sumocfg"])
        traci.trafficlight.setProgram("e2", "0")
        while traci.simulation.getMinExpectedNumber() > 0:
            if traci.inductionloop.getLastStepVehicleNumber("detector_e2_0_e2_0") > 0:
                traci.trafficlight.setProgram("e2", "switch_e2_0")
            elif traci.inductionloop.getLastStepVehicleNumber("detector_e2_0_e2_0") == 0\
                    and traci.trafficlight.getPhase("e2") == 3:
                traci.trafficlight.setProgram("e2", "0")
            traci.simulationStep()

        traci.close()
        sys.stdout.flush()


def get_options():
    optParser = optparse.OptionParser()
    optParser.add_option("--nogui", action="store_true",
                         default=False, help="run the commandline version of sumo")
    options, args = optParser.parse_args()
    return options


# this is the main entry point of this script
if __name__ == "__main__":
    options = get_options()

    # this script has been called from the command line. It will start sumo as a
    # server, then connect and run
    if options.nogui:
        sumoBinary = checkBinary('sumo')
    else:
        sumoBinary = checkBinary('sumo-gui')

    # first, generate the route file for this simulation
    Generator.generate_routefile()
    r = Runner()
    r.run()
