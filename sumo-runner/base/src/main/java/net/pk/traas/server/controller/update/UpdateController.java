package net.pk.traas.server.controller.update;

import java.util.Observable;

/** Takes care of data stream entries coming from the stream engine.
 * @author peter
 *
 */
public abstract class UpdateController extends Observable {

	/**
	 * This method handles the update process. It is responsible for reading new
	 * stream values and store it in a shared storage object.
	 */
	public abstract void update();

}
