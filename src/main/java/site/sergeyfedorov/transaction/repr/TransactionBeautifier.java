package site.sergeyfedorov.transaction.repr;

import site.sergeyfedorov.transaction.model.Transaction;

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
            transaction.cardNumber().substring(0, 6),
            discreetMode ?
                "*".repeat(transaction.cardNumber().length() - 10) :
                transaction.cardNumber().substring(6, transaction.cardNumber().length() - 4),
            transaction.cardNumber().substring(transaction.cardNumber().length() - 4),
            transaction.cardNumber().length(),
            transaction.amount().toPlainString(),
            transaction.currency().getDisplayName(),
            transaction.currency().getCurrencyCode()
        );
    }
}
