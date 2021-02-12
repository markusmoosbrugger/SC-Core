package at.uibk.dps.sc.core.interpreter;

import java.util.Set;

import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.dataflow.Aggregation;
import at.uibk.dps.ee.enactables.local.dataflow.Distribution;
import at.uibk.dps.ee.enactables.local.dataflow.EarliestArrival;
import at.uibk.dps.ee.enactables.local.utility.CollOperEnactable;
import at.uibk.dps.ee.enactables.local.utility.ConditionEvaluation;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction.UsageType;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlow;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlow.DataFlowType;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlowCollections;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlowCollections.OperationType;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtility;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtility.UtilityType;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

/**
 * The {@link ScheduleInterpreterEE} provides the schedules for the tasks representing the internal
 * operations of the EE.
 * 
 * @author Fedor Smirnov
 *
 */
public class ScheduleInterpreterEE implements ScheduleInterpreter {

  @Override
  public EnactmentFunction interpretSchedule(Task task,
      Set<Mapping<Task, Resource>> scheduleModel) {
    if (!scheduleModel.isEmpty()) {
      throw new IllegalArgumentException("EE tasks should not be annotated with mappings.");
    }
    UsageType usage = PropertyServiceFunction.getUsageType(task);
    if (usage.equals(UsageType.DataFlow)) {
      return getDataFlowFunction(task);
    } else if (usage.equals(UsageType.Utility)) {
      return getUtilityFunction(task);
    } else {
      throw new IllegalArgumentException("Neither a utility nor a data flow task: " + task.getId());
    }
  }

  /**
   * Returns the data flow function for the given task.
   * 
   * @param task the given task.
   * @return the data flow function for the given task
   */
  protected EnactmentFunction getDataFlowFunction(Task task) {
    DataFlowType dfType = PropertyServiceFunctionDataFlow.getDataFlowType(task);
    if (dfType.equals(DataFlowType.EarliestInput)) {
      return new EarliestArrival();
    } else if (dfType.equals(DataFlowType.Collections)) {
      OperationType oType = PropertyServiceFunctionDataFlowCollections.getOperationType(task);
      if (oType.equals(OperationType.Aggregation)) {
        return new Aggregation();
      } else if (oType.equals(OperationType.Distribution)) {
        return new Distribution(task);
      } else {
        throw new IllegalArgumentException("Unknown coll operation type: " + oType.name());
      }
    } else {
      throw new IllegalArgumentException("Unknown data flow type: " + dfType.name());
    }
  }

  /**
   * Returns the utility function for the given task.
   * 
   * @param task the given task.
   * @return the utility function for the given task
   */
  protected EnactmentFunction getUtilityFunction(Task task) {
    UtilityType utilType = PropertyServiceFunctionUtility.getUtilityType(task);
    if (utilType.equals(UtilityType.Condition)) {
      return new ConditionEvaluation(task);
    } else if (utilType.equals(UtilityType.CollectionOperation)) {
      return new CollOperEnactable(task);
    } else {
      throw new IllegalArgumentException("Unknown utility type: " + utilType.name());
    }
  }
}
