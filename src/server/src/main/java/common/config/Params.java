//BO HUANG 1584795

package common.config;

public class Params {

    public static final int PORT = 6667;

    //location of data doc
    public static String DATA_LOC = "./server/data.txt";
//    public static String DATA_LOC = "./data/data.txt";

    // thread pool config
    public static final int CORE_POOL_SIZE = 5;
    public static final int MAX_POOL_SIZE = 10;
    public static final int QUEUE_CAPACITY = 100;
    public static final Long KEEP_ALIVE_TIME = 1L;

    //data set config
    public static final String WORD_QELIIMTER = " --- ";
    public static final String MEANING_QELIIMTER = " - ";

}
