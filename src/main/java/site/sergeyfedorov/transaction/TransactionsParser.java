package site.sergeyfedorov.transaction;

import site.sergeyfedorov.emv.model.EMVMessage;
import site.sergeyfedorov.emv.model.tag.EMVTag;
import site.sergeyfedorov.emv.model.tag.EMVTagId;
import site.sergeyfedorov.emv.model.EMVTransmission;
import site.sergeyfedorov.transaction.model.Kernel;
import site.sergeyfedorov.transaction.model.Transaction;
import site.sergeyfedorov.util.currency.CurrencyFinder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

public class TransactionsParser implements Function<EMVTransmission, Transaction[]> {
    @Override
    public Transaction[] apply(EMVTransmission emvTransmission) {
        return Arrays.stream(emvTransmission.messages())
            .flatMap(msg -> parseTransaction(msg).stream())
            .toArray(Transaction[]::new);
    }

    private Optional<Transaction> parseTransaction(EMVMessage message) {
        Map<EMVTagId, EMVTag> mappedTags = new HashMap<>(message.tags().length);
        for (EMVTag tag : message.tags()) mappedTags.put(tag.tagId(), tag);

        EMVTag kernelTag = mappedTags.get(EMVTagId.KERNEL_IDENTIFIER);
        EMVTag panTag = mappedTags.get(EMVTagId.PAN);
        EMVTag amountTag = mappedTags.get(EMVTagId.AMOUNT_AUTHORISED);
        EMVTag currencyTag = mappedTags.containsKey(EMVTagId.TRANSACTION_CURRENCY_CODE)
            ? mappedTags.get(EMVTagId.TRANSACTION_CURRENCY_CODE)
            : mappedTags.get(EMVTagId.AMOUNT_OTHER);

        if (kernelTag != null && panTag != null && amountTag != null && currencyTag != null) {
            Kernel kernel = Kernel.parseTag(kernelTag);
            BigInteger pan = (BigInteger) panTag.tagId().getFormat().parseTagValue(panTag.tagValue());
            BigInteger amount = (BigInteger) amountTag.tagId().getFormat().parseTagValue(amountTag.tagValue());
            Currency currency = CurrencyFinder.getCurrencyInstance(
                ((BigInteger) currencyTag.tagId().getFormat().parseTagValue(currencyTag.tagValue())).intValue()
            );

            return Optional.of(
                new Transaction(kernel, pan.toString(), new BigDecimal(amount).movePointLeft(2), currency)
            );
        } else {
            return Optional.empty();
        }
    }
}
