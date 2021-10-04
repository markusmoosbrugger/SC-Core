package at.uibk.dps.sc.core.decorators;

import at.uibk.dps.ee.core.enactable.EnactmentStateListener;
import at.uibk.dps.sc.core.http.RequestHelper;
import okhttp3.OkHttpClient;

/**
 * The {@link EnactmentModelWriter} is used to send a request to
 * Pythia-ML after every termination of an enactment sequence.
 *
 * @author Markus Moosbrugger
 */
public class EnactmentModelWriter implements EnactmentStateListener {

  protected final OkHttpClient client;
  protected static final String ServerBaseUrl = "http://localhost:5000/";


  public EnactmentModelWriter() {
    this.client = RequestHelper.initHttpClient();
  }

  @Override
  public void enactmentStarted() {
  }

  @Override
  public void enactmentTerminated() {
    RequestHelper.sendRequest(client, ServerBaseUrl, null, "save_all");
  }
}
