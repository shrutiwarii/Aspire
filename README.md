# Aspire

### Mini loan providing service

## Table of Contents

- [Getting Started](#getting-started)
    - [Prerequisites](#prerequisites)
    - [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Database Configuration](#database-configuration)
- [Testing](#testing)

## Getting Started

Welcome to the Aspire Project! This project allows users to apply for loans and manage loan repayments. It also helps the ADMINS to approve the loans. Below are the instructions to get started with the project.

### Prerequisites

Before you begin, ensure you have met the following requirements:

- Java Development Kit (JDK)
- Maven
- MongoDB

### Installation

To install and run the project, follow these steps:

1. **Clone the repository:**
```bash
   git clone https://github.com/shrutiwarii/Aspire.git
````
2. **Change to the project directory:**
```bash
    cd AspireProject
````

3. **BUild the project:**
```bash
    ./mvnw clean install
````

### Usage
To use the Aspire Project, follow these steps:
1. **Run the project:**
```bash 
    ./mvnw spring-boot:run
````
2. Access the application at http://localhost:8080.

3. Sign up for an account and apply for a loan.

4. Manage your loans and make repayments.


### API Documentation

The Aspire Project provides RESTful APIs for loan application and repayment. You can find detailed API documentation in the [API Documentation file](https://docs.google.com/document/d/1TowCKT2EFHnVR2Oa5qJBI3AhB2c1uqml_f4qE_n3NQA/edit).

### Database Configuration
```bash
    spring.data.mongodb.uri=mongodb://localhost:27017/aspiredata
````
### Testing
To run tests, execute the following command:
```bash
    ./mvnw test
````

    
