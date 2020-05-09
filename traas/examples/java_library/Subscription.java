/****************************************************************************/
// Eclipse SUMO, Simulation of Urban MObility; see https://eclipse.org/sumo
// Copyright (C) 2017-2018 German Aerospace Center (DLR) and others.
// TraaS module
// Copyright (C) 2013-2017 Dresden University of Technology
// This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v2.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v20.html
// SPDX-License-Identifier: EPL-2.0
/****************************************************************************/
/// @file    Subscription.java
/// @author  Jakob Erdmann
/// @date    2019
/// @version $Id$
///
//
/****************************************************************************/
import java.util.Observer;
import java.util.Observable;
import it.polito.appeal.traci.SumoTraciConnection;
import it.polito.appeal.traci.TraCIException;
import de.tudresden.sumo.cmd.Simulation;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.cmd.Inductionloop;
import de.tudresden.sumo.cmd.Trafficlight;
import de.tudresden.sumo.config.Constants;
import de.tudresden.sumo.subscription.VariableSubscription;
import de.tudresden.sumo.subscription.SubscribtionVariable;
import de.tudresden.sumo.subscription.SubscriptionObject;
import de.tudresden.sumo.subscription.ResponseType;
import de.tudresden.ws.container.SumoVehicleData;
import de.tudresden.ws.container.SumoStringList;
import de.tudresden.ws.container.SumoPrimitive;
import de.tudresden.ws.container.SumoPosition2D;

public class Subscription implements Observer {

	static String sumo_bin = "sumo";
	static String config_file = "simulation/config.sumo.cfg";
	static double step_length = 1;		

    static SumoTraciConnection conn = null;

	public static void main(String[] args) {
	
		
		try{
			
			conn = new SumoTraciConnection(sumo_bin, config_file);
			conn.addOption("step-length", step_length+"");
			conn.addOption("start", "true"); //start sumo immediately
		
			//start Traci Server
			conn.runServer();
            conn.setOrder(1);
            conn.addObserver(new Subscription());

            VariableSubscription vs = new VariableSubscription(SubscribtionVariable.simulation,0,100000 * 60, "");
            vs.addCommand(Constants.VAR_DEPARTED_VEHICLES_IDS);
            conn.do_subscription(vs);

			
			for(int i=0; i<10; i++){
			
				conn.do_timestep();
				conn.do_job_set(Vehicle.addFull("v"+i, "r1", "car", "now", "0", "0", "max", "current", "max", "current", "", "", "", 0, 0));
                double timeSeconds = (double)conn.do_job_get(Simulation.getTime());
                System.out.println("Step: " + i);
                
			}
			
			conn.close();
			
		}catch(Exception ex){ex.printStackTrace();}
		
	}

    public void update(Observable arg0, Object arg1) {
        SubscriptionObject so = (SubscriptionObject) arg1;
        //System.out.println("Subscription id=" + so.id + " domain=" +  so.domain + " name=" + so.name + " var=" + so.variable + " status=" + so.status + " ret=" + so.return_type + " resp=" + so.response.getID());

        if (so.response == ResponseType.SIM_VARIABLE) {
            assert(so.variable == Constants.VAR_DEPARTED_VEHICLES_IDS); 
            SumoStringList ssl=(SumoStringList) so.object;
            if(ssl.size()>0) {
                for (String vehID : ssl) {
                    System.out.println("Subscription Departed vehicles: " + vehID);
                    VariableSubscription vs= new VariableSubscription(SubscribtionVariable.vehicle,0,100000 * 60, vehID);
                    vs.addCommand(Constants.VAR_POSITION);
                    vs.addCommand(Constants.VAR_SPEED);
                    try {
                        conn.do_subscription(vs);
                    } catch (Exception ex) {
                        System.err.println("subscription to " + vehID + " failed");
                    }
                }
            }
        } else if (so.response == ResponseType.VEHICLE_VARIABLE) {
            if (so.variable == Constants.VAR_SPEED) {
                SumoPrimitive sp = (SumoPrimitive) so.object;
                System.out.println("Speed of vehicle " + so.id + ": "  + sp.val);
            } else if (so.variable == Constants.VAR_POSITION) {
                SumoPosition2D sc = (SumoPosition2D) so.object;
                System.out.println("Position of vehicle " + so.id + ": x = " + sc.x + " y = " + sc.y);
            }
        }

    }



}
