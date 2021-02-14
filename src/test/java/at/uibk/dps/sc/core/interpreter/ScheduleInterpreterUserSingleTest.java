package at.uibk.dps.sc.core.interpreter;

import static org.junit.Assert.*;
import org.junit.Test;
import at.uibk.dps.ee.enactables.local.LocalFunctionAbstract;
import at.uibk.dps.ee.enactables.local.ConstantsLocal.LocalCalculations;
import at.uibk.dps.ee.enactables.local.calculation.LocalFunctionFactory;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUser;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
import at.uibk.dps.ee.model.properties.PropertyServiceResource.ResourceType;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.HashSet;
import java.util.Set;

public class ScheduleInterpreterUserSingleTest {

  @Test
  public void test() {
    Task task = PropertyServiceFunctionUser.createUserTask("task", "Addition");
    Resource local = PropertyServiceResource.createResource("res", ResourceType.Local);
    Mapping<Task, Resource> localMapping = new Mapping<Task, Resource>("local", task, local);
    Set<Mapping<Task, Resource>> localSchedule = new HashSet<>();
    localSchedule.add(localMapping);
    LocalFunctionAbstract functionMockLockal = mock(LocalFunctionAbstract.class);
    LocalFunctionFactory factoryMock = mock(LocalFunctionFactory.class);
    when(factoryMock.getLocalFunction(LocalCalculations.Addition)).thenReturn(functionMockLockal);
    ScheduleInterpreterUserSingle tested = new ScheduleInterpreterUserSingle(factoryMock);
    assertEquals(functionMockLockal, tested.interpretSchedule(task, localSchedule));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCheckSchedule() {
    LocalFunctionFactory mockFactory = mock(LocalFunctionFactory.class);
    ScheduleInterpreterUserSingle tested = new ScheduleInterpreterUserSingle(mockFactory);
    Task task = new Task("task");
    Resource res = new Resource("res");
    Resource res2 = new Resource("res2");
    Mapping<Task, Resource> m1 = new Mapping<>("m1", task, res);
    Mapping<Task, Resource> m2 = new Mapping<>("m2", task, res2);
    Set<Mapping<Task, Resource>> schedule = new HashSet<>();
    schedule.add(m1);
    schedule.add(m2);
    tested.checkSchedule(task, schedule);
  }
}
