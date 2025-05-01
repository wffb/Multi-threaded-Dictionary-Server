//BO HUANG 1584795

import common.config.Config;
import common.config.ConfigLoader;

public class ClientBoostrap {

    public static void main(String[] args) {
        
        Gui.start();

        //load start params
        Config config = ConfigLoader.getInstance().load(args);
        SocketClient.start(config.getHost(),config.getPort());
    }
}
