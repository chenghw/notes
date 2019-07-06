# Spring Boot - (JavaBrains.io)[https://www.youtube.com/watch?v=msXL2oDexqw&list=PLmbC-xnvykcghOSOJ1ZF6ja3aOgZAgaMO]

## What is Spring Boot?

Spring Boot makes it easy to create stand-alone, production-grade Spring based Applictions that you can "just run".

- Spring is a framework for creating Java Applications
- Boot - Bootstrap

Spring Bootstrap - Create Spring based Applications

## What is Spring?

More than just dependency injection.

- Application framework
- Programming and configuration model

## Problems with Spring

- Huge framework
- Multiple setup steps
- Multiple configuration steps
- Multiple build and deploy steps

# Enter Spring Boot

- Opinionated
- Convention over configuration
- Stand alone
- Production ready

## Setup

- Java SDK
- Maven

## Maven

Lets you declare all the dependencies all in a single file. pom.xml kinda like package.json. `pom` stands for Project Object Model

Maven is your build and dependency management tool. Kinda like npm reading package.json(pom.xml).

Parent/Child relationship. Declaring a parent in the pom.xml will have your project as a child of that parent. As a child, your project inherits the configuration from the parent project.

## Creating a Maven Java Project

`$ mvn archetype:generate`

or in vscode using the `Spring Initializr Java Support`

`> Spring Initializr: Generate a Maven Project`

Project requirements
- groupId
- artifactId
- version
- package

## Spring Boot initial setup in a Maven Project

Project will be using the Spring Boot framework so having spring-boot-starter-parent is required in the pom.xml. Parent will determine which versions of the dependencies it'll download based on its own version. Bill of Materials

```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.6.RELEASE</version>
</parent>
```

Set the java version if necessary in properties.

```
<properties>
  ...
  <java.version>1.8</java.version>
  ...
</properties>
```

Add `spring-boot-starter-web` to the dependencies.

```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Create an class which will act as your applications starting point.

- `@SpringBootApplication` decorator to inherit the properties of a Spring Boot Application
- `SpringApplication.run` passing in the class that has `@SpringBootApplication` attached to it and any pass through arguments

```java
package io.javabrains.springbootquickstart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CourseApiApp
 */
@SpringBootApplication
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
```

- Sets up default configuration
- Starts Spring application context
- Performs class path scan - Create classes and annotate through decorators
- Starts Tomcat server

## Customizing Spring Boot

`application.properties` file is where you can customize some properties.

Example:
```
server.port=3000
```

[Application Properties Docs](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)

## REST Controllers

What is a controller? These controllers allow us to map requests to responses through a instance method.

- A Java class
- Marked with annotations
- Has info about
  - What URL access triggers it
  - What method to run when accessed

`@RestController` - makes the class a RestController
`@RequestMapping` - url and method for a public instance method
`@ResponseBody`

## Tomcat Server

Spring Boot has an embedded Tomcat Server.

- Servlet container config is now application config
- Standalone application
- Useful for microservice architecture

## Annotations/Decorators for MVC REST

`@Autowired` - Ensure singleton has been created and there upon intialization
`@Service` - Singleton
`@PathVariable` - Used in parameters to tell that the variable is coming from `@RequestMapping`, variables are passed through the url by `{}`
`@RequestBody` - Request payload is going to contain a request payload of the specified shape

## JPA - Java Persistence API

## Spring Data JPA

Map model topic class to JPA using `@Entity` from persistence.

Using an interface allows for JPA to implement functions for you.

## Adding in relationships

`@ManyToOne`
...

## Interface Auto Generation

Using a combination of return type, method name, and arguments, persistence can implement your methods for you.

## Monitoring

`Actuator` - Production ready features to help you monitor and manage your application

`spring-boot-starter-actuator`

Adds the endpoint `/health` which has some stats/info about your server.

