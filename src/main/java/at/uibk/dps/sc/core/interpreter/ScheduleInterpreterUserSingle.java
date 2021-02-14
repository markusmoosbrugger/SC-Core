package at.uibk.dps.sc.core.interpreter;

import java.util.Set;
import com.google.inject.Inject;
import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.calculation.LocalFunctionFactory;
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

  /**
   * Injection constructor.
   * 
   * @param localFunctionFactory the factory used for the creation of
   *        {@link EnactmentFunction} used for the local calculation.
   */
  @Inject
  public ScheduleInterpreterUserSingle(final LocalFunctionFactory localFunctionFactory) {
    super(localFunctionFactory);
  }

  @Override
  protected EnactmentFunction interpretScheduleUser(final Task task,
      final Set<Mapping<Task, Resource>> scheduleModel) {
    return getFunctionForMapping(scheduleModel.iterator().next());
  }

  @Override
  protected void checkSchedule(final Task task, final Set<Mapping<Task, Resource>> scheduleModel) {
    super.checkSchedule(task, scheduleModel);
    if (scheduleModel.size() != 1) {
      throw new IllegalArgumentException(
          "The configured schedule interpreter user expects exactly one mapping. Task with problem: "
              + task.getId());
    }
  }
}
