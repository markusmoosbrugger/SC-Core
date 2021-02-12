package at.uibk.dps.sc.core.interpreter;

import java.util.Set;

import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link ScheduleInterpreterUser} is used to schedule user tasks.
 * 
 * @author Fedor Smirnov
 *
 */
public abstract class ScheduleInterpreterUser implements ScheduleInterpreter {

  @Override
  public final EnactmentFunction interpretSchedule(Task task,
      Set<Mapping<Task, Resource>> scheduleModel) {
    if (scheduleModel.size() < 1) {
      throw new IllegalArgumentException("A user task must be scheduled to at least one mapping.");
    }
    return interpretScheduleUser(task, scheduleModel);
  }

  /**
   * Method doing the actual interpreting for the given user task.
   * 
   * @param task the user task
   * @param scheduleModel the schedule model
   * @return the enactment function resulting from the schedule.
   */
  protected abstract EnactmentFunction interpretScheduleUser(Task task,
      Set<Mapping<Task, Resource>> scheduleModel);
}
