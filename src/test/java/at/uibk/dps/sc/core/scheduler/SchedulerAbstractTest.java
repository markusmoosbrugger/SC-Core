package at.uibk.dps.sc.core.scheduler;

import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import at.uibk.dps.ee.model.graph.EnactmentGraph;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.ResourceGraph;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUser;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtilityCollections;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUtilityCollections.CollectionOperation;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;;

public class SchedulerAbstractTest {

  protected static class SchedulerMock extends SchedulerAbstract {

    public SchedulerMock(SpecificationProvider specProvider) {
      super(specProvider);
    }

    @Override
    protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task,
        Set<Mapping<Task, Resource>> mappingOptions) {
      Set<Mapping<Task, Resource>> result = new HashSet<>();
      result.add(mappingOptions.iterator().next());
      return result;
    }
  }

  @Test
  public void testUser() {
    Task task = PropertyServiceFunctionUser.createUserTask("task", "addition");
    Resource res = new Resource("res");
    Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", task, res);
    Set<Mapping<Task, Resource>> expected = new HashSet<>();
    expected.add(mapping);
    Mappings<Task, Resource> mappings = new Mappings<>();
    mappings.add(mapping);
    EnactmentGraph eGraph = new EnactmentGraph();
    ResourceGraph rGraph = new ResourceGraph();
    EnactmentSpecification spec = new EnactmentSpecification(eGraph, rGraph, mappings);
    SpecificationProvider providerMock = mock(SpecificationProvider.class);
    when(providerMock.getMappings()).thenReturn(mappings);
    when(providerMock.getSpecification()).thenReturn(spec);

    SchedulerMock tested = new SchedulerMock(providerMock);
    SchedulerMock testedSpy = spy(tested);

    assertTrue(testedSpy.scheduleTask(task).size() == 1);
    verify(testedSpy).chooseMappingSubset(task, expected);
  }

  @Test
  public void testUtility() {
    Task task = PropertyServiceFunctionUtilityCollections.createCollectionOperation("task", "bla",
        CollectionOperation.Block);
    Resource res = new Resource("res");
    Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", task, res);
    Set<Mapping<Task, Resource>> expected = new HashSet<>();
    expected.add(mapping);
    Mappings<Task, Resource> mappings = new Mappings<>();
    mappings.add(mapping);
    EnactmentGraph eGraph = new EnactmentGraph();
    ResourceGraph rGraph = new ResourceGraph();
    EnactmentSpecification spec = new EnactmentSpecification(eGraph, rGraph, mappings);
    SpecificationProvider providerMock = mock(SpecificationProvider.class);
    when(providerMock.getMappings()).thenReturn(mappings);
    when(providerMock.getSpecification()).thenReturn(spec);

    SchedulerMock tested = new SchedulerMock(providerMock);
    SchedulerMock testedSpy = spy(tested);

    Set<Mapping<Task, Resource>> result = testedSpy.scheduleTask(task);
    assertTrue(result.isEmpty());
    verify(testedSpy, never()).chooseMappingSubset(task, expected);
  }
}
