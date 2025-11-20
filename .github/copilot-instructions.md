# Copilot Instructions for Product Distribution System

## Project Architecture
- The system is a distributed, service-oriented architecture (SOA) with 5 main services:
  - **Warehouse Service**: Manages inventory, stock, and warehouse capacity. Data partitioned by location. Key methods: `reserve_stock(product_id, quantity)`, `confirm_shipment()`.
  - **Order Processing Service**: Coordinates order lifecycle, assigns warehouses, and balances load. Order states: Received → Assigned → Stock Reserved → Scheduled for Delivery → Delivered.
  - **Logistics Service**: Manages vehicles, delivery assignments, and optimizes resource allocation. Tracks vehicle capacity and location.
  - **Location Service**: Maps customer addresses to nearest warehouse using geocoding. Caches frequent lookups for performance.
  - **Faults/Returns Service**: Handles returns, complaints, and post-delivery issues. Updates inventory and manages product flow after delivery.

## Developer Workflows
- **Build**: Use Maven (`mvn clean install`) in each service directory to build Java services.
- **Run**: Start each service independently, typically via `mvn spring-boot:run`.
- **Test**: Run unit/integration tests with `mvn test` in each service directory.
- **Frontend**: Static HTML UIs for each service are in `frontend/` subfolders. No build step required for HTML/CSS.

## Conventions & Patterns
- **Service Boundaries**: Each service is isolated in its own folder with its own `pom.xml` and `src/` structure.
- **Data Partitioning**: Inventory and location data are partitioned by warehouse/location for scalability.
- **State Management**: Order and delivery states are explicit and managed via state transitions.
- **Communication**: Services are designed for message passing (e.g., REST APIs, not direct DB access across services).
- **Fault Tolerance**: Location and Faults/Returns services use caching and retry logic for reliability.
- **Concurrency**: Inventory and logistics updates are concurrent; use atomic operations or distributed locks where needed.

## Integration Points
- **Warehouse ↔ Order Processing**: Order assignment and inventory reservation.
- **Order Processing ↔ Location**: Warehouse selection based on customer address.
- **Order Processing ↔ Logistics**: Delivery scheduling and vehicle assignment.
- **Warehouse ↔ Faults/Returns**: Inventory updates for returns and complaints.

## Key Files & Directories
- `faults-returns-service/`, `location-service/`, `logistics-service/`, `order-processing-service/`, `warehouse-service/`: Main service implementations.
- `frontend/`: Contains static HTML UIs for each service.
- `README.md`: High-level architecture and team responsibilities.
- `src/main/resources/application.properties`: Service configuration files.

## Example Patterns
- **Order State Transition**: Implement order state changes as explicit methods or state machines.
- **Geocoding & Caching**: Use caching for frequent location lookups in Location Service.
- **Inventory Reservation**: Use atomic checks and updates for stock reservation in Warehouse Service.

---

For questions or unclear conventions, review `README.md` or ask for clarification. Please suggest improvements if you find missing or outdated instructions.
