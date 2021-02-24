package at.uibk.dps.sc.core.scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction.UsageType;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * Abstract class to define the general scheduling process which is based on the
 * processing of the {@link EnactmentSpecification}.
 * 
 * @author Fedor Smirnov
 */
public abstract class SchedulerAbstract implements Scheduler {

  protected final EnactmentSpecification specification;
  protected final ConcurrentHashMap<Task, Set<Mapping<Task, Resource>>> concurrentMappings;

  /**
   * Default constructor
   * 
   * @param specProvider specification provider
   */
  public SchedulerAbstract(final SpecificationProvider specProvider) {
    this.specification = specProvider.getSpecification();
    this.concurrentMappings = makeConcurrentMappings(specification.getMappings());
  }

  protected ConcurrentHashMap<Task, Set<Mapping<Task, Resource>>> makeConcurrentMappings(
      Mappings<Task, Resource> mappings) {
    ConcurrentHashMap<Task, Set<Mapping<Task, Resource>>> result = new ConcurrentHashMap<>();
    for (Mapping<Task, Resource> mapping : mappings) {
      if (!result.containsKey(mapping.getSource())) {
        result.put(mapping.getSource(), new HashSet<>());
      }
      result.get(mapping.getSource()).add(mapping);
    }
    return result;
  }

  @Override
  public Set<Mapping<Task, Resource>> scheduleTask(final Task task) {
    if (PropertyServiceFunction.getUsageType(task).equals(UsageType.User)) {
      return chooseMappingSubset(task,
          getTaskMappingOptions(concurrentMappings.get(getOriginalTask(task)), task));
    } else {
      return new HashSet<>();
    }
  }

  /**
   * Returns the mappings annotated for the given task in the specification
   * (checks the parent task in case no mappings are found).
   * 
   * @param task the task to check
   * @return the mappings annotated for the given task in the specification
   *         (checks the parent task in case no mappings are found)
   */
  protected Set<Mapping<Task, Resource>> getTaskMappingOptions(
      final Set<Mapping<Task, Resource>> taskMappings, final Task task) {
    Set<Mapping<Task, Resource>> result = new HashSet<>(taskMappings);
    if (result.isEmpty()) {
      if (task.getParent() == null) {
        throw new IllegalArgumentException("No mappings provided for the task " + task.getId());
      } else {
        return getTaskMappingOptions(taskMappings, (Task) task.getParent());
      }
    } else {
      return result;
    }
  }

  protected Task getOriginalTask(Task task) {
    if (task.getParent() == null) {
      return task;
    } else {
      return getOriginalTask((Task) task.getParent());
    }
  }

  /**
   * Method provided with a mapping set representing all possible bindings of the
   * given task. Returns a subset of these mappings representing an actual
   * schedule.
   * 
   * @param task the given task
   * @param mappingOptions all mapping options for the given task
   * @return a mapping subset representing a schedule
   */
  protected abstract Set<Mapping<Task, Resource>> chooseMappingSubset(final Task task,
      final Set<Mapping<Task, Resource>> mappingOptions);
}
