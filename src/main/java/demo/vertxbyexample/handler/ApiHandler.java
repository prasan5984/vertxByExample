package demo.vertxbyexample.handler;

import demo.vertxbyexample.model.ApiRequest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ApiHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext event) {
    String apiKey = (String) event.data()
                                  .get("API_KEY");
    HttpServerRequest httpServerRequest = event.request();
    Vertx.currentContext()
         .owner()
         .eventBus()
      .<JsonObject>request(apiKey,
        JsonObject.mapFrom(new ApiRequest(event)),
        r -> this.processResponse(r, httpServerRequest));
  }

  private void processResponse(AsyncResult<Message<JsonObject>> asyncResult, HttpServerRequest request) {
    if (asyncResult.succeeded()) {
      request.response()
             .end(asyncResult.result()
                             .body()
                             .encode());
    } else {
      request.response()
             .end(asyncResult.cause()
                             .getMessage());
    }

  }


}
