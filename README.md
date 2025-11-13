SOFE 4790U
Distributed Systems

Product Distribution System: Proposal

Group 4

Bryan Parmar, 100872527
Emilio Giron, 100783207
Riyan Faroqui, 100869339
Shane Currie, 100860282
Sanzir Anarbaev, 100704172


Oct 20, 2025

Introduction
This project proposes a Product Distribution System (PDS) that applies distributed computing principles to optimize warehouse capacity, delivery vehicle capacity, and warehouse coverage areas for multiple warehouse locations. By applying core distributed system design concepts - such as transparency, fault tolerance, concurrency, and load balancing - the system will improve delivery performance and overall logistics efficiency. The project will demonstrate how distributed architecture can provide scalability, reliability, and real-time synchronization across multiple nodes.

The Problem
The project aims to tackle the main problem of having multiple different systems communicating together while maintaining best practices for distributed systems. The entire system is split into 5 independent systems that have to work together efficiently to promote optimal routing for the package delivery. The challenge is to overcome scaling logistics, maintain data consistency across geographically dispersed inventory and provide optimal routing.

Solution
Implementing a service-oriented architecture (SOA) with distinct services (Warehouse, Order, Logistics), utilizing principles like data partitioning and load balancing. The system can be broken down into four main services, communicating via message passing.

1. The Warehouse Service (Inventory Management) is the core storage and control service for each location.
Data Model: Stores products, available stock, and the warehouse capacity (volume).
Key Concept: 
Data Partitioning: Inventory data is naturally partitioned by warehouse location. Each warehouse's inventory is managed locally.
Consistency: When an order is processed, inventory needs to be updated. The updates are atomic and consistent  (e.g., using a distributed lock or transactional mechanism) if an item is available in multiple warehouses and an order must choose one.
Implementation Focus: Implement concurrent methods for reserve_stock(product_id, quantity) and confirm_shipment().

2. The Order Processing Service (The Coordinator) handles incoming customer orders and assigns them to the appropriate warehouse.
Coordination & State Management:
Warehouse Assignment: Uses the warehouse ID provided by the Location Service to assign each order to the correct warehouse.
Load Balancing: If multiple warehouses can fulfill an order, this service should assign the order based on the current load or remaining capacity of the warehouses.
Workflow: An order moves through states: Received → Assigned → Stock Reserved → Scheduled for Delivery → Delivered.

3. The Logistics/Vehicle Scheduling Service manages the fleet and delivery assignments.
Data Model: Stores vehicle ID, current location, delivery vehicle capacity (volume), and current assigned deliveries.
Resource Allocation & Optimization:
Vehicle Selection: When an order is ready for delivery, this service finds the best available vehicle based on its remaining capacity and current route/location. It may use data provided by the Location Service to determine warehouse and delivery coordinates.
Concurrency: Drivers constantly update their location and status, leading to concurrent writes. A consistent method for tracking available capacity is essential.

4. The Location Service (Geo-Distribution) provides geographic lookups and warehouse assignment.
Functionality: Translates a customer's address/coordinates into geographic data used to determine the nearest serving warehouse. It provides this warehouse ID to the Order Processing Service to support efficient order assignment.
Implementation Focus: The service uses a geocoding module that maps postal codes or coordinates to the nearest warehouse boundary. To enhance performance, caching can be used to store frequently accessed locations and reduce repeated queries, resulting in faster response times and improved scalability.
Key Concept: Performs distance and mapping computation to maintain consistent geographic data used by other services for warehouse and routing decisions
Scalability & Fault Tolerance: The service will be designed to handle multiple simultaneous lookup requests efficiently and recover gracefully from API or network failures by using retry mechanisms and cached data.

5. Faults, Returns, and Customer Complaints ensure that the product delivered to the customer is what they were looking for and in good condition. Additionally, if there are problems during delivery, this service ensures that the products are marked as unsuccessful deliveries and either scheduled for next delivery or disposed of appropriately.
Data Model: Restocks items that have been returned, discards items that are damaged beyond repair, and updates warehouse inventory.
Reliability, Stability, and Accuracy: 
Reliability: The main purpose of this system is to provide an accurate representation of inventory flow and management. It implements a system to monitor products once they have left the warehouse
Accuracy: Identifies the issue with the product and whether it is to be shipped back after fix, sent to another warehouse, or discarded as non-operational 
Stability: Provides a way for the system to recover if faults occur during the final stages and post-delivery stages of the product 

Timeline 
Week
Task
Description

Week 1
(Oct 14-20)
Project Proposal
Prepare and submit the project proposal outlining the problem, proposed solution, realistic timeline, and team responsibilities.

Week 2
(Oct 21-27)
System Design
Finalize system architecture, define data models, and establish communication methods between the four distributed systems.

Week 3 - 5
(Oct 28-Nov 23)
System Development
Development and implementation of core services. Ensure each service can operate independently while supporting message-based coordination.

Week 6
(Nov 18-23)
System Testing
Conduct integration testing, verify communication between services, and resolve bugs. Finalize the final report and project code.

Week 7
(Nov 24-Dec 1)
Final Demo & Report
Present and demonstrate the Product Distribution System, and submit the final report and code.

Team Responsibilities
Member
Role
Responsibilities

Bryan Parmar
Order processing 
Implement the order management logic to handle new orders, validate data integrity, and communicate with the warehouse and logistics services for fulfillment.

Emilio Giron
Logistics/Vehicle Scheduling Service
Manages vehicle location and volume, determining the best available vehicle to execute delivery on an order. 

Riyan Faroqui
Warehouse Service
Manage warehouse data and ensure concurrency control for multi-warehouse operations. Implement failure recovery to maintain system availability.

Shane Currie
Location Service
Implement the geocoding and warehouse-selection logic using distance calculations and caching for efficient lookup performance.

Sanzir Anarbaev
Faults, Returns, and Complaints
Maintenance of the system during and post delivery, ensuring accurate representation and accuracy.


