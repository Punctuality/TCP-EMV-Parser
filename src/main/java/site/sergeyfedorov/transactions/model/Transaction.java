package site.sergeyfedorov.transactions.model;

import java.math.BigDecimal;
import java.util.Currency;

public record Transaction(Kernel kernel, String cardNumber, BigDecimal amount, Currency currency) {}
