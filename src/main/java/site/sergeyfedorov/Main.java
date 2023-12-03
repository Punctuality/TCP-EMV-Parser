package site.sergeyfedorov;

import org.slf4j.LoggerFactory;
import site.sergeyfedorov.server.EMVServer;

public class Main {
    public static void main(String[] args) {
        EMVServer server = new EMVServer(8123);

        try {
            server.run();
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(Main.class).error("Server interrupted", e);
        }
    }
}