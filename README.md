# Product-Order-Microservice
Product-Order Microservice Application using SpringBoot

This project deals with creating a microservice based application for order service using REST API's.
#
It contains :

  Discovery Server : which is a Eureka Service Registery.
  
  API Gateway : all the calls to the product and order api's are handled along with authentication using Keycloak.
  
  Product Service : maintains a catalogue of all available products.
  
  Order Service : responsible for placing orders based on the availability by checking the inventory.
  
  Inventory Service: maintains record of the quantity of products available to order.
  
  Notification Service: For now just receives a message when an order is placed. It is implemented using Apache Kafka.

This project also has zipkin configured for distributed tracing.
