/****************************************************************************/
// Eclipse SUMO, Simulation of Urban MObility; see https://eclipse.org/sumo
// Copyright (C) 2017-2018 German Aerospace Center (DLR) and others.
// TraaS module
// Copyright (C) 2016-2017 Dresden University of Technology
// This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v2.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v20.html
// SPDX-License-Identifier: EPL-2.0
/****************************************************************************/
/// @file    SumoTLSPhase.java
/// @author  Mario Krumnow
/// @author  Evamarie Wiessner
/// @date    2016
/// @version $Id$
///
//
/****************************************************************************/
package de.tudresden.ws.container;
import java.util.ArrayList;

/**
 * 
 * @author Mario Krumnow
 *
 */

public class SumoTLSPhase implements SumoObject {

	public double duration;
	public double minDur;
	public double maxDur;
	public String phasedef;
	public ArrayList<Integer> next;
	public String name;
	
	public SumoTLSPhase(){
		
		this.duration = 0;
		this.minDur = 0;
		this.maxDur = 0;
		this.phasedef = "r";
        this.name = "";
        this.next = new ArrayList<Integer>();
		
	}
	
	public SumoTLSPhase(double duration, double minDur, double maxDur, String phasedef, ArrayList<Integer> next, String name){
		
		this.duration = duration;
		this.minDur = minDur;
		this.maxDur = maxDur;
		this.phasedef = phasedef;
        this.name = name;
        this.next = next;
		
	}
	
	public SumoTLSPhase(int duration, String phasedef){
		
		this.duration = duration;
		this.minDur = duration;
		this.maxDur = duration;
		this.phasedef = phasedef;
        this.name = "";
        this.next = new ArrayList<Integer>();
		
	}
	
	public String toString(){
		return this.phasedef+"#"+this.duration+"#"+this.minDur+"#"+this.maxDur+"#"+this.next+"#"+this.name;
	}
	
}
