package at.uibk.dps.sc.core.scheduler;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import at.uibk.dps.ee.model.graph.EnactmentGraph;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.ResourceGraph;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUser;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

public class SchedulerSingleOptionTest {

  @Test
  public void test() {
    Task task = PropertyServiceFunctionUser.createUserTask("bla", "addition");
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
    SchedulerSingleOption tested = new SchedulerSingleOption(providerMock);
    assertEquals(expected, tested.scheduleTask(task));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoreThanOne() {
    Task task = PropertyServiceFunctionUser.createUserTask("bla", "addition");
    Resource res = new Resource("res");
    Resource res2 = new Resource("res2");
    Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("m", task, res);
    Mapping<Task, Resource> mapping2 = new Mapping<Task, Resource>("m2", task, res2);
    Mappings<Task, Resource> mappings = new Mappings<>();
    mappings.add(mapping);
    mappings.add(mapping2);
    EnactmentGraph eGraph = new EnactmentGraph();
    ResourceGraph rGraph = new ResourceGraph();
    EnactmentSpecification spec = new EnactmentSpecification(eGraph, rGraph, mappings);
    SpecificationProvider providerMock = mock(SpecificationProvider.class);
    when(providerMock.getMappings()).thenReturn(mappings);
    when(providerMock.getSpecification()).thenReturn(spec);
    SchedulerSingleOption tested = new SchedulerSingleOption(providerMock);
    tested.scheduleTask(task);
  }
}
