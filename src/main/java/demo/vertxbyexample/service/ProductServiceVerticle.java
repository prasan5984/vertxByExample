package demo.vertxbyexample.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class ProductServiceVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {
    super.start();
    this.getVertx()
        .eventBus()
        .consumer("service.getProduct", this::getProduct);
  }

  private void getProduct(Message<Integer> message) {
    Integer productId = message.body();
    this.getVertx()
        .eventBus().<JsonObject>request("dao.getProduct", productId, r -> processGetProduct(message, r));

  }

  private void processGetProduct(Message<Integer> message, AsyncResult<Message<JsonObject>> result) {
    if (result.succeeded()) {
      JsonObject response = result.result()
                                  .body();
      if (response != null) {
        message.reply(response);
      } else {
        message.fail(404, "Product Id not found");
      }
    } else {
      message.fail(500, result.cause()
                              .getMessage());
    }
  }
}
