## TCP Server for parsing EMV tags

* This is a simple implementation of a TCP server that parses EMV tags

* The server supports just as much functionality as was required for the task. 
Meaning: only the tags from the task description are supported and tested.

* On the other hand, the server's codec is designed to be easily extensible. And EMV tags format are fully described,
even providing some validation to the tags used.

### Supported features:
* Sharding handling of TCP packets
* Validation of incoming messages (length, format, etc.)
* Response to the client with the parsed tags (some sensitive data is masked)
* Extensive logging
* Unit tests (+1 Integration test). High coverage percentage.


### Examples:

Request:
```bash
> echo 'AF0CGJ8qAQKfAgIBAFoIQRERERERERFfKgIJeAMCJJ8qCAQAAAAAAAAAXyoCCCafAgMSNFZaCDeCgiRjEABfnwMBAAMCGJ8qAQKfAgMAUABaBzRWeJASNFZfKgIIQAM=' | base64 -d | nc localhost 8123
```

Response:
```
Transaction {
    kernel: MasterCard
    cardNumber: 411111******1111 (16 digits)
    amount: 1.00
    currency: Euro (EUR)
}

Transaction {
    kernel: American Express
    cardNumber: 378282*****0005 (15 digits)
    amount: 1234.56
    currency: British Pound (GBP)
}

Transaction {
    kernel: MasterCard
    cardNumber: 345678****3456 (14 digits)
    amount: 50.00
    currency: US Dollar (USD)
}
```

Readme TBC