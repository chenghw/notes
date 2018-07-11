# Graphql

`yarn add graphql`

`const { graphql, buildSchema } = require('graphql');`

`graphql` - (schema, query, resolvers) => Promise

## Schema

Multiple ways to build a GraphQL scheme.

`buildSchema` - accepts a (template) string with schema type definitions

`GraphQLSchema` - `new GraphQLSchema({ query, mutation, subscription });`

## Resolvers

An object of keys that map to the schema type definitions. Values are functions of which how to resolve the value returned.

## Query

Template string that starts with `query` instead of `type` like in type definitions.

```javascript
const query = `
query myFirstQuery {
  videos {
    id
    title
    duration
    watched
  }
}
`;
```

## Arguements

Arguments can be passed to a query through the `args` key.

Example:

```javascript
const queryType = new GraphQLObjectType({
  name: 'QueryType',
  description: 'The root query type.',
  fields: {
    video: {
      type: videoType,
      args: {
        id: {
          type: GraphQLID,
          description: 'The id of the video.',
        },
      },
      resolve: () => new Promise(resolve => {
        resolve({
          id: 'a',
          title: 'GraphQL',
          duration: 180,
          watched: true,
        });
      }),
    }
  }
});

// Execution of query
{
  video(id: 'a') {
    title
  }
}
```

## Primative Types

- ID - `GraphQLID`
- String - `GraphQLString`
- Int - `GraphQLInt`
- Boolean - `GraphQLBoolean`
- Object - `type {}` - `GraphQLObjectType`
- Array - `[String]` - Array of string types

## GraphQL Non Null type

Helps designate required fields/arguments.

```javascript
const { GraphQLNonNull } = require('graphql');

new GraphQLNonNull(/* Some graphql type */)
```

## GraphQL List type

Define a list/collection.

```javascript
const { GraphQLList } = require('graphql');

new GraphQLList(/* Some graphql type */)
```

## Mutation

Declared similar to a query in that it is a GraphQLObject.

```javascript
const mutationType = new GraphQLObjectType({
  name: 'Mutation',
  description: 'Root mutation type',
  fields: {
    createVideo: {
      type: videoType,
      args: {
        title: {
          type: new GraphQLNonNull(GraphQLString),
          description: 'The title of the video.',
        },
        duration: {
          type: new GraphQLNonNull(GraphQLInt),
          description: 'The duration of the video (in seconds).'
        },
        released: {
          type: new GraphQLNonNull(GraphQLBoolean),
          description: 'Whether or not the video is released.'
        },
      },
      // return can be queried on
      resolve: (_, args) => createVideo(args),
    }
  }
})

const schema = new GraphQLSchema({
  query: queryType,
  mutation: mutationType,
});
```

```graphql
mutation M {
  createVideo(title: "Foo", duration: 300, released: false) {
    // Query on the result
    id,
    title
  }
}
```

## Input Object Type

Used for creating mutations with complex object types that deserve their own object type.

```javascript
const { GraphQLInputObjectType } = require('graphql');

const videoInputType = new GraphQLInputObjectType({
  name: 'VideoInput',
  fields: {
    title: {
      type: new GraphQLNonNull(GraphQLString),
      description: 'The title of the video.',
    },
    duration: {
      type: new GraphQLNonNull(GraphQLInt),
      description: 'The duration of the video (in seconds).'
    },
    released: {
      type: new GraphQLNonNull(GraphQLBoolean),
      description: 'Whether or not the video is released.'
    },
  }
});

const mutationType = new GraphQLObjectType({
  name: 'Mutation',
  description: 'Root mutation type',
  fields: {
    createVideo: {
      type: videoType,
      args: {
        video: {
          type: new GraphQLNonNull(videoInputType),
        },
      },
      resolve: (_, args) => createVideo(args.video),
    }
  }
})
```

```graphql
mutation M {
  createVideo(video: {
    title: "Foo",
    duration: 300,
    released: false
  }) {
    id,
    title
  }
}
```

## Interface Type

When multiple types share the same field you'll want to create a interface type to keep things DRY.

```javascript
const {
  GraphQLInterfaceType,
  GraphQLNonNull,
  GraphQLID
} = require('graphql');
const { videoType } = require('../index');

const nodeInterfaceType = new GraphQLInterfaceType({
  name: 'Node',
  fields: {
    id: {
      type: new GraphQLNonNull(GraphQLID),
    },
  },
  resolveType: (object) => {
    if (object.title) {
      return videoType;
    }

    return null;
  }
});

// Inside other types along side name, description and fields there is an interfaces key

const videoType = new GraphQLObjectType({
  name: ...,
  description: ...,
  fields: ...,
  interfaces: [nodeInterface]
});
```

## Relay

`yarn add -E graphql-relay`

## Relay Connections

Efficiently page through collections of items. These are called connections.

## Relay Mutations



## Express

`yarn add express express-graphql`

```javascript
const express = require('express');
const graphqlHTTP = require('express-graphql');

const server = express();

// graphqlHTTP accepts an options object
server.use('/graphql', graphqlHTTP({
  schema,
  graphiql: true, // Graphql GUI
  rootValue: resolvers, // Necessary if schema is built through buildSchema
}));
```

