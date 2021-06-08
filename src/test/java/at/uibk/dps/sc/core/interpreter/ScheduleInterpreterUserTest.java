package at.uibk.dps.sc.core.interpreter;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.mockito.Mockito;
import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.enactables.local.LocalFunctionAbstract;
import at.uibk.dps.ee.enactables.local.container.ContainerFunction;
import at.uibk.dps.ee.enactables.local.container.FunctionFactoryLocal;
import at.uibk.dps.ee.enactables.local.demo.FunctionFactoryDemo;
import at.uibk.dps.ee.enactables.serverless.FunctionFactoryServerless;
import at.uibk.dps.ee.enactables.serverless.ServerlessFunction;
import at.uibk.dps.ee.model.constants.ConstantsEEModel;
import at.uibk.dps.ee.model.properties.PropertyServiceFunctionUser;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import at.uibk.dps.ee.model.properties.PropertyServiceResource;
import at.uibk.dps.ee.model.properties.PropertyServiceResourceServerless;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import static org.junit.Assert.*;

public class ScheduleInterpreterUserTest {

  protected static class InterpreterMock extends ScheduleInterpreterUser {

    public InterpreterMock(FunctionFactoryLocal localFunctionFactory,
        FunctionFactoryServerless functionFacSl, FunctionFactoryDemo factoryDemo) {
      super(localFunctionFactory, functionFacSl, factoryDemo);
    }

    @Override
    protected EnactmentFunction interpretScheduleUser(Task task,
        Set<Mapping<Task, Resource>> scheduleModel) {
      return null;
    }
  }

  @Test
  public void getFunctionForMappingTest() {
    Task task = PropertyServiceFunctionUser.createUserTask("task", "Addition");
    Resource local = PropertyServiceResource.createResource("res");
    Resource serverless =
        PropertyServiceResourceServerless.createServerlessResource("res", "resLink");
    Mapping<Task, Resource> localMapping = PropertyServiceMapping.createMapping(task, local,
        EnactmentMode.Local, ConstantsEEModel.implIdLocalNative);
    Mapping<Task, Resource> serverlessMapping =
        PropertyServiceMapping.createMapping(task, serverless, EnactmentMode.Serverless, "resLink");
    LocalFunctionAbstract functionMockLockal = mock(LocalFunctionAbstract.class);
    FunctionFactoryLocal factoryMock = mock(FunctionFactoryLocal.class);
    FunctionFactoryServerless mockFacSl = mock(FunctionFactoryServerless.class);
    FunctionFactoryDemo demoMock = mock(FunctionFactoryDemo.class);
    InterpreterMock tested = new InterpreterMock(factoryMock, mockFacSl, demoMock);
    InterpreterMock spy = spy(tested);
    EnactmentFunction serverlessFunc = mock(EnactmentFunction.class);
    Mockito.doReturn(serverlessFunc).when(spy).interpretServerless(serverlessMapping);
    Mockito.doReturn(functionMockLockal).when(spy).interpretLocal(localMapping);
    assertEquals(serverlessFunc, spy.getFunctionForMapping(serverlessMapping));
    assertEquals(functionMockLockal, spy.getFunctionForMapping(localMapping));
  }

  @Test
  public void interpretLocalTest() {
    Task task = PropertyServiceFunctionUser.createUserTask("id", "Addition");
    Resource res = new Resource("res");
    ContainerFunction functionMock = mock(ContainerFunction.class);
    FunctionFactoryLocal factoryMock = mock(FunctionFactoryLocal.class);
    Mapping<Task, Resource> localMapping = new Mapping<Task, Resource>("bla", task, res);
    when(factoryMock.getContainerFunction(localMapping)).thenReturn(functionMock);
    FunctionFactoryServerless mockFacSl = mock(FunctionFactoryServerless.class);
    FunctionFactoryDemo demoMock = mock(FunctionFactoryDemo.class);
    InterpreterMock tested = new InterpreterMock(factoryMock, mockFacSl, demoMock);
    assertEquals(functionMock, tested.interpretLocal(localMapping));
  }

  @Test
  public void interpretServerlessTest() {
    Task task = PropertyServiceFunctionUser.createUserTask("task", "fancyType");
    String resLink = "link";
    Resource res = PropertyServiceResourceServerless.createServerlessResource("res", resLink);
    Mapping<Task, Resource> mapping =
        PropertyServiceMapping.createMapping(task, res, EnactmentMode.Serverless, resLink);
    FunctionFactoryLocal factoryMock = mock(FunctionFactoryLocal.class);
    FunctionFactoryServerless mockFacSl = mock(FunctionFactoryServerless.class);
    ServerlessFunction slFuncMock = mock(ServerlessFunction.class);
    when(mockFacSl.createServerlessFunction(mapping)).thenReturn(slFuncMock);
    FunctionFactoryDemo demoMock = mock(FunctionFactoryDemo.class);
    InterpreterMock tested = new InterpreterMock(factoryMock, mockFacSl, demoMock);
    assertEquals(slFuncMock, tested.interpretServerless(mapping));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEmptyMapping() {
    Task task = new Task("task");
    Set<Mapping<Task, Resource>> schedule = new HashSet<>();
    FunctionFactoryServerless mockFacSl = mock(FunctionFactoryServerless.class);
    FunctionFactoryDemo demoMock = mock(FunctionFactoryDemo.class);
    InterpreterMock tested = new InterpreterMock(mock(FunctionFactoryLocal.class), mockFacSl, demoMock);
    tested.interpretSchedule(task, schedule);
  }
}
