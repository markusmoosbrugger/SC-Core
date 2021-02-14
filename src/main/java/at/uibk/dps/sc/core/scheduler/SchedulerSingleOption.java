package at.uibk.dps.sc.core.scheduler;

import java.util.HashSet;
import java.util.Set;
import com.google.inject.Inject;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link SchedulerSingleOption} is used in cases where exactly one mapping
 * is provided for each task (enactments which are to run with the specified
 * binding, without any scheduling decisions).
 * 
 * @author Fedor Smirnov
 */
public class SchedulerSingleOption extends SchedulerAbstract {

  /**
   * Injection constructor; Same as parent.
   * 
   * @param specProvider
   */
  @Inject
  public SchedulerSingleOption(final SpecificationProvider specProvider) {
    super(specProvider);
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(final Task task,
      final Set<Mapping<Task, Resource>> mappingOptions) {
    if (mappingOptions.size() != 1) {
      throw new IllegalArgumentException("More than one mapping provided for task " + task.getId());
    }
    return new HashSet<>(mappingOptions);
  }
}
