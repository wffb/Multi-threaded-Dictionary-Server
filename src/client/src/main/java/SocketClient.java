//BO HUANG 1584795
import com.alibaba.fastjson2.JSONObject;
import common.IOHelper;
import common.config.Params;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;

@Slf4j
public class SocketClient {

    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    private String action = "0";

    public static SocketClient INSTANCE;

    //Constructor, completing the initialization work
    public SocketClient(String host, Integer port ) throws IOException {
            selector = Selector.open();
            //Connect to the server
            socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
            //Set non-blocking mode
            socketChannel.configureBlocking(false);
            //Register the channel to the selector
            socketChannel.register(selector, SelectionKey.OP_READ);
            //get local address
            username = socketChannel.getLocalAddress().toString().substring(1);
            System.out.println(username + " is ok...");
    }

    // handle response from serve
    public void readInfo(){

        try {
            int readChannels = selector.select();
            if (readChannels > 0) { //channel available
                Iterator< SelectionKey > iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {

                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        //read
                        JSONObject jsonObject = IOHelper.read(key);

                        //logic
//                        if (jsonObject != null) {
//                            System.out.println("get reply: "+ jsonObject.toString());
//                        }

                        Gui.setFeedback(jsonObject);

                    }
                }
                iterator.remove(); //Delete the current selectionKey to prevent duplicate operations.
            } else {
                log.info("no available channel now");
            }
        } catch (Exception e) {
            log.error("There is an internal error in the client:{}",e.getMessage());
            Gui.setError("There is an internal error in the client");
        }

    }

    public static void start (String host, Integer port)  {

        //start the client

        try {

            INSTANCE = new SocketClient(host,port);

        }catch (Exception e){
            log.error("The client fails to be started due to an exception:{}",e.getMessage());
            Gui.setError("The client fails to be started");
            return;
        }

        //Start a thread to read data sent from the server every 3 seconds.
        SocketClient finalChatClient = INSTANCE;
        new Thread() {
            public void run() {
                while (true) {
                    finalChatClient.readInfo();
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        log.error("something wrong  when receiver sleeping");
                        Gui.setError("something wrong when receiver sleeping");
                    }
                }
            }
        }.start();

    }


    public static void wirte (String type,String... order){
        if(!Objects.isNull(INSTANCE)){
            IOHelper.wirte(INSTANCE.socketChannel,type,order);
        }
    }

}
