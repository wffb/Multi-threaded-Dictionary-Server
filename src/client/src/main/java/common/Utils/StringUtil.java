package common.Utils;

import common.config.Params;

import java.util.ArrayList;
import java.util.Arrays;

public class StringUtil {

    public static ArrayList<String> splitMeaningList(String s){
        return new ArrayList<String>( Arrays.asList(s.split(Params.MEANING_QELIIMTER)) );
    }


    public static String combineMeaningList(ArrayList<String> set){

        if(set.isEmpty())
            return "";

        StringBuilder res = new StringBuilder();

        for(int i=0;i<set.size();i++){
            if(i!=0)
                res.append(Params.MEANING_QELIIMTER);
            res.append(set.get(i));
        }

        return res.toString();
    }


}
