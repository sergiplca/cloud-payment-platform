#!/bin/sh

BOOTSTRAP=kafka:9092
KAFKA_BIN=/opt/kafka/bin/kafka-topics.sh

echo "Waiting for Kafka to be ready..."

until $KAFKA_BIN --list --bootstrap-server $BOOTSTRAP >/dev/null 2>&1; do
  sleep 2
done

echo "Kafka is ready. Creating topics..."

$KAFKA_BIN --create --if-not-exists \
  --topic orders.order.created \
  --bootstrap-server $BOOTSTRAP \
  --partitions 3

$KAFKA_BIN --create --if-not-exists \
  --topic payments.payment.completed \
  --bootstrap-server $BOOTSTRAP \
  --partitions 3

$KAFKA_BIN --create --if-not-exists \
  --topic notifications.notification.requested \
  --bootstrap-server $BOOTSTRAP \
  --partitions 3

$KAFKA_BIN --create --if-not-exists \
  --topic payments.payment.ledger \
  --bootstrap-server $BOOTSTRAP \
  --partitions 3