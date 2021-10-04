package at.uibk.dps.sc.core.scheduler;

import at.uibk.dps.ee.model.graph.SpecificationProvider;
import at.uibk.dps.sc.core.http.RequestHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.inject.Inject;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import okhttp3.OkHttpClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SchedulerRL extends SchedulerAbstract {
  protected Map<String, Set<String>> typeResourceMappings;
  protected final OkHttpClient client;
  protected static final String ServerBaseUrl = "http://localhost:5000/";

  /**
   * The injection constructor.
   *
   * @param specProvider the specification provider
   */
  @Inject
  public SchedulerRL(final SpecificationProvider specProvider) {
    super(specProvider);
    this.typeResourceMappings = new HashMap<>();
    this.client = RequestHelper.initHttpClient();
    this.initRLModels(specProvider.getMappings());
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task,
      Set<Mapping<Task, Resource>> mappingOptions) {
    String typeId = task.getAttribute("TypeID");
    Set<String> possibleResources = typeResourceMappings.get(typeId);
    // check if correct model has been initialized
    final Task parentTask;
    if (task.getParent() != null) {
      parentTask = (Task) task.getParent();
    } else {
      parentTask = task;
    }
    for (Mapping<Task, Resource> mappingOption : mappingOptions) {
      // use parent task if it exists
      if (mappingOption.getSource().equals(parentTask) && possibleResources.contains(
          mappingOption.getTarget().getId())) {
        continue;
      } else {
        throw new IllegalStateException(
            "Model for these mappings was not initialized previously. ");
      }
    }
    final String chosenImplementation = getImplementationForTask(typeId, task.getId());
    Set<Mapping<Task, Resource>> result = mappingOptions.stream().filter(
        map -> map.getSource().equals(parentTask) && map.getTarget().getId()
            .equals(chosenImplementation)).collect(Collectors.toSet());
    return result;
  }

  private String getImplementationForTask(String typeId, String taskId) {
    JsonObject input = new JsonObject();
    input.add("type_id", new JsonPrimitive(typeId));
    input.add("task_id", new JsonPrimitive(taskId));
    JsonObject result =
        RequestHelper.sendRequest(client, ServerBaseUrl, input, "get_implementation");
    String implementationID = result.get("implementation_id").getAsString();

    return implementationID;
  }

  private void initRLModels(Mappings<Task, Resource> mappings) {
    for (Mapping<Task, Resource> mapping : mappings) {
      Task task = mapping.getSource();
      String typeId = task.getAttribute("TypeID");
      Resource resource = mapping.getTarget();
      typeResourceMappings.computeIfAbsent(typeId, k -> new HashSet<>()).add(resource.getId());
    }

    typeResourceMappings.forEach((key, value) -> {
      initSingleRLModel(key, value);
    });

    return;
  }

  private void initSingleRLModel(String typeId, Set<String> implementations) {
    JsonObject input = new JsonObject();
    input.add("type_id", new JsonPrimitive(typeId));
    JsonArray implementationsJson = new JsonArray();
    implementations.forEach((r) -> implementationsJson.add(r));
    input.add("implementations", implementationsJson);
    RequestHelper.sendRequest(client, ServerBaseUrl, input, "init_model");
  }

}
