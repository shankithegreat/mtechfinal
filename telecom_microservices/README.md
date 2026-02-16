# Telecom Microservices Solution (Generated)
This workspace contains 10 Spring Boot microservices for a telecom online ordering system. Each service is a Maven project and listens on a distinct port starting from 8081.

Services:
1. product-catalog -> 8081
2. customer-management -> 8082
3. shopping-cart -> 8083
4. order-management -> 8084
5. inventory-management -> 8085
6. payment-processing -> 8086
7. billing-invoicing -> 8087
8. auth-service -> 8088
9. service-provisioning -> 8089
10. notification-service -> 8090

Each service uses in-memory maps for storage and exposes REST endpoints described in each service's README.

To build & run a service:
- cd <service-dir>
- mvn spring-boot:run

Notes:
- This generated code is intentionally implemented for local/demo usage.
- Replace in-memory repositories with databases and add messaging (Kafka/RabbitMQ) for production.
