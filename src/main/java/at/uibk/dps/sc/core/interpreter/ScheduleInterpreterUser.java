package at.uibk.dps.sc.core.interpreter;

import java.util.Set;

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
 * The {@link ScheduleInterpreterUser} is used to schedule user tasks.
 * 
 * @author Fedor Smirnov
 *
 */
public abstract class ScheduleInterpreterUser implements ScheduleInterpreter {

  protected final LocalFunctionFactory localFunctionFactory;

  public ScheduleInterpreterUser(LocalFunctionFactory localFunctionFactory) {
    this.localFunctionFactory = localFunctionFactory;
  }

  @Override
  public final EnactmentFunction interpretSchedule(Task task,
      Set<Mapping<Task, Resource>> scheduleModel) {
    checkSchedule(task, scheduleModel);
    return interpretScheduleUser(task, scheduleModel);
  }

  /**
   * Checks the schedule before processing. Throws an exception if the schedule
   * does not comply with the interpreter's expectations.
   * 
   * @param task the scheduled task
   * @param scheduleModel the schedule model
   */
  protected void checkSchedule(Task task, Set<Mapping<Task, Resource>> scheduleModel) {
    if (scheduleModel.size() < 1) {
      throw new IllegalArgumentException("A user task must be scheduled to at least one mapping.");
    }
  }

  /**
   * Returns the enactment function corresponding to the provided mapping edge.
   * 
   * @param mapping the provided mapping edge
   * @return the enactment function corresponding to the provided mapping edge
   */
  protected EnactmentFunction getFunctionForMapping(Mapping<Task, Resource> mapping) {
    Task task = mapping.getSource();
    Resource target = mapping.getTarget();
    ResourceType resType = PropertyServiceResource.getResourceType(target);
    if (resType.equals(ResourceType.Local)) {
      return interpretLocal(task, target);
    } else if (resType.equals(ResourceType.Serverless)) {
      return interpretServerless(task, target);
    } else {
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
    try {
      LocalCalculations localFunction =
          LocalCalculations.valueOf(PropertyServiceFunctionUser.getFunctionTypeString(task));
      return localFunctionFactory.getLocalFunction(localFunction);
    } catch (IllegalArgumentException exc) {
      throw new IllegalStateException(
          "The task " + task.getId() + " is annotated with a type which cannot be run locally: "
              + PropertyServiceFunctionUser.getFunctionTypeString(task));
    }
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
