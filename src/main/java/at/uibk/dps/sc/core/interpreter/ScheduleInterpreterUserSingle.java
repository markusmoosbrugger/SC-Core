package at.uibk.dps.sc.core.interpreter;

import java.util.Set;
import com.google.inject.Inject;
import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.ConstantsLocal.LocalCalculations;
import at.uibk.dps.ee.enactables.local.calculation.LocalFunctionFactory;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUser;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
import at.uibk.dps.ee.model.properties.PropertyServiceResource.ResourceType;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link ScheduleInterpreterUserSingle} expects to get a schedule with
 * exactly one mapping.
 * 
 * @author Fedor Smirnov
 *
 */
public class ScheduleInterpreterUserSingle extends ScheduleInterpreterUser {

	protected final LocalFunctionFactory localFunctionFactory;
	
	@Inject
	public ScheduleInterpreterUserSingle(LocalFunctionFactory localFunctionFactory) {
		this.localFunctionFactory = localFunctionFactory;
	}

	@Override
	protected EnactmentFunction interpretScheduleUser(Task task, Set<Mapping<Task, Resource>> scheduleModel) {
		if (scheduleModel.size() != 1) {
			throw new IllegalArgumentException(
					"The configured schedule interpreter user expects exactly one mapping. Task with problem: "
							+ task.getId());
		}
		Resource target = scheduleModel.iterator().next().getTarget();
		ResourceType resType = PropertyServiceResource.getResourceType(target);
		if (resType.equals(ResourceType.Local)) {
			return interpretLocal(task, target);
		}else if (resType.equals(ResourceType.Serverless)) {
			return interpretServerless(task, target);
		}else {
			throw new IllegalArgumentException("Unknown resource type " + resType.name());
		}
	}

	/**
	 * Gets the enactment function for the task on the local resource.
	 * 
	 * @param task the task 
	 * @param resource the local resource
	 * @return the enactment function for the task on the local resource
	 */
	protected EnactmentFunction interpretLocal(Task task, Resource resource) {
		LocalCalculations localFunction = LocalCalculations.valueOf(PropertyServiceFunctionUser.getFunctionTypeString(task));
		return localFunctionFactory.getLocalFunction(localFunction);
	}

	/**
	 * Gets the enactment function for the task on a serverless resource.
	 * 
	 * @param task the task 
	 * @param resource the local resource
	 * @return the enactment function for the task on a serverless resource
	 */
	protected EnactmentFunction interpretServerless(Task task, Resource resource) {
		throw new IllegalStateException("Not yet implemented.");
	}
}
