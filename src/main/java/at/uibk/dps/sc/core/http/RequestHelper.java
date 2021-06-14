package at.uibk.dps.sc.core.http;

import at.uibk.dps.ee.enactables.serverless.ConstantsServerless;
import at.uibk.dps.ee.model.constants.ConstantsEEModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RequestHelper {


  public static final MediaType MediaTypeJson = MediaType.get("application/json; charset=utf-8");

  public static OkHttpClient initHttpClient() {
    final OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.connectTimeout(ConstantsEEModel.defaultFaaSTimeoutSeconds, TimeUnit.SECONDS);
    builder.readTimeout(ConstantsServerless.readWriteTimeoutSeconds, TimeUnit.SECONDS);
    builder.writeTimeout(ConstantsServerless.readWriteTimeoutSeconds, TimeUnit.SECONDS);
    return builder.build();

  }

  public static JsonObject sendRequest(OkHttpClient client, String baseUrl, JsonObject input,
      String path) {
    final RequestBody body = RequestBody.create(new Gson().toJson(input), MediaTypeJson);
    String url = baseUrl + path;
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
