//BO HUANG 1584795

import common.config.Config;
import common.config.ConfigLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerBoostrap {

    public static void main(String[] args) {

        try {
            //load params
            Config config = ConfigLoader.getInstance().load(args);

            //load data
            DynamicContentHolder.start(config.getLoc());

            //update dataset
            new Thread() {
                public void run() {

                    while (true) {
                        try {
                            if(DynamicContentHolder.isChanged())
                                DynamicContentHolder.writeInstancesToDoc();

                            sleep(3000);

                        } catch (InterruptedException e) {
                            log.error("something wrong when updating data doc");
                        }
                    }
                }
            }.start();
            log.info("The dictionary update thread start.");

            //start sever
            SocketServer server = new SocketServer(config.getPort());
            server.listen();

        }catch (Exception e){

            log.error("A failure occurred during the startup process of the server: {}",e.toString());
        }
    }
}
