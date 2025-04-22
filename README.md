# Inventory Management System (IMS)

## Table of Contents
1. [Project Overview](#project-overview)
2. [Features](#features)
3. [System Requirements](#system-requirements)
4. [Installation Guide](#installation-guide)
5. [User Interface Guide](#user-interface-guide)
6. [API Documentation](#api-documentation)
7. [Troubleshooting](#troubleshooting)
8. [Support](#support)

## Project Overview
The Inventory Management System (IMS) is a comprehensive web-based solution designed to streamline inventory tracking, order management, and stock control. The system consists of a React-based frontend and a Spring Boot backend, providing a robust and scalable platform for businesses to manage their inventory efficiently.

### Architecture
```
┌─────────────────┐     ┌──────────────────┐     ┌─────────────┐
│   React Frontend│ <── │   Spring Boot    │ <── │  Database   │
│   (Vite)       │ ──> │   Backend API    │ ──> │  (MongoDB)  │
└─────────────────┘     └──────────────────┘     └─────────────┘
```

## Features
- User Authentication and Authorization
- Dashboard with Real-time Analytics
- Inventory Management
  - Stock tracking
  - Product categories
  - Stock alerts
- Order Management
- Supplier Management
- Reports Generation
- User Management

## System Requirements
### Frontend Requirements
- Node.js (v14.0 or higher)
- npm (v6.0 or higher)

### Backend Requirements
- Java JDK 17 or higher
- Maven 3.6 or higher
- MongoDB 6.0 or higher

## Installation Guide

### Frontend Setup
1. Clone the repository
   ```bash
   git clone [repository-url]
   cd imsfrontend
   ```

2. Install dependencies
   ```bash
   npm install
   ```

3. Configure environment variables
   Create a `.env` file in the root directory with the following content:
   ```
   VITE_API_URL=http://localhost:8080
   ```

4. Start the development server
   ```bash
   npm run dev
   ```
   The application will be available at `http://localhost:5173`

### Backend Setup
1. Navigate to the backend directory
   ```bash
   cd inventory-management
   ```

2. Configure MongoDB database
   Update `src/main/resources/application.properties` with your MongoDB connection string:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/inventory_management
   ```

3. Build the project
   ```bash
   mvn clean install
   ```

4. Run the application
   ```bash
   mvn spring-boot:run
   ```
   The backend server will start at `http://localhost:8080`

## User Interface Guide

### Login Page
- Enter your credentials to access the system
- Use the "Forgot Password" link if needed

### Dashboard
- Overview of key metrics
- Quick access to main features
- Real-time alerts and notifications

### Inventory Management
- Add/Edit/Delete products
- Manage categories
- Track stock levels
- Set stock alerts

### Order Management
- Create new orders
- Track order status
- Manage order history

### Reports
- Generate various reports
- Export data in different formats
- Customize report parameters

## API Documentation

### Authentication
- POST `/api/auth/login`
- POST `/api/auth/logout`

### Inventory Endpoints
- GET `/api/products`
- POST `/api/products`
- PUT `/api/products/{id}`
- DELETE `/api/products/{id}`

### Order Endpoints
- GET `/api/orders`
- POST `/api/orders`
- PUT `/api/orders/{id}`

For detailed API documentation, please refer to the Swagger UI available at:
`http://localhost:8080/swagger-ui.html`

## Troubleshooting

### Common Issues

1. Frontend Connection Issues
   - Verify API URL in `.env` file
   - Check if backend server is running
   - Confirm CORS settings

2. Backend Issues
   - Verify database connection
   - Check application logs
   - Ensure correct Java version

3. Database Issues
   - Verify MongoDB service is running
   - Check MongoDB connection string
   - Ensure MongoDB is installed and running on port 27017
   - Check if the database and collections exist
   - Verify MongoDB user permissions

## Support
For additional support or to report issues:
- Create an issue in the project repository
- Contact the development team at [ashukirjat@gmail.com]
- Check the documentation in the project wiki

---
© 2025 Inventory Management System. All rights reserved.
