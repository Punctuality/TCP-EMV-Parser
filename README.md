## TCP Server for parsing EMV tags

* This is a simple implementation of a TCP server that parses EMV tags

* The server supports just as much functionality as was required for the task. 
Meaning: only the tags from the task description are supported and tested.

* On the other hand, the server's codec is designed to be easily extensible. And EMV tags format are fully described,
even providing some validation to the tags used.

### Supported features:
* Netty TCP Server
* Validation of incoming messages (length, format, etc.)
* Response to the client with the parsed tags (some sensitive data is masked)
* Extensive logging
* Unit tests (+1 Integration test). High coverage percentage.

### Assumptions:
* I presume, there's no need to implement the whole EMV tags spec. Scope here is limited to the tags from the task description.
* Some examples are not completely clear/valid. For example:
  * usage of 9F03 tag with AMEX kernel (might be my misunderstanding)
  * malformed value encoding of 9F2A tag in AMEX (`0x04` with trailing zeros)
* Nothing of the listed bellow was implemented, though one could find it usefully if not developing a toy project:
  * Monitoring/Metrics
  * DB integration
  * tags Internet lookups
  * Configuration of NettyEventLoop worker counts
  * etc.

### Project code decisions:
* Parsing of tags is separated into two parts. One internal part, which implements
EMV Tags spec and is used for sanity-check validation of received data. Parser part is used to combine/extract values from
retrieved tags into something meaningful, like a transaction (exactly what was implemented).
* The pipeline of message received by the server is divided into multiple handlers, which are capable of error propagation,
packet validation, tags decoding, parsing, response building and logging.
All of these handlers can be reused independent one to another. Some of them are embedded inside `EMVServer` class,
others are passed as a "business logic" and can be combined from _user's_ side.
* Netty TCP server is used as a transport layer. Reasoning behind this is that Netty is a well-known and widely used, 
high-performance framework for building network applications.
* At some places, one may find many classes inheriting from sealed-interface, even though their implementation is either
empty or similar. This is done to provide a clear separation of data types and to make it easier to extend/evolve the project.
* In most cases records are used instead of classes. This is done to produce immutable objects,
which are easier to reason about and have no limitation in the current code structure.

### Examples:

Start the server:
```bash
> mvn exec:java -Dexec.mainClass="site.sergeyfedorov.Main"
```

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