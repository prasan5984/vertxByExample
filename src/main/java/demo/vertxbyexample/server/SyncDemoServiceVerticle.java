package demo.vertxbyexample.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;

public class SyncDemoServiceVerticle extends AbstractVerticle {

  private boolean inprogress = false;

  private void handle(Message<Object> m) {
    inprogress = true;
    System.out.println(Thread.currentThread()
                             .getName());
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println(this.getVertx()
                           .getOrCreateContext());
    if (!inprogress) {
      System.out.println("Something's wrong");
      System.exit(1);
    }
    m.reply("done");
  }

  @Override
  public void start() throws Exception {
    this.getVertx()
        .eventBus()
        .consumer("service.syncdemo", this::handle);
  }
}
