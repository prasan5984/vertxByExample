package demo.vertxbyexample.model;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.stream.Collectors;

public class ApiRequest {
  private Map<String, String> pathParams;
  private Map<String, String> queryParams;
  private JsonObject body;

  public ApiRequest() {
  }

  public ApiRequest(RoutingContext context) {
    this.pathParams = context.pathParams();
    this.queryParams = context.queryParams()
                              .entries()
                              .stream()
                              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    //this.body = context.getBodyAsJson();
  }

  public Map<String, String> getQueryParams() {
    return queryParams;
  }

  public JsonObject getBody() {
    return body;
  }

  public Map<String, String> getPathParams() {
    return pathParams;
  }
}
