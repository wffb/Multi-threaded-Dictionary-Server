//BO HUANG 1584795

package common;

import com.alibaba.fastjson2.JSONObject;
import common.feedback.ResponseCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

@Slf4j
public class IOHelper {

    /**
     *  output: feedback
     *      status: code [500 , 400]
     *      message: status message
     *      info: result
     */



    static public void successFeedback(SocketChannel socketChannel,String type,String info)  {
        wirte(socketChannel,
                type,
                ResponseCode.SUCCESS.getCode(),
                ResponseCode.SUCCESS.getMessage(),
                info);
    }
    static public void successFeedback(SocketChannel socketChannel,String info)  {
        wirte(socketChannel,
                "",
                ResponseCode.SUCCESS.getCode(),
                ResponseCode.SUCCESS.getMessage(),
                info);
    }

    static public void failureFeedback(SocketChannel socketChannel,String type,String info)  {
        wirte(socketChannel,
                type,
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                info);
    }
    static public void failureFeedback(SocketChannel socketChannel,String info)  {
        wirte(socketChannel,
                "",
                ResponseCode.FAILED.getCode(),
                ResponseCode.FAILED.getMessage(),
                info);
    }

    static public void wirte(SocketChannel socketChannel,String type,Long status, String msg,String info) {

        try {
            //Json Test
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operationType",type);
            jsonObject.put("status",status);
            jsonObject.put("message",msg);
            jsonObject.put("info",info);



            socketChannel.write(ByteBuffer.wrap(jsonObject.toString().getBytes()));

        }catch (Exception e) {
            log.warn("The information transmission failed:{}", e.getMessage());
        }
    }

    /**
     * input:
     *  operationType:
     *  operationInfo...:
     */

    static public JSONObject read(SelectionKey key){

        SocketChannel channel = (SocketChannel) key.channel();

        try{
            //create buffer and read data from a network socket channel into a ByteBuffer object
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //count: The number of bytes read from the buffer。
            int count = channel.read(buffer);
            if (count > 0) {
                String jsonMsg = new String(buffer.array());

                //jsonTest
                return JSONObject.parseObject(jsonMsg);

            }
        }catch (Exception e){

            //return exception
            String failMsg = "failure when receiving message: "+ e.getMessage();
            log.warn(failMsg);
            failureFeedback(channel,failMsg);


            //close connection

            try {
                System.out.println(channel.getRemoteAddress() + "Offline..");
                //Cancel registration
                key.cancel();
                //close channel
                channel.close();

            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        
        return null;
    }


}
