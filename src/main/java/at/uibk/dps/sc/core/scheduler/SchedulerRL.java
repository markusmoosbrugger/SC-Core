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
  protected Map<String, Set<String>> taskResourceMappings;
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
    this.taskResourceMappings = new HashMap<>();
    this.client = RequestHelper.initHttpClient();
    this.initRLModels(specProvider.getMappings());
  }

  @Override
  protected Set<Mapping<Task, Resource>> chooseMappingSubset(Task task,
      Set<Mapping<Task, Resource>> mappingOptions) {
    String typeID = task.getAttribute("TypeID");
    Set<String> possibleResources = taskResourceMappings.get(typeID);
    // check if correct model has been initialized
    for (Mapping<Task, Resource> mappingOption : mappingOptions) {
      if (mappingOption.getSource().equals(task) && possibleResources.contains(
          mappingOption.getTarget().getId())) {
        continue;
      } else {
        throw new IllegalStateException(
            "Model for these mappings was not initialized previously. ");
      }
    }
    String chosenResource = getResourceForTask(typeID);
    Set<Mapping<Task, Resource>> result = mappingOptions.stream().filter(
            map -> map.getSource().equals(task) && map.getTarget().getId().equals(chosenResource))
        .collect(Collectors.toSet());
    return result;
  }

  private String getResourceForTask(String typeID) {
    JsonObject input = new JsonObject();
    input.add("task", new JsonPrimitive(typeID));
    JsonObject result = RequestHelper.sendRequest(client, ServerBaseUrl, input, "get_resource");
    String implementationID = result.get("implementation_id").getAsString();

    return implementationID;
  }

  private void initRLModels(Mappings<Task, Resource> mappings) {
    for (Mapping<Task, Resource> mapping : mappings) {
      Task task = mapping.getSource();
      String typeID = task.getAttribute("TypeID");
      Resource resource = mapping.getTarget();
      taskResourceMappings.computeIfAbsent(typeID, k -> new HashSet<>()).add(resource.getId());
    }

    taskResourceMappings.forEach((key, value) -> {
      initSingleRLModel(key, value);
    });

    return;
  }

  private void initSingleRLModel(String task, Set<String> resources) {
    JsonObject input = new JsonObject();
    input.add("task", new JsonPrimitive(task));
    JsonArray resourcesJson = new JsonArray();
    resources.forEach((r) -> resourcesJson.add(r));
    input.add("resources", resourcesJson);
    RequestHelper.sendRequest(client, ServerBaseUrl, input, "init_model");
  }

}
