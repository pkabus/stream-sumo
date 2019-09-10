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

# we need to import python modules from the $SUMO_HOME/tools directory
if 'SUMO_HOME' in os.environ:
    tools = os.path.join(os.environ['SUMO_HOME'], 'tools')
    sys.path.append(tools)
else:
    sys.exit("please declare environment variable 'SUMO_HOME'")

from sumolib import checkBinary  # noqa
import traci  # noqa


class Generator:

    def __init__(self):
        pass

    pSN_0 = 1. / 1
    pNS_0 = 1. / 1

    @classmethod
    def generate_routefile(cls):
        random.seed(42)  # make tests reproducible
        N = 500  # number of generation loop cycles
        # demand per second from different directions

        with open("tcross.rou.xml", "w") as routes:
            print("""<routes>
            <vType id="typeSuperSlow" accel="0.3" decel="4.5" sigma="0.2" length="6" minGap="4.5" maxSpeed="10"/>
            <vType id="typeSlow" accel="0.5" decel="4.5" sigma="0.5" length="6" minGap="2.5" maxSpeed="20"/>
            <vType id="typeFast" accel="1.0" decel="4.8" sigma="0.5" length="5" minGap="2" maxSpeed="30"/>

    <route id="SN_0" edges="e0_2_e0_1 e0_1_e0 e0_e1 e1_e2 e2_e3 e3_e4 e4_e5 e5_e6"/>
    <route id="NS_0" edges="-e5_e6 -e4_e5 -e3_e4 -e2_e3 -e1_e2 -e0_e1 -e0_1_e0 -e0_2_e0_1"/>
    <route id="WS_0" edges="e2_0_e2 -e1_e2 -e0_e1 -e0_1_e0 -e0_2_e0_1"/>
    <route id="WN_0" edges="e2_0_e2 e2_e3 e3_e4 e4_e5 e5_e6"/>""", file=routes)
            veh_nr = 0
            for i in range(N):
                print(
                    '<vehicle id="pSN_%i" type="typeFast" route="SN_0" depart="%i" departLane="free"  color="1,0,0" />'
                    % (veh_nr, i), file=routes)
                veh_nr += 1
                print(
                    '<vehicle id="pNS_%i" type="typeFast" route="NS_0" depart="%i" departLane="free"  color="1,0,0" />'
                    % (veh_nr, i), file=routes)
                veh_nr += 1
                if i % 7 == 0:
                    print(
                        '<vehicle id="pSN_superSlow_%i" type="typeSuperSlow" route="SN_0" depart="%i" color="0,1,0" />'
                        % (i, i), file=routes)
                    veh_nr += 1
                    print(
                        '<vehicle id="pNS_superSlow_%i" type="typeSuperSlow" route="NS_0" depart="%i" color="0,1,0" />'
                        % (i, i), file=routes)
                    veh_nr += 1
                if i % 5 == 0:
                    print('    <vehicle id="pWN_%i" type="typeSlow" route="WN_0" depart="%i" />' % (i, i),
                          file=routes)
                    veh_nr += 1
                    print('    <vehicle id="pWS_%i" type="typeSlow" route="WS_0" depart="%i" />' % (i, i),
                          file=routes)
                    veh_nr += 1

            print("</routes>", file=routes)

    @classmethod
    def add_vehicles(cls):
        now = int(str(time.time()).replace('.', ''))
        random.seed(now)
        for i in range(random.randint(1, 300)):
            if random.uniform(0, 1) < cls.pSN_0:
                traci.vehicle.add("veh_pSN_0_%i_%i" % (now, i), "pSN_0", typeID="typeFast")
            if random.uniform(0, 1) < cls.pNS_0:
                traci.vehicle.add("veh_pNS_0_%i_%i" % (now, i), "pNS_0", typeID="typeFast")


# this is the main entry point of this script
if __name__ == "__main__":
    Generator.generate_routefile()
