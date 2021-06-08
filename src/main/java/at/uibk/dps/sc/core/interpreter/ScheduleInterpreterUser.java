package at.uibk.dps.sc.core.interpreter;

import java.util.Set;

import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.container.FunctionFactoryLocal;
import at.uibk.dps.ee.enactables.local.demo.FunctionFactoryDemo;
import at.uibk.dps.ee.enactables.serverless.FunctionFactoryServerless;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
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

  protected final FunctionFactoryLocal functionFactoryLocal;
  protected final FunctionFactoryServerless functionFactorySl;
  protected final FunctionFactoryDemo functionFactoryDemo;

  /**
   * Default constructor.
   * 
   * @param functionFactoryLocal the factory for the creation of
   *        {@link EnactmentFunction}s performing local calculation
   * @param functionFactoryDemo the factory for the creation of demo functions
   *        implemented natively
   * @param functionFactorySl the factory for the creation of serverless functions
   */
  public ScheduleInterpreterUser(final FunctionFactoryLocal functionFactoryLocal,
      final FunctionFactoryServerless functionFactorySl,
      final FunctionFactoryDemo functionFactoryDemo) {
    this.functionFactoryLocal = functionFactoryLocal;
    this.functionFactorySl = functionFactorySl;
    this.functionFactoryDemo = functionFactoryDemo;
  }

  @Override
  public final EnactmentFunction interpretSchedule(final Task task,
      final Set<Mapping<Task, Resource>> scheduleModel) {
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
  protected void checkSchedule(final Task task, final Set<Mapping<Task, Resource>> scheduleModel) {
    if (scheduleModel.isEmpty()) {
      throw new IllegalArgumentException("A user task must be scheduled to at least one mapping.");
    }
  }

  /**
   * Returns the enactment function corresponding to the provided mapping edge.
   * 
   * @param mapping the provided mapping edge
   * @return the enactment function corresponding to the provided mapping edge
   */
  protected EnactmentFunction getFunctionForMapping(final Mapping<Task, Resource> mapping) {
    final EnactmentMode resType = PropertyServiceMapping.getEnactmentMode(mapping);
    if (resType.equals(EnactmentMode.Local)) {
      return interpretLocal(mapping);
    } else if (resType.equals(EnactmentMode.Serverless)) {
      return interpretServerless(mapping);
    } else if (resType.equals(EnactmentMode.Demo)) {
      return functionFactoryDemo.getLocalFunction(mapping);
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
  protected EnactmentFunction interpretLocal(final Mapping<Task, Resource> mapping) {
    return functionFactoryLocal.getContainerFunction(mapping);
  }

  /**
   * Gets the enactment function for the task on a serverless resource.
   * 
   * @param mapping the mapping
   * @return the enactment function for the task on a serverless resource
   */
  protected EnactmentFunction interpretServerless(final Mapping<Task, Resource> mapping) {
    return functionFactorySl.createServerlessFunction(mapping);
  }

  /**
   * Method doing the actual interpreting for the given user task.
   * 
   * @param task the user task
   * @param scheduleModel the schedule model
   * @return the enactment function resulting from the schedule.
   */
  protected abstract EnactmentFunction interpretScheduleUser(final Task task,
      final Set<Mapping<Task, Resource>> scheduleModel);
}
