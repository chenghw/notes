# GraphQL

## What is GraphQL?

GraphQL is a query language for your API. Currently most applications are hitting either REST (Representational State Transfer) or SOAP (Simple Object Access Protocol) APIs

SOAP is an XML based protocol that lets you exchange information over a particular protocol such as HTTP (Hypertext Transfer Protocol) or SMTP (Simple Mail Transfer Protocol). SOAP uses XML for its messaging format to relay information.

REST is not a protocol but an architectural style for APIs.

GraphQL is a specification, not an implementation. The spec therefore needs to be implemented on the client and server side. GraphQL just being a spec leads to multiple implementations and choices around what to implement on the client and server sides.

## Server

The GraphQL specification has been implemented in multiple [languages](http://graphql.org/code/). For JavaScript there are two main libraries that implement the spec.

### Facebook

- [graphql-js](https://github.com/graphql/graphql-js) - Facebook's JavaScript implementation of the GraphQL spec
- [express-graphql](https://github.com/graphql/express-graphql) - GraphQL HTTP server middleware for expressjs

### Apollo

- [apollo-server](https://github.com/apollographql/apollo-server) - GraphQL HTTP server complatible with mutliple frameworks (Express, Hapi, Koa, etc)
- [graphql-tools](https://github.com/apollographql/graphql-tools) - Library with useful tooks such as creating GraphQL schemas (an alternative to using `graphql-js`)

## Client

Like the server there are multiple GraphQL implementations for the client depending on what language you are dealing with. Whether its Java/Android, Swift/Objective-C or good old JavaScript.

### Facebook

- [Relay](https://github.com/facebook/relay) - JavaScript framework for creating GraphQL client applications

### Apollo

- [apollo-client](https://github.com/apollographql/apollo-client) - Apollo's client library


