# Phase 1

## Introduction

This document contains the relevant design and implementation aspects of LS project's first phase.

## Modeling the database

### Conceptual model ###

The following diagram holds the Entity-Relationship model for the information managed by the system.
[Entity-Relationship Model](https://drive.google.com/file/d/1GLaFSvp2_tc7XhItJ_ohYZRLyUMypwP3/view?usp=sharing)

We highlight the following aspects:

* (_include a list of relevant design issues_)

The conceptual model has the following restrictions:

* The email of a user is unique. 
* The name of a club is unique.
* The id of a court is unique.
* The id of a rental is unique.

### Physical Model ###

The physical model of the database is available in (_link to the SQL script with the schema definition_).

We highlight the following aspects of this model:

* (_include a list of relevant design issues_)

## Software organization

### Open-API Specification ###

[OpenApi Sepcification](./openapi.yaml)

In our Open-API specification, we highlight the following aspects:

(_include a list of relevant issues or details in your specification_)

### Request Details

(_describe how a request goes through the different elements of your solution_)

(_describe the relevant classes/functions used internally in a request_)

(_describe how and where request parameters are validated_)

### Connection Management

(_describe how connections are created, used and disposed_, namely its relation with transaction scopes).

### Data Access

The data access layer is implemented using the Repository pattern. 
The interfaces below establish the contract for the repositories:
* `UserRepository`
* `ClubRepository`
* `CourtRepository`
* `RentalRepository`

We have the following implementations, for the test of the data access layer:
* `UserRepositoryInMem`
* `ClubRepositoryInMem`
* `CourtRepositoryInMem`
* `RentalRepositoryInMem`

* And the following implementations, for the actual data access layer:
* `UserRepositoryJdbc`
* `ClubRepositoryJdbc`
* `CourtRepositoryJdbc`
* `RentalRepositoryJdbc`

(_identify any non-trivial used SQL statements_).

### Error Handling/Processing

Our solutions deal with the errors in the following way:
When we detect an error made by the client in the request we return an error directly to the user,
without processing the request.
When we detect an error in the processing of the request our service will return an Error, to our api
detailing what happen wrong during the request processing.
If our access to the database fails, or the database returns an error, we will catch the error in our api
with and ExceptionHandler that will detail the error that happened during the request processing.

## Critical Evaluation

(_enumerate the functionality that is not concluded and the identified defects_)

(_identify improvements to be made on the next phase_)