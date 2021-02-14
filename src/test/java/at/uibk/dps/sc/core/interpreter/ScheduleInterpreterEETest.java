package at.uibk.dps.sc.core.interpreter;

import static org.junit.Assert.*;
import org.junit.Test;
import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.dataflow.Aggregation;
import at.uibk.dps.ee.enactables.local.dataflow.Distribution;
import at.uibk.dps.ee.enactables.local.dataflow.EarliestArrival;
import at.uibk.dps.ee.enactables.local.utility.CollOperFunction;
import at.uibk.dps.ee.enactables.local.utility.ConditionEvaluation;
import at.uibk.dps.ee.model.objects.Condition;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlow;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlow.DataFlowType;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlowCollections.OperationType;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionDataFlowCollections;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtilityCollections.CollectionOperation;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtilityCondition;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtilityCondition.Summary;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtilityCollections;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import static org.mockito.Mockito.spy;
import java.util.HashSet;
import java.util.Set;
import static org.mockito.Mockito.doReturn;

import static org.mockito.Mockito.mock;

public class ScheduleInterpreterEETest {

  @Test
  public void testInterpretSchedule() {
    ScheduleInterpreterEE tested = new ScheduleInterpreterEE();
    ScheduleInterpreterEE spy = spy(tested);
    Task dfTask = PropertyServiceFunctionDataFlow.createDataFlowFunction("taskdf",
        DataFlowType.EarliestInput);
    Task utilTask = PropertyServiceFunctionUtilityCollections.createCollectionOperation("bla",
        "blabla", CollectionOperation.Block);
    EnactmentFunction dataFunc = mock(EnactmentFunction.class);
    EnactmentFunction utilFunc = mock(EnactmentFunction.class);
    doReturn(dataFunc).when(spy).getDataFlowFunction(dfTask);
    doReturn(utilFunc).when(spy).getUtilityFunction(utilTask);
    assertEquals(dataFunc, spy.interpretSchedule(dfTask, new HashSet<>()));
    assertEquals(utilFunc, spy.interpretSchedule(utilTask, new HashSet<>()));

    Mapping<Task, Resource> mapping = new Mapping<>("mapping", utilTask, new Resource("res"));
    Set<Mapping<Task, Resource>> mappings = new HashSet<>();
    mappings.add(mapping);
    try {
      tested.interpretSchedule(utilTask, mappings);
      fail();
    } catch (IllegalArgumentException exc) {
    }
  }

  @Test
  public void testGetDataFlowTask() {
    ScheduleInterpreterEE tested = new ScheduleInterpreterEE();
    Task earliestInTask =
        PropertyServiceFunctionDataFlow.createDataFlowFunction("t", DataFlowType.EarliestInput);
    Task aggrTask = PropertyServiceFunctionDataFlowCollections.createCollectionDataFlowTask("t1",
        OperationType.Aggregation, "scope");
    Task distTask = PropertyServiceFunctionDataFlowCollections.createCollectionDataFlowTask("t1",
        OperationType.Distribution, "scope");
    assertTrue(tested.getDataFlowFunction(earliestInTask) instanceof EarliestArrival);
    assertTrue(tested.getDataFlowFunction(distTask) instanceof Distribution);
    assertTrue(tested.getDataFlowFunction(aggrTask) instanceof Aggregation);
  }

  @Test
  public void testGetUtilityFunction() {
    ScheduleInterpreterEE tested = new ScheduleInterpreterEE();
    Set<Condition> conditions = new HashSet<>();
    Summary summary = Summary.AND;
    Task conditionTask = PropertyServiceFunctionUtilityCondition.createConditionEvaluation("bla",
        conditions, summary);
    Task collectionTask = PropertyServiceFunctionUtilityCollections.createCollectionOperation("bla",
        "blabla", CollectionOperation.Block);
    assertTrue(tested.getUtilityFunction(collectionTask) instanceof CollOperFunction);
    assertTrue(tested.getUtilityFunction(conditionTask) instanceof ConditionEvaluation);
  }
}
