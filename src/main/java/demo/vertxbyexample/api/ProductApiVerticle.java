package demo.vertxbyexample.api;

import demo.vertxbyexample.model.ApiRequest;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class ProductApiVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    this.vertx.eventBus()
              .consumer("getProduct", this::processGetProduct);
  }

  private void processGetProduct(Message<JsonObject> message) {
    ApiRequest apiRequest = message.body()
                                   .mapTo(ApiRequest.class);
    String productId = apiRequest.getPathParams()
                                           .get("productId");
    this.vertx.eventBus().<JsonObject>request("service.getProduct", Integer.parseInt(productId), r -> processResponse(r, message));
  }

  private void processResponse(AsyncResult<Message<JsonObject>> asyncResult, Message m) {
    if (asyncResult.succeeded()) {
      m.reply(asyncResult.result()
                         .body());
    } else {
      m.fail(500, asyncResult.cause()
                             .getMessage());
    }

  }
}
