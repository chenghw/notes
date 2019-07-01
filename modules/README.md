# JavaScript Modules

## Resources

http://exploringjs.com/es6/ch_modules.html

https://addyosmani.com/writing-modular-js/

## Questions I want answered

- What do these techniques solve?
- CommonJS, AMD, RequireJS, ES6 Modules? Future dynamic imports

## Notes

### Modules in JavaScript

- Each module is a piece of code that is executed one it is loaded.
- Declaration inside a module stay local, however can be marked as exports for other imports to use them.
- A module can import things from other modules. It refers to those modules via module specifiers, strings that are either:
  - Relative paths: these paths are interpreted relatively to the location of the importing module
  - Absolute paths: point directly to the file of the module to be imported
  - Named: what modules refer to has to be configured
- Modules are singletons. Even if a module is imported multiple times, only a single "instance" of it exists.
- The only thing that are globals are module specifiers.

### ECMAScript 5 module systems

ECMAScript 5 was released in 2009, which was the last major release before ES6 (ES2015) which was released in 2015.

Two implementations:

#### CommonJS Modules

A compact syntax designs for synchronous loading and servers.

#### AMD - Asynchronous Module Definition

RequireJS is a popular implementation of this standard. Slightly more complicated syntax designed for asynchronous loading and browsers.

### ECMAScript 6 modules

- Similarly to CommonJS, they have a compact syntax, a preference for single exports and support for cyclic dependencies.
- Similarly to AMD, they have direct support for asynchronous loading and configurable module loading.
- Their syntax is even more compact than CommonJS's
- Their structure can be statically analyzed for static checking, optimization, etc.
- Their support for cyclic dependencies is better than CommonJS's

ES6 module standard has two parts:

- Declarative syntax for importing and exporting
- Programmatic loader API: to configure how modules are loaded and to conditionally load modules

### The basics of ES6 modules

There are two kinds of exports: named exports and default exports.

#### Named exports

A module can export multiple things by prefixing its declarations with the keyword export. These exports are distinguished by their names and are called named exports.

```javascript
// es6-named-exports-example.js
// ES6 named exports
export const sqrt = Math.sqrt;

// commonjs-named-exports-example.js
// CommonJS named exports
var sqrt = Math.sqrt;

module.exports = {
  sqrt: sqrt
};
```

#### Default exports

A module can only have one default export.

```javascript
// es6-default-exports-example.js
// ES6 default exports
const sqrt = Math.sqrt;

export default sqrt;

// commonjs-default-exports-example.js
// CommonJS default exports
var sqrt = Math.sqrt;

module.exports = {
  sqrt: sqrt
};
```

## Question

- If modules are singletons, how does reselect functions work? Does the library create singletons of each of the functions created so across multiple imports it still refers to the same function and the same cache?
- What is a module specifier?
- What does compact syntax mean in reference to modules?
