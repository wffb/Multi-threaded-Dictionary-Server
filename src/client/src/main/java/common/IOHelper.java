//BO HUANG 1584795

package common;

import com.alibaba.fastjson2.JSONObject;
import common.feedback.RequestCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@Slf4j
public class IOHelper {

    /**
     * input:
     *  operationType:
     *  operationInfo...:
     */

    static public void wirte(SocketChannel socketChannel,String type,String... info) {

        try {
            //Json Test
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("operationType",type);
            jsonObject.put("info",info);

            socketChannel.write(ByteBuffer.wrap(jsonObject.toString().getBytes()));

        }catch (Exception e) {
            log.error("The information transmission failed:{}", e.getMessage());
        }
    }

    static public void search(SocketChannel channel,String keyword){
        wirte(channel, RequestCode.SEARCH.getCode(),keyword);
    }
    static public void delete(SocketChannel channel,String keyword){
        wirte(channel,RequestCode.DELETE.getCode(),keyword);
    }
    static public void update(SocketChannel channel,String keyword,String value){
        wirte(channel,"update",RequestCode.UPDATE.getCode(),value);
    }
    static public void add(SocketChannel channel,String keyword,String value){
        wirte(channel,"add",RequestCode.ADD_WORD.getCode(),value);
    }



    /**
     *  output: feedback
     *      status: code [500 , 400]
     *      message: status message
     *      info: result
     */
    static public JSONObject read(SelectionKey key){

        SocketChannel channel = (SocketChannel) key.channel();

        try{
            //create buffer and read data from a network socket channel into a ByteBuffer object
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            //count: The number of bytes read from the buffer
            int count = channel.read(buffer);
            if (count > 0) {
                String jsonMsg = new String(buffer.array());
                return JSONObject.parseObject(jsonMsg);

            }
        }catch (Exception e){

            //return exception
            String failMsg = "failure when receiving message: "+ e.getMessage();
            log.error(failMsg);
        }
        return null;
    }


}
