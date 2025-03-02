# MotorIST

**MotorIST** is a secure system for electric car management.
It encrypts and signs vehicle settings (lock/unlock, seat/AC configs, battery level checks, and firmware updates) using a custom **CryptoLib**. The project features a three-machine infrastructure (database, application, client) secured via **TLS**, ensuring **confidentiality, integrity, and authentication**. A special “maintenance mode” lets mechanics perform tests on default settings without revealing real configurations, with all actions verified by **digital signatures**.

## Project Files

This project has a great number of documents so we're going to describe them:

- The [**Report**](/Project/REPORT.md) - explains how the project was designed, implemented, and secured, covering its cryptographic library, infrastructure setup, communication protections, and the “maintenance mode” security challenge.

- The [**Overview**](/Project/README.md) and instalation - Explains how to install the project.

- The [**Guidelines**](/Project/Guidelines/README.md) - It explains what projects where avaiable, the constraints of each one, and the deadlines of each delivery.

## Grade Received

- **Final Grade:** 17/20

## Acknowledgments

Project done in collaboration with the following colleagues:

- [**Mafalda Fernandes**](https://github.com/mafarrica)
- [**Diana Goulão**](https://github.com/Dianix21)
