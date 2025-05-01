//BO HUANG 1584795

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import common.IOHelper;
import common.Utils.StringUtil;
import common.config.Params;
import common.feedback.RequestCode;
import lombok.extern.slf4j.Slf4j;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SocketServerProcessor {

    //thread pool
     private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Params.CORE_POOL_SIZE,
            Params.MAX_POOL_SIZE,
            Params.KEEP_ALIVE_TIME,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(Params.QUEUE_CAPACITY),
            new ThreadPoolExecutor.CallerRunsPolicy());




    static public void handleData(SocketChannel channel,JSONObject object){
        executor.execute(()->{
            doHandleData(channel,object);
        });
    }

    //read the client message
    private static void doHandleData(SocketChannel channel,JSONObject jsonObject) {

        //logic
        if (jsonObject == null)
                return;

        List<String> orders = JSON.parseArray(jsonObject.get("info").toString()).toJavaList(String.class) ;
        String type = (String) jsonObject.get("operationType");


        if(!Objects.isNull(orders)&&!Objects.isNull(type)&& !orders.isEmpty()){

            //operations
            //search
            if(type.equals(RequestCode.SEARCH.getCode())){

                String key = orders.get(0);

                if(!DynamicContentHolder.isExist(key)){
                    IOHelper.failureFeedback(channel,"the word does not exist");;
                    return;
                }

                IOHelper.successFeedback(
                        channel,
                        type,
                        DynamicContentHolder.search(key));

            //update
            }else if(type.equals(RequestCode.UPDATE.getCode())){

                String k = orders.get(0);
                String oV = orders.get(1);
                String nV = orders.get(2);


                if(!DynamicContentHolder.isExist(k)){
                    IOHelper.failureFeedback(channel,"the word does not exist");
                    return;
                }
                if(!DynamicContentHolder.isExist(k,oV)){
                    IOHelper.failureFeedback(channel,"this meaning does not exist");
                    return;
                }
                if(oV.equals(nV)){
                    IOHelper.failureFeedback(channel,"new meaning should not be equal to the same");
                    return;
                }

                DynamicContentHolder.update(k,oV,nV);
                IOHelper.successFeedback(
                        channel,
                        type,
                        "Dictionary update successful!");

            //delete
            }else if(type.equals(RequestCode.DELETE.getCode())){

                String k = orders.get(0);

                if(!DynamicContentHolder.isExist(k)){
                    IOHelper.failureFeedback(channel,"the word does not exist");
                    return;
                }
                DynamicContentHolder.delete(k);
                IOHelper.successFeedback(
                        channel,
                        type,
                        "the word delete successful!");

            }else if(type.equals(RequestCode.ADD_WORD.getCode())){

                String k = orders.get(0);
                String v = orders.get(1);

                if(DynamicContentHolder.isExist(k)){
                    IOHelper.failureFeedback(channel,"the word already existed");
                    return;
                }
                DynamicContentHolder.addWord(k,v);
                IOHelper.successFeedback(
                        channel,
                        type,
                        "the word add successful!");

            }else if(type.equals(RequestCode.ADD_MEANING.getCode())){

                String k = orders.get(0);
                String v = orders.get(1);

                if(!DynamicContentHolder.isExist(k)){
                    IOHelper.failureFeedback(channel,"the word does not exist");
                    return;
                }
                if(DynamicContentHolder.isExist(k,StringUtil.splitMeaningList(v))){
                    IOHelper.failureFeedback(channel,"some meanings already existed");
                    return;
                }

                DynamicContentHolder.addMeaning(k,v);
                IOHelper.successFeedback(
                        channel,
                        type,
                        "the meaning add successful!");

            }else{
                IOHelper.failureFeedback(channel,"Illegal operation type");
            }

        }else{
            IOHelper.failureFeedback(channel,"The required information for the operation is incomplete.");
        }

    }

}
