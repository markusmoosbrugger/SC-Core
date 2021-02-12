package at.uibk.dps.sc.core.interpreter;

import java.util.Set;
import com.google.inject.Inject;
import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction.UsageType;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link ScheduleInterpreterAll} is the interpreter receiving all tasks which are to be scheduled.
 * 
 * @author Fedor Smirnov
 *
 */
public class ScheduleInterpreterAll implements ScheduleInterpreter{

	protected final ScheduleInterpreterEE interpreterEE;
	protected final ScheduleInterpreterUser interpreterUser;
	
	@Inject
	public ScheduleInterpreterAll(ScheduleInterpreterEE interpreterEE, ScheduleInterpreterUser interpreterUser) {
		this.interpreterEE = interpreterEE;
		this.interpreterUser = interpreterUser;
	}

	@Override
	public EnactmentFunction interpretSchedule(Task task, Set<Mapping<Task, Resource>> scheduleModel) {
		UsageType usage = PropertyServiceFunction.getUsageType(task);
		if(usage.equals(UsageType.User)) {
			return interpreterUser.interpretSchedule(task, scheduleModel);
		}else if (usage.equals(UsageType.DataFlow) || usage.equals(UsageType.Utility)) {
			return interpreterEE.interpretSchedule(task, scheduleModel);
		}else {
			throw new IllegalArgumentException("Unknown usage type " + usage.name());
		}
	}
}
