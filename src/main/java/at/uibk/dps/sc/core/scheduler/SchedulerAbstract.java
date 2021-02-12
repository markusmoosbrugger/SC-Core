package at.uibk.dps.sc.core.scheduler;

import java.util.Set;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * Abstract class to define the general scheduling process which is based on the processing of the
 * {@link EnactmentSpecification}.
 * 
 * @author Fedor Smirnov
 */
public abstract class SchedulerAbstract implements Scheduler {

  protected final EnactmentSpecification specification;

  public SchedulerAbstract(SpecificationProvider specProvider) {
    this.specification = specProvider.getSpecification();
  }

  @Override
  public Set<Mapping<Task, Resource>> scheduleTask(Task task) {
    Mappings<Task, Resource> mappings = specification.getMappings();
    return chooseMappingSubset(task, mappings.get(task));
  }

  /**
   * Method provided with a mapping set representing all possible bindings of the given task.
   * Returns a subset of these mappings representing an actual schedule.
   * 
   * @param task the given task
   * @param mappingOptions all mapping options for the given task
   * @return a mapping subset representing a schedule
   */
  protected abstract Set<Mapping<Task, Resource>> chooseMappingSubset(Task task,
      Set<Mapping<Task, Resource>> mappingOptions);
}
