/****************************************************************************/
// Eclipse SUMO, Simulation of Urban MObility; see https://eclipse.org/sumo
// Copyright (C) 2017-2019 German Aerospace Center (DLR) and others.
// TraaS module
// Copyright (C) 2016-2019 Dresden University of Technology
// This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v2.0
// which accompanies this distribution, and is available at
// http://www.eclipse.org/legal/epl-v20.html
// SPDX-License-Identifier: EPL-2.0
/****************************************************************************/
/// @file    SumoStage.java
/// @author  Jakob Erdmann
/// @date    2019
/// @version $Id$
///
//
/****************************************************************************/
package de.tudresden.ws.container;
import de.tudresden.ws.container.SumoStringList;

/**
 * 
 * @author Jakob Erdmann
 *
 */

public class SumoStage implements SumoObject {

	public double type;
	public String vType;
	public String line;
	public String destStop;
	public SumoStringList edges;
	public double travelTime;
	public double cost;
	public double length;
	public String intended;
	public double depart;
	public double departPos;
	public double arrivalPos;
	public String description;
	
	public SumoStage(){ 
        edges = new SumoStringList();
    }
}
