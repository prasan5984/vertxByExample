package demo.vertxbyexample.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.UUID;

public class RequestIDGeneratorHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext event) {
    UUID requestID = UUID.randomUUID();
    event.data()
         .put("requestID", requestID.toString());
    event.next();
  }
}
