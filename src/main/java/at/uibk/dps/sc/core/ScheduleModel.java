package at.uibk.dps.sc.core;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * Interface for the class used to maintain the information that the EE has on
 * the scheduling of its tasks.
 * 
 * @author Fedor Smirnov
 */
public class ScheduleModel {

  protected final ConcurrentHashMap<Task, Set<Mapping<Task, Resource>>> scheduleMap =
      new ConcurrentHashMap<>();

  /**
   * Returns true if the task is already scheduled.
   * 
   * @param functionTask the requested task
   * @return true if the task is already scheduled
   */
  public boolean isScheduled(final Task functionTask) {
    return scheduleMap.containsKey(functionTask);
  }

  /**
   * Sets the schedule for the given task.
   * 
   * @param task
   * @param schedule
   */
  public void setTaskSchedule(final Task task, final Set<Mapping<Task, Resource>> schedule) {
    scheduleMap.put(task, schedule);
  }

  /**
   * Returns the schedule annotated for the requested task.
   * 
   * @param task the requested task
   * @return the task schedule, in the form of (annotated) mapping edges
   */
  public Set<Mapping<Task, Resource>> getTaskSchedule(final Task task) {
    if (!isScheduled(task)) {
      throw new IllegalArgumentException(
          "Request for the schedule of unscheduled task " + task.getId());
    }
    return scheduleMap.get(task);
  }
}
