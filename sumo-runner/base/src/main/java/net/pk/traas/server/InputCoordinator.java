package net.pk.traas.server;

import java.util.Arrays;
import java.util.List;

import net.pk.stream.format.E1DetectorValue;
import net.pk.traas.server.controller.junction.CoachManager;
import net.pk.traas.server.controller.update.InputController;

/**
 * @author peter
 * @param <V>
 *
 */
public class InputCoordinator {

	private double timestep;
	private List<InputController<?>> inputControllers;
	private CoachManager coachManager;

	public InputCoordinator(InputController<?>... controllers) {
		this.inputControllers = Arrays.asList(controllers);
		validateControllers();
	}

	private void validateControllers() {
		inputControllers.stream().filter(c -> E1DetectorValue.class.equals(c.getType())).findAny()
				.orElseThrow(() -> new RuntimeException("At least a " + InputController.class + "with type "
						+ E1DetectorValue.class + " must be registered."));
	}

	public void coordinate() {
//		for (int i = 0; i < e1DetectorValues.size(); i++) {
//			E1DetectorValue current = e1DetectorValues.get(i);
//			TLSCoach coach = coachManager.getCoach(current);
//			if (coach.acceptNextProgram(new E1DetectorValueToEdgeConverter().apply(current), timestep)) {
//				coach.greenToYellow();
//				updater.remove(current);
//			}
//
//		}
	}
}
