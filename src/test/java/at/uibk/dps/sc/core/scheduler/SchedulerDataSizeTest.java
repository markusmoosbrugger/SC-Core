package at.uibk.dps.sc.core.scheduler;

import static org.junit.Assert.*;
import org.junit.Test;
import com.google.gson.JsonObject;
import at.uibk.dps.ee.enactables.EnactableAtomic;
import at.uibk.dps.ee.model.graph.EnactmentSpecification;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.ee.model.properties.PropertyServiceFunction;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping;
import at.uibk.dps.ee.model.properties.PropertyServiceMapping.EnactmentMode;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import static org.mockito.Mockito.mock;
import java.util.Random;

import static org.mockito.Mockito.when;

public class SchedulerDataSizeTest {

  @Test
  public void testUnderThresh() {
    SpecificationProvider provMock = mock(SpecificationProvider.class);
    EnactmentSpecification mockSpec = mock(EnactmentSpecification.class);
    Mappings<Task, Resource> mappings = new Mappings<>();
    when(mockSpec.getMappings()).thenReturn(mappings);
    when(provMock.getSpecification()).thenReturn(mockSpec);
    SchedulerDataSize tested = new SchedulerDataSize(provMock, new Random(), 1, 1);
    Task task = new Task("task");
    EnactableAtomic enactable = mock(EnactableAtomic.class);
    JsonObject empty = new JsonObject();
    when(enactable.getInput()).thenReturn(empty);
    PropertyServiceFunction.setEnactable(task, enactable);
    Resource res = new Resource("res");
    Mapping<Task, Resource> serverlessMapping =
        PropertyServiceMapping.createMapping(task, res, EnactmentMode.Serverless, "serv");
    Mapping<Task, Resource> localMapping =
        PropertyServiceMapping.createMapping(task, res, EnactmentMode.Local, "serv");
    assertTrue(tested.excludeMapping(localMapping, task));
    assertFalse(tested.excludeMapping(serverlessMapping, task));
  }

  @Test
  public void testOverThresh() {
    SpecificationProvider provMock = mock(SpecificationProvider.class);
    EnactmentSpecification mockSpec = mock(EnactmentSpecification.class);
    Mappings<Task, Resource> mappings = new Mappings<>();
    when(mockSpec.getMappings()).thenReturn(mappings);
    when(provMock.getSpecification()).thenReturn(mockSpec);
    SchedulerDataSize tested = new SchedulerDataSize(provMock, new Random(), 1, 0);
    Task task = new Task("task");
    EnactableAtomic enactable = mock(EnactableAtomic.class);
    JsonObject empty = new JsonObject();
    when(enactable.getInput()).thenReturn(empty);
    PropertyServiceFunction.setEnactable(task, enactable);
    Resource res = new Resource("res");
    Mapping<Task, Resource> serverlessMapping =
        PropertyServiceMapping.createMapping(task, res, EnactmentMode.Serverless, "serv");
    Mapping<Task, Resource> localMapping =
        PropertyServiceMapping.createMapping(task, res, EnactmentMode.Local, "serv");
    assertFalse(tested.excludeMapping(localMapping, task));
    assertTrue(tested.excludeMapping(serverlessMapping, task));
  }
}
