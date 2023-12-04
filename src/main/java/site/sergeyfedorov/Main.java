package site.sergeyfedorov;

import org.slf4j.LoggerFactory;
import site.sergeyfedorov.emv.model.EMVTransmission;
import site.sergeyfedorov.emv.server.EMVServer;
import site.sergeyfedorov.emv.server.handler.FunctionApplyingHandler;
import site.sergeyfedorov.transaction.TransactionsParser;
import site.sergeyfedorov.transaction.model.Transaction;
import site.sergeyfedorov.transaction.repr.TransactionBeautifier;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        EMVServer server = new EMVServer(
            8123,
            new FunctionApplyingHandler<>(new TransactionsParser(), EMVTransmission.class),
            new FunctionApplyingHandler<>(
                transactions -> Arrays
                    .stream(transactions)
                    .map(new TransactionBeautifier(true))
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse(""),
                Transaction[].class
            ));

        try {
            server.run().sync();
        } catch (InterruptedException e) {
            LoggerFactory.getLogger(Main.class).error("Server interrupted", e);
        }
    }
}