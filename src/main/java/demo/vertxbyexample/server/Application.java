package demo.vertxbyexample.server;

import demo.vertxbyexample.api.ProductApiVerticle;
import demo.vertxbyexample.dao.ProductDao;
import demo.vertxbyexample.handler.ApiHandler;
import demo.vertxbyexample.handler.AuthHandler;
import demo.vertxbyexample.handler.RequestIDGeneratorHandler;
import demo.vertxbyexample.service.ProductServiceVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.BodyHandlerImpl;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

public class Application {

  private JsonObject appOptions;
  private Vertx vertxInstance;
  private Pool pool;

  public static void main(String[] args) {
    new Application().start(new JsonObject());
  }

  private void start(JsonObject appOptions) {
    this.appOptions = appOptions;
    initVertx();
    initializeDBPool();
    initializeVerticles();
    startHttpServer();
  }

  private void initializeVerticles() {
    vertxInstance.deployVerticle(new ProductApiVerticle());
    vertxInstance.deployVerticle(new ProductServiceVerticle());
    vertxInstance.deployVerticle(new ProductDao(this.pool));
    /*vertxInstance.deployVerticle(new SyncDemoServiceVerticle());*/
/*
    vertxInstance.deployVerticle(SyncDemoServiceVerticle.class, new DeploymentOptions().setInstances(8));
*/
    vertxInstance.deployVerticle(SyncDemoServiceVerticle.class, new DeploymentOptions().setWorker(true)
                                                                                       .setWorkerPoolSize(8)
    );
  }

  private void initVertx() {
    VertxOptions vertxOptions = new VertxOptions()
      .setEventLoopPoolSize(this.appOptions.getInteger("event_pool_size", VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE));

    vertxInstance = Vertx.vertx(vertxOptions);
  }

  private Router getRouter() {
    Router router = Router.router(getVertxInstance());

    router.route(HttpMethod.GET, "/product/:productId")
          .handler(rc -> putApiKey(rc, "getProduct"))
          .handler(new RequestIDGeneratorHandler())
          .handler(new AuthHandler())
          .handler(new BodyHandlerImpl())
          .handler(new ApiHandler());

    router.route(HttpMethod.GET, "/syncdemo")
          .handler(new SynchronizationDemoHandler());

    return router;
  }

  private void putApiKey(RoutingContext rc, String apiKey) {
    rc.data()
      .put("API_KEY", apiKey);
    rc.next();
  }

  public Vertx getVertxInstance() {
    return vertxInstance;
  }

  private void startHttpServer() {
    HttpServerOptions httpServerOptions = new HttpServerOptions().setPort(appOptions.getInteger("port", 8080));
    getVertxInstance().createHttpServer(httpServerOptions)
                      .requestHandler(getRouter())
                      .listen();

  }

  private void initializeDBPool() {

    MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setHost("localhost")
                                                                       .setPort(3306)
                                                                       .setDatabase("demo")
                                                                       .setUser("root")
                                                                       .setPassword("root");
    PoolOptions poolOptions = new PoolOptions().setMaxSize(4);
    this.pool = MySQLPool.pool(vertxInstance, mySQLConnectOptions, poolOptions);
  }

}
