package at.uibk.dps.sc.core.scheduler;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.opt4j.core.start.Constant;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * Random scheduler which excludes the serverless options in case they exceed a
 * defined payload-size threshold.
 * 
 * @author Fedor Smirnov
 *
 */
public class SchedulerDataSize extends SchedulerRandom {

  protected final int sizeThresholdKb;

  @Inject
  public SchedulerDataSize(SpecificationProvider specProvider, Random random,
      @Constant(namespace = SchedulerRandom.class, value = "mappingsToPick") int mappingsToPick,
      @Constant(namespace = SchedulerDataSize.class, value = "sizeThreshold") int sizeThreshold) {
    super(specProvider, random, mappingsToPick);
    this.sizeThresholdKb = sizeThreshold;
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task,
      Set<Mapping<Task, Resource>> mappingOptions) {
    return super.chooseMappingSubset(task, mappingOptions.stream()
        .filter(mapping -> !excludeMapping(mapping, task)).collect(Collectors.toSet()));
  }

  /**
   * Returns true iff the task in the given mapping has a data input smaller than
   * the defined threshold.
   * 
   * @param mapping the given mapping
   * @return true iff the task in the given mapping has a data input smaller than
   *         the defined threshold
   */
  protected boolean excludeMapping(Mapping<Task, Resource> mapping, Task process) {
    JsonObject input = PropertyServiceFunction.getEnactable(process).getInput();
    int byteSize = input.toString().getBytes().length;
    boolean overThreshold = byteSize > (sizeThresholdKb * 1000);
    boolean overAndServerless = overThreshold
        && PropertyServiceMapping.getEnactmentMode(mapping).equals(EnactmentMode.Serverless);
    boolean underAndLocal = !overThreshold
        && PropertyServiceMapping.getEnactmentMode(mapping).equals(EnactmentMode.Local);
    return overAndServerless || underAndLocal;
  }
}
