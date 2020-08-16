package demo.vertxbyexample.server;


import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

public class SynchronizationDemoHandler implements Handler<RoutingContext> {


  @Override
  public void handle(RoutingContext event) {
    System.out.println("Sync Demo" + Thread.currentThread()
                                           .getName());
    Vertx.currentContext()
         .owner()
         .eventBus()
         .request("service.syncdemo", null, r -> event.end());
    // event.end();
  }
}
