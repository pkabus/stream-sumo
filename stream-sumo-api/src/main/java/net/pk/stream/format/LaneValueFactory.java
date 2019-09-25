package net.pk.stream.format;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class LaneValueFactory implements ValueListFromXmlFactory<LaneValue> {

	private Logger log;

	/**
	 * 
	 */
	public LaneValueFactory() {
		this.log = LoggerFactory.getLogger(getClass());
	}

	@Override
	public LaneValue create() {
		return new LaneValue();
	}

	@Override
	public List<LaneValue> parseXml(@Nullable final String str) {
		if (str == null) {
			return null;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document document = null;
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			document = builder.parse(new InputSource(new StringReader(str)));
		} catch (SAXException e) {
			this.log.debug("Ignore record " + str + " because of " + e);
			return Collections.emptyList();
		} catch (IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		// list which is to be filled with objects
		ArrayList<LaneValue> list = new ArrayList<>();

		NodeList timestepNodes = document.getElementsByTagName("timestep");
		if (timestepNodes.getLength() != 1) {
			this.log.debug(
					"Unexpected number of <timestep> nodes: " + timestepNodes.getLength() + " in xml snippet " + str);
			return list;
		}
		// timestamp for all following LaneValue objects
		Element timestepEle = (Element) timestepNodes.item(0);
		String timestep = timestepEle.getAttribute("time");

		// separate lane elements from each other
		NodeList lanes = document.getElementsByTagName("lane");

		for (int i = 0; i < lanes.getLength(); i++) {
			Element lane = (Element) lanes.item(i);
			String id = lane.getAttribute("id");
			NodeList vehicleList = lane.getElementsByTagName("vehicle");

			// create LaneValue and set all attributes
			final LaneValue laneValue = this.create();
			laneValue.set(LaneValue.KEY_ID, id);
			laneValue.set(LaneValue.KEY_NUM_VEHICLES, "" + vehicleList.getLength());
			laneValue.set(LaneValue.KEY_TIMESTAMP, timestep);
			list.add(laneValue);
		}

		return list;
	}
}
