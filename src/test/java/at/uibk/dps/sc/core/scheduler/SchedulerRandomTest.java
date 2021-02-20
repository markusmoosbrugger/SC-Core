package at.uibk.dps.sc.core.scheduler;

import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Test;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import static org.mockito.Mockito.mock;

public class SchedulerRandomTest {

  @Test
  public void test() {
    SpecificationProvider mockSpec = mock(SpecificationProvider.class);
    SchedulerRandom tested = new SchedulerRandom(mockSpec, new Random(), 1);
    Task task = new Task("task");
    Resource res = new Resource("res");
    Mapping<Task, Resource> mapping = new Mapping<Task, Resource>("mapping", task, res);
    Set<Mapping<Task, Resource>> subset = new HashSet<>();
    subset.add(mapping);
    assertEquals(mapping, tested.chooseMappingSubset(task, subset).iterator().next());
  }
}
