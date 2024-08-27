#!/bin/bash

# Create the exchange
rabbitmqadmin declare exchange name=notificationsExchange type=direct durable=true

# Create the queue
rabbitmqadmin declare queue name=notificationQueue durable=true

# Create the binding
rabbitmqadmin declare binding source=notificationsExchange destination=notificationQueue routing_key=notificationRoutingKey
