//BO HUANG 1584795

package common.config;

public class ConfigLoader {

    private static ConfigLoader INSTANCE = new ConfigLoader();

    private Config config;

    public static ConfigLoader getInstance(){
       return INSTANCE;
    }

    public Config load(String[] args){
        config = new Config();

        loadFromDefaultParams();
        loadFromArgs(args);

        return config;
    }

    private void loadFromDefaultParams(){
        config.setPort(Params.PORT);
        config.setLoc(Params.DATA_LOC);
    }

    private void loadFromArgs(String[] args){

        if(args.length>=1 && !args[0].isEmpty())
            config.setPort(Integer.parseInt(args[0]));

        if(args.length>=2 && !args[1].isEmpty())
            config.setLoc(args[1]);
    }

}
