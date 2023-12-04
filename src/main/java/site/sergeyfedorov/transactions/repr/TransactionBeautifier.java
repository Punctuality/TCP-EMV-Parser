package site.sergeyfedorov.transactions.repr;

import site.sergeyfedorov.transactions.model.Transaction;

import java.util.function.Function;

public class TransactionBeautifier implements Function<Transaction, String> {
    private final boolean discreetMode;

    public TransactionBeautifier(boolean discreetMode) {
        this.discreetMode = discreetMode;
    }

    public String apply(Transaction transaction) {
        return """
            Transaction {
                kernel: %s
                cardNumber: %s%s%s (%d digits)
                amount: %s
                currency: %s (%s)
            }
            """.formatted(
            transaction.kernel().getDisplayName(),
            transaction.cardNumber().substring(0, 4),
            discreetMode ?
                "*".repeat(transaction.cardNumber().length() - 8) :
                transaction.cardNumber().substring(4, transaction.cardNumber().length() - 4),
            transaction.cardNumber().substring(transaction.cardNumber().length() - 4),
            transaction.cardNumber().length(),
            transaction.amount().toPlainString(),
            transaction.currency().getDisplayName(),
            transaction.currency().getCurrencyCode()
        );
    }
}
