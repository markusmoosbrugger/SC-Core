package at.uibk.dps.sc.core.scheduler;

import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import at.uibk.dps.ee.model.graph.EnactmentGraph;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.ResourceGraph;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.spy;;

public class SchedulerAbstractTest {

  protected static class SchedulerMock extends SchedulerAbstract {

    public SchedulerMock(SpecificationProvider specProvider) {
      super(specProvider);
    }

    @Override
    protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task,
        Set<Mapping<Task, Resource>> mappingOptions) {
      return new HashSet<>();
    }
  }

  @Test
  public void test() {
    Task task = new Task("task");
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

    assertTrue(testedSpy.scheduleTask(task).isEmpty());
    verify(testedSpy).chooseMappingSubset(task, expected);
  }
}
