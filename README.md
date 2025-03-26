# Messaging System Implementation
## Producer
  - Developed a Producer API that enables applications on any host to publish messages to a Broker. The Producer allows applications to:
  - Establish a connection with a Broker.
  - Send data by providing a byte array (message content) and a String (topic).

## Consumer
  - Designed a Consumer API for applications on any host to consume messages from a Broker. The Consumer enables applications to:
  - Connect to a Broker.
  - Retrieve messages using a pull-based approach by specifying a topic of interest and a starting position in the message stream.

## Broker
  - The Broker handles an unlimited number of connections from Producers and Consumers. The core implementation:

Maintains a thread-safe, in-memory data structure to store all messages.
