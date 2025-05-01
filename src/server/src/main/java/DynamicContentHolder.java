//BO HUANG 1584795

import common.Utils.StringUtil;
import common.config.Params;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class DynamicContentHolder {



    public static ConcurrentHashMap<String,String> dataSet = new ConcurrentHashMap<>();
    private static AtomicBoolean isChanged =new AtomicBoolean(false);

    private static String loc;


    public static void start (String locS) throws IOException {
        loc = locS;
        getInstancesFormDoc();
    }



    //read data from doc
    private static void getInstancesFormDoc() throws IOException {
        //judge whether file exists
        File f = new File(loc);
        if(!f.exists() || !f.isFile()){
            log.error("there is no file");
        }

            //reader
            FileInputStream fis = new FileInputStream(f);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));

            String line;
            while ((line = br.readLine())  != null) {
                if(line.isEmpty())
                    continue;

                 String[]splits = line.split(Params.WORD_QELIIMTER);

                 //warn if there are duplicate items
                if(dataSet.containsKey(splits[0])){
                    log.warn("there are duplicate items in the doc: {}",splits[0]);
                    continue;
                }
                dataSet.put(splits[0],splits[1]);
            }


    }


    //search
    public static String search(String key){

        if(isExist(key))
            return  dataSet.get(key);

        return null;
    }

    //update
    public static void update(String key,String oV,String nV ){

        //CAS
        while (true){
            // exits or not
            if(!isExist(key))
                return;

            String meaningS = dataSet.get(key);
            ArrayList<String> set = StringUtil.splitMeaningList(meaningS);

            for(int i=0 ;i<set.size();i++){
                if(set.get(i).equals(oV)){
                    set.set(i,nV);
                    break;
                }
            }

            // changed or not
            if(dataSet.get(key).equals(meaningS)){
                dataSet.put(key,StringUtil.combineMeaningList(set));
                break;
            }
        }

        markChange(true);
    }

    //delete
    public static void delete(String key){
        if(isExist(key))
            dataSet.remove(key);

        markChange(true);
    }

    //add
    public static void addWord(String key,String value){

        if(!isExist(key))
            dataSet.put(key,value);

        markChange(true);
    }

    public static void addMeaning(String key,String newM ){
        // CAS
        while (true){
            if(!isExist(key))
                return;

            String s = dataSet.get(key);
            String res = s + Params.MEANING_QELIIMTER +newM;

            if(dataSet.get(key).equals(s)){
                dataSet.put(key,res);
                break;
            }
        }
        markChange(true);
    }

    //isExist
    public static boolean isExist(String key){
        return dataSet.containsKey(key);
    }

    public static boolean isExist(String key,String value){
        if(!isExist(key))
            return false;

        for(String s: StringUtil.splitMeaningList(dataSet.get(key))){
            if(s.equals(value))
                return true;
        }
        return false;
    }
    public static boolean isExist(String key,ArrayList<String> value){
        for(String s : value){
            if(isExist(key,s))
                return true;
        }
        return false;
    }




    //write to doc
    static public void writeInstancesToDoc(){

        markChange(false);

        //构造器第二个参数，默认false - 覆盖写入
        try (FileWriter writer =
                     new FileWriter(loc, false)) {

            for(Map.Entry<String,String> entry : dataSet.entrySet()){

                writer.write(entry.getKey());
                writer.write(Params.WORD_QELIIMTER);
                writer.write(entry.getValue());

                writer.write(System.lineSeparator());
            }
        } catch (Exception e) {
           log.error("A failure occurred during the writing of the storage file: "+e.getMessage());
        }
    }

    //change mark management
    public static boolean isChanged(){
        return isChanged.get();
    }

    private static void markChange(boolean changeTo){

        while (true){
                if(isChanged()==changeTo)
                    break;
                if(isChanged.compareAndSet(!changeTo,changeTo))
                    break;
        }
    }


}
