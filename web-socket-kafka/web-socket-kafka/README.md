How to run?

1. Run `docker compose up -d` to have Kafka and Confluent UI
2. Run `SessionRegistry` which will accept traffic on `localhost:5050`
3. Run `MessageDispatcher` which will consume message from topic `user_message_received`.
4. Run 1 to multiple `ChatServer`s,  note `port` is an argument passed in.


