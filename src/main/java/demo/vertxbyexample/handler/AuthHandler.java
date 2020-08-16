package demo.vertxbyexample.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;

public class AuthHandler implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext event) {
    String token = event.request()
                        .getHeader("Authorization");

    if (StringUtils.isBlank(token)) {
      event.fail(401);
    } else {
      event.next();
    }
  }
}
