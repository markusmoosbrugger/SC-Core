package at.uibk.dps.sc.core.scheduler;

import at.uibk.dps.ee.enactables.serverless.ConstantsServerless;
import at.uibk.dps.ee.model.constants.ConstantsEEModel;
import at.uibk.dps.ee.model.graph.SpecificationProvider;
import com.google.gson.*;
import com.google.inject.Inject;
import net.sf.opendse.model.Mapping;
import net.sf.opendse.model.Mappings;
import net.sf.opendse.model.Resource;
import net.sf.opendse.model.Task;
import okhttp3.*;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SchedulerRL extends SchedulerAbstract {
  protected Map<String, Set<String>> taskResourceMappings;
  protected final OkHttpClient client;
  protected final String ServerBaseUrl = "http://localhost:5000/";
  public static final MediaType MediaTypeJson = MediaType.get("application/json; charset=utf-8");

  /**
   * The injection constructor.
   *
   * @param specProvider the specification provider
   */
  @Inject
  public SchedulerRL(final SpecificationProvider specProvider) {
    super(specProvider);
    this.taskResourceMappings = new HashMap<>();
    this.client = initHttpClient();
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

  private void saveModel(String typeID) {
    JsonObject input = new JsonObject();
    input.add("task", new JsonPrimitive(typeID));
    sendRequest(input, "save_model");
  }

  private String getResourceForTask(String typeID) {
    JsonObject input = new JsonObject();
    input.add("task", new JsonPrimitive(typeID));
    JsonObject result = sendRequest(input, "get_resource");
    String implementationID = result.get("implementation_id").getAsString();

    // TODO update model with execution time
    updateModel(typeID, implementationID, 2000);
    // TODO only save model at the end of workflow; currently we save it every
    //  timestep
    saveModel(typeID);
    return implementationID;
  }

  private void updateModel(String typeID, String implementationID, int executionTime) {
    JsonObject input = new JsonObject();
    input.add("task", new JsonPrimitive(typeID));
    input.add("execution_time", new JsonPrimitive(executionTime));
    input.add("resource", new JsonPrimitive(implementationID));
    input.add("timestamp", new JsonPrimitive(Instant.now().toEpochMilli()));
    sendRequest(input, "update");
  }



  private void initRLModels(Mappings<Task, Resource> mappings) {
    Set<Mapping<Task, Resource>> mappingOptions = mappings.getAll();
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
    sendRequest(input, "init_model");
  }

  private OkHttpClient initHttpClient() {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(ConstantsEEModel.defaultFaaSTimeoutSeconds, TimeUnit.SECONDS);
    builder.readTimeout(ConstantsServerless.readWriteTimeoutSeconds, TimeUnit.SECONDS);
    builder.writeTimeout(ConstantsServerless.readWriteTimeoutSeconds, TimeUnit.SECONDS);
    return builder.build();

  }

  private JsonObject sendRequest(JsonObject input, String path) {
    final RequestBody body = RequestBody.create(new Gson().toJson(input), MediaTypeJson);
    String url = ServerBaseUrl + path;
    final Request request = new Request.Builder().url(url).post(body).build();
    try (Response response = client.newCall(request).execute()) {
      final String resultString = response.body().string();
      JsonObject result = JsonParser.parseString(resultString).getAsJsonObject();
      if (result.has("success") && result.get("success").getAsBoolean() == false) {
        throw new IllegalStateException("HTTP Request failed.");
      }
      return result;
    } catch (IOException e) {
      throw new IllegalStateException(
          "IOException when initializing the model with the url:\n" + url, e);
    }
  }
}
