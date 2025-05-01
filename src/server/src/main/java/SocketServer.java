//BO HUANG 1584795

import com.alibaba.fastjson2.JSONObject;
import common.IOHelper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Objects;

@Slf4j
public class SocketServer {


    private Selector selector;
    private ServerSocketChannel listenChannel;


    //Constructor - Initialization
    public SocketServer(int port) {
        try {
            //get selector
            selector = Selector.open();
            //ServerSocketChannel
            listenChannel = ServerSocketChannel.open();
            //Port binding
            listenChannel.socket().bind(new InetSocketAddress(port));
            //set nonblocking mode
            listenChannel.configureBlocking(false);
            //Register the channel to the Selector and specify the types of events of interest.
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

            log.info("Listening on port:{}",port);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void listen() {

        log.info("service start success!");

        try {
            //循环处理
            while (true) {

                //A ready event that waits non-blocking for one or more NIO channels, such as SocketChannel, ServerSocketChannel, DatagramChannel, or FileChannel
                int count = selector.select();

                if (count > 0) {
                    // The loop yields a collection of selectionkeys, which represent the ready channels and their events
                    Iterator< SelectionKey > iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {

                        SelectionKey key =null;
                        try{
                            /**
                             * A SelectionKey is a class in the Java NIO library that represents a Key registered to a Channel on the Selector. Each SelectionKey is associated with a specific Selector
                             * a specific channel (such as SocketChannel, ServerSocketChannel, etc.) and contains status information that indicates whether the channel is ready for read, write, or connect operations
                             */
                             key = iterator.next();

                            //if it is acceptance  event
                            if (key.isAcceptable()) {

                                //Creates a channel containing the new connection and registers it with the selector
                                SocketChannel sc = listenChannel.accept();
                                sc.configureBlocking(false);
                                sc.register(selector, SelectionKey.OP_READ);

                                log.info(sc.getRemoteAddress() + " goes live ");
                            }

                            // if it is readable event
                            if (key.isReadable()) {

                                //gain info
                                JSONObject jsonObject = IOHelper.read(key);
                                SocketChannel channel = (SocketChannel) key.channel();
                                //execute operations
                                SocketServerProcessor.handleData(channel,jsonObject);

                            }

                            //delete the current key to prevent duplicate processing.
                            iterator.remove();

                        }catch (Exception e){

                            //channel error
                            log.error("Obtaining and processing information in the Channel resulted in a failure.: "+e.getMessage());
                            if (key != null && !Objects.isNull(key.channel()))
                                IOHelper.failureFeedback((SocketChannel) key.channel(),"There is an internal error on the server" );
                        }
                    }
                } else {
                    log.info("waiting....");
                }
            }
        } catch (Exception e) {

            //selector error
            log.error("There is an internal error on the server: "+e.getMessage());
        } finally {

        }
    }





}
