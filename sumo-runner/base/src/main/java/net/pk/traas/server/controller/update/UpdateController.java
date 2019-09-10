package net.pk.traas.server.controller.update;

/** Takes care of data stream entries coming from the stream engine.
 * @author peter
 *
 */
public interface UpdateController {

	/**
	 * This method handles the update process. It is responsible for reading new
	 * stream values and store it in a shared storage object.
	 */
	public abstract void update();

}
