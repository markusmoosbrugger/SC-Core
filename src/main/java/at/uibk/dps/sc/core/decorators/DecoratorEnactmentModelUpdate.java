package at.uibk.dps.sc.core.decorators;

import at.uibk.dps.ee.core.enactable.EnactmentFunction;
import at.uibk.dps.ee.core.enactable.EnactmentFunctionDecorator;
import at.uibk.dps.sc.core.http.RequestHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import okhttp3.OkHttpClient;

import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;

/**
 * The {@link DecoratorEnactmentModelUpdate} sends a request to Pythia-ML after
 * every enactment in order to update the RL model.
 *
 * @author Markus Moosbrugger
 */
public class DecoratorEnactmentModelUpdate extends EnactmentFunctionDecorator {

  protected Instant start;
  protected final OkHttpClient client;
  protected static final String ServerBaseUrl = "http://localhost:5000/";

  /**
   * Default constructor.
   *
   * @param decoratedFunction the function whose execution properties are logged.
   */
  public DecoratorEnactmentModelUpdate(final EnactmentFunction decoratedFunction) {
    super(decoratedFunction);
    this.client = RequestHelper.initHttpClient();
  }

  @Override
  protected JsonObject preprocess(final JsonObject input) {
    start = Instant.now();

    return input;
  }

  @Override
  protected JsonObject postprocess(final JsonObject result) {
    final Instant now = Instant.now();
    final long executionTime = Duration.between(start, now).toMillis();
    updateModel(executionTime);
    return result;
  }


  /**
   * Updates the RL model of Pythia-ML.
   *
   * @param executionTime
   */
  private void updateModel(long executionTime) {
    JsonObject input = new JsonObject();

    input.add("type_id", new JsonPrimitive(decoratedFunction.getTypeId()));
    for (AbstractMap.SimpleEntry entry : decoratedFunction.getAdditionalAttributes()) {
      if (entry.getKey().equals("taskId")) {
        input.add("task_id", new JsonPrimitive((String) entry.getValue()));
      }
    }
    input.add("execution_time", new JsonPrimitive(executionTime));
    input.add("implementation_id", new JsonPrimitive(decoratedFunction.getImplementationId()));
    input.add("timestamp", new JsonPrimitive(Instant.now().toEpochMilli()));
    RequestHelper.sendRequest(client, ServerBaseUrl, input, "update");
  }
}
