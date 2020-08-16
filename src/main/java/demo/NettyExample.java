package demo;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Discards any incoming data.
 */
public class NettyExample {

  private int port;

  public NettyExample(int port) {
    this.port = port;
  }

  public static void main(String[] args) throws Exception {
    int port = 5050;
    if (args.length > 0) {
      port = Integer.parseInt(args[0]);
    }

    new NettyExample(port).run();
  }

  public void run() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap(); // (2)
      b.group(bossGroup, workerGroup)
       .channel(NioServerSocketChannel.class) // (3)
       .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
         @Override
         public void initChannel(SocketChannel ch) throws Exception {
           ch.pipeline()
             .addLast(new DiscardServerHandler());
         }
       })
       .option(ChannelOption.SO_BACKLOG, 128)          // (5)
       .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      // Bind and start to accept incoming connections.
      ChannelFuture f = b.bind(port)
                         .sync(); // (7)

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      f.channel()
       .closeFuture()
       .sync();
    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

  public static class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
      // Discard the received data silently.
      ((ByteBuf) msg).release(); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
      cause.printStackTrace();
      ctx.close();
    }
  }
}
