# ASD User Manager

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Quick Start](#quick-start)
- [Requirements](#requirements)
- [Acknowledgements](#acknowledgements)

---

## Introduction

The "User Manager" is a system that enables the registration, login, logout, administration and deletion of user
accounts.

This is a submission for advanced software development project by students from
the [University of Applied Sciences Campus Vienna](https://www.fh-campuswien.ac.at/en/studies/study-courses/software-design-and-engineering-master.html)
.

---

## Quick Start

### SDK: Java 17

### Installation

```
mvn clean install
```

### Run

`UserManagerApplication.main()`

### Launch GUI

- Launch Browser
- Open http://localhost:8080/swagger.html

---

## Features

TBD

## Requirements

### Functional requirements

**Registration**

- [x] 100 The system must allow the user to create an account with first name, last name, user name and password.
- [x] 101 When a new account is created, the system must check for the existence of the user name.
- [x] 102 If the user name already exists, the system must display the message "The user name is already taken".

**Logon and logoff**

- [x] 200 The system must offer the user the option of logging in with a user name and password.
- [x] 201 If the user name or password does not match the stored data, the system shall output the error message "User
  name or password incorrect".
- [x] 202 The system must offer the user three attempts to log in before the corresponding user name is blocked for 60
  seconds after the fourth, incorrect attempt.
- [x] 203 If the user attempts to log in while the username is locked, the error message 'User locked until DD.MM.YYYY
  at hh:mm' shall be displayed.
- [x] 204 When the user is logged in, the system shall allow the user to log out.

**Password Change**

- [x] 300 If the user is logged in, the system shall allow the user to change his/her password.
- [ ] 301 If the password is to be changed, the user shall re-enter his/her password for confirmation.
- [ ] 302 If the entered password does not match the current password, the system shall display the error message "
  Password is not correct".
- [ ] 303 If the password is to be changed, the system must ask the user twice for the new password.
- [ ] 304 If the new password does not match the second entry, the system shall display the error message "Passwords do
  not match".
- [ ] 305 If the old password matches and the new password is successfully compared with the second entry, the system
  shall replace the old password with the new one.

**Deletion**

- [ ] 400 When the user is logged in, the system shall provide the user with the option to delete the account.
- [ ] 401 If the account is to be deleted, the user must enter his/her current password for confirmation.
- [ ] 402 If the password entered does not match the current password, the system shall display the error message "
  Password is incorrect".
- [ ] 403 If the password entered was correct, the system shall log the user off and delete the account.

### Boundary Conditions & Non-Functional Requirements

- [x] 500 The backend must be developed in Java (version 15 or higher).
- [x] 501 The system must always store passwords in encrypted form.
- [x] 502 Git must be used as the VCS together with GitHub.
- [x] 503 There is a README file in the repository root that describes the operation and interactions with the system.
- [x] 504 Any package managers, libraries and frameworks can be used to develop the system.
- [x] 505 Data must be persisted until the system is restarted.
- [x] 506 The persistence of data can be done in any way.

### Extensions (optional)

- [x] 600 The system can be interacted with using a GUI.
- [ ] 601 The system must automatically log off the user after 120 seconds of inactivity.

---

## Acknowledgements

- [Eren Dogan](https://github.com/erendogan51)
- [Alexander Pecl](https://github.com/peclboi)
- [Leo Bowen Wang](https://github.com/leobowenwang)

