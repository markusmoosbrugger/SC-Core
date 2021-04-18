package at.uibk.dps.sc.core.interpreter;

import static org.junit.Assert.*;
import org.junit.Test;
import at.uibk.dps.ee.enactables.local.LocalFunctionAbstract;
import at.uibk.dps.ee.enactables.local.ConstantsLocal.LocalCalculations;
import at.uibk.dps.ee.enactables.local.calculation.FunctionFactoryLocal;
import at.uibk.dps.ee.enactables.serverless.FunctionFactoryServerless;
import at.uibk.dps.ee.model.constants.ConstantsEEModel;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUser;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
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
    Resource local = PropertyServiceResource.createResource("res");
    Mapping<Task, Resource> localMapping = PropertyServiceMapping.createMapping(task, local,
        EnactmentMode.Local, ConstantsEEModel.implIdLocalNative);
    Set<Mapping<Task, Resource>> localSchedule = new HashSet<>();
    localSchedule.add(localMapping);
    LocalFunctionAbstract functionMockLockal = mock(LocalFunctionAbstract.class);
    FunctionFactoryLocal factoryMock = mock(FunctionFactoryLocal.class);
    FunctionFactoryServerless mockFacSl = mock(FunctionFactoryServerless.class);
    when(factoryMock.getLocalFunction(LocalCalculations.Addition)).thenReturn(functionMockLockal);
    ScheduleInterpreterUserSingle tested =
        new ScheduleInterpreterUserSingle(factoryMock, mockFacSl);
    assertEquals(functionMockLockal, tested.interpretSchedule(task, localSchedule));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCheckSchedule() {
    FunctionFactoryLocal mockFactory = mock(FunctionFactoryLocal.class);
    FunctionFactoryServerless mockFacSl = mock(FunctionFactoryServerless.class);
    ScheduleInterpreterUserSingle tested =
        new ScheduleInterpreterUserSingle(mockFactory, mockFacSl);
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
