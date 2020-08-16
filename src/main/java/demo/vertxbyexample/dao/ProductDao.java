package demo.vertxbyexample.dao;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.*;

import java.util.function.Function;

public class ProductDao extends AbstractVerticle {

  private static Function<Row, JsonObject> productMapper = row -> new JsonObject().put("productId", row.getValue(0))
                                                                                  .put("productName", row.getValue(1));


  private final Pool pool;

  public ProductDao(Pool pool) {
    this.pool = pool;
  }

  @Override
  public void start() throws Exception {
    super.start();
    this.getVertx()
        .eventBus()
        .consumer("dao.getProduct", this::getProduct);
  }

  private void getProduct(Message<Integer> message) {
    Integer productId = message.body();
    pool.getConnection(connectionResult -> {
      if (connectionResult.succeeded()) {
        SqlConnection connection = connectionResult.result();
        connection.preparedQuery("select * from product where id = ?")
                  .execute(Tuple.of(productId), queryResult -> {
                    if (queryResult.succeeded()) {
                      this.processResponse(queryResult.result(), message);
                    }
                    connection.close();
                  });
      }
    });
  }

  private void processResponse(RowSet<Row> rowSet, Message<Integer> message) {
    RowIterator<Row> iterator = rowSet.iterator();
    message.reply(iterator
      .hasNext() ? productMapper.apply(iterator.next()) : null);
  }

}
