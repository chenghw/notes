# Cypress

## Getting Started

https://docs.cypress.io/guides/getting-started/installing-cypress.html

## Core Concepts

### Introduction to Cypress

- cy does not return synchronously
  - for synchronous work, use `Cypress.$`
- `cy.get` will wait until element appears, if the element never appears, then the test will fail
- Cypress wraps all DOM queries with retry-and-timeout logic
- cy default timeout is set to 4000ms
  - `cy.get('.my-slow-selector', { timeout: 10000 })`
- Test will run as fast as possible, however a longer timeout will lead to longer failure cases
- Cypress manages its own promise chain. There should be no need for the developer to write their own promises, but understand how they work
- cy provides interaction methods
  - `.click()` - Click on a DOM element
    - Built in assertions, element must not be hidden, covered, disabled, animating
  - `.blur()` - Make a focused DOM element blur
  - `.focus()` - Focus on a DOM element
  - `.clear()` - Clear the value of an input or textarea
  - `.check()` - Check checkbox(es) or radio(s)
  - `.uncheck()` - Uncheck checkbox(es)
  - `.select()` - Select an `<option>` within a `<select>`
  - `.dblclick()` - Double-click a DOM element
  - `.rightclick()` - Right-click a DOM element
- Assertions describe the desired state of your elements, your objects, and your application.
  - Unlike other tests, there may not need to be any assertions in your tests. With the default assertions and queueing of commands, these default behaviors can be used as tests
- Assertions on elements will wait until the assertion passes
  - `cy.get('.some-element').should('be.disabled)` - will wait until the element is disabled else timeout and fail
- Subject management with `cy.[command]`
  - return of `then` will be the next subject (just like promises)
  - each of these commands have their own built in assertions
- Alias of previous subjects
  - `cy.get('.some-el').as('myElement').click()` ... sometime later ... `cy.get('@myElement').click()`
  - This lets us reuse our DOM queries for faster tests when the element is still in the DOM, and it automatically handles re-querying the DOM for us when it is not immediately found in the DOM. This is particularly helpful when dealing with front end frameworks that do a lot of re-rendering
- Cypress runs asynchronously, each Cypress command returns immediately and are appended to a queue of commands to be executed at a later time. The management of the queue is done by Cypress.
- Cypress does not support async await since their API is promise like but not exactly promises and purpose built for Cypress
- How Cypress commands are different from Promises
  - You cannot race or run multiple commands at the same time in parallel
  - You cannot 'accidentally' forget to return or chain a command
  - You cannot add a `.catch` error handler to a failed command
- Negative DOM assertions (`.should('not.exist')`)
  - default assertion is `.should('exist')`
  - any `.should()` command will remove the should exist default
    - positive assertions such as `.should('have.class')` aren't really affected because those imply existence in the first place
  - negative assertions such as `.should('not.have.class')` will pass even if the DOM element does not exist
- Writing Assertions
  - Implicit Subjects
    - `.should()`
    - `.and()`
  Explicit Subjects
    - `expect`
- Unit Tests in Cypress? Yes you can do it. Would you want to?
  - [Unit tests](https://docs.cypress.io/examples/examples/recipes.html#Fundamentals)
  - [Unit tests with React components](https://docs.cypress.io/examples/examples/recipes.html#Unit-Testing)
- `.should()` has built in retry logic, so when using the callback API, ensure that the callback function can be executed multiple times without side effects (retry-safe). The technical term for this means your code must be idempotent.
- Most default timeouts are 4000ms
  - Some exceptions:
    - `cy.visit()` defaults to 60000ms
    - `cy.exec()` defaults to 60000ms
    - `cy.wait()` defaults to 5000ms for the request and 30000ms for the response
  - Updating the timeout goes on the subject
  - Subject's timeout applies to all chained assertions

### Writing and Organizing Tests

- default folder structure
  - /cypress
    - /fixtures
      - example.json
    - /integrations
      - /examples
        - actions.spec.js
    - /plugins
      - index.js
    - /support
      - commands.js
      - index.js
- typical files/folders to be added to .gitignore
  - screenshotsFolder
  - videosFolder
- Fixture Files are used as external pieces of static data that can be used by your tests.
  - typically used by `cy.fixture()` command
  - and stubbing Network Requests
- Test files are located in /cypress/integrations
  - Test files can be written in `.js`, `.jsx`, `.coffee`, and `cjsx` by default. TypeScript support recently added and can be configured
- Plugin files are included before every single spec file runs
  - a convenience mechanism so you don't have to import this file every single one of your spec files
- Support files run before every single spec file
  - a convenience mechanism so you don't have to import this file in every single one of your spec files
  - The support file is a great place to put reusable behavior such as Custom Commands or global overrides that you want applied and available to all of your spec files
- Cypress is built on top of Mocha and Chai
  - test interface is borrowed from Mocha
    - `describe()`, `context()`, `it()`, and `specify()`
  - hooks also borrowed from Mocha
    - `before()`, `beforeEach()`, `after()`, and `afterEach()`
      - [Cleanup Best Practices](https://docs.cypress.io/guides/references/best-practices.html#Using-after-or-afterEach-hooks)
- Watch mode
  - What is watched?
    - cypress.json
    - cypress.env.json
    - /cypress/integrations
    - /cypress/plugins
    - /cypress/support
  - What is NOT watched?
    - Application code
    - node_modules
    - /cypress/fixtures

### Retry-ability

- Cypress has two types of methods: `commands` and `assertions`
- If the assertion that follows a command fails, ONLY THE LAST command right before the assertion is retried along with the assertion
  - Cypress only retries commands that query the DOM
- With the last command retry restriction, sometimes complex assertions can be written as a should callback
  - Remember to ensure idempotent

### Interacting with Elements

- Actionable commands ensure the DOM element is "ready" to receive the action
  - Scroll the element into view
  - Ensure the element is not hidden
  - Ensure the element is not disabled
    - Cypress checks whether an element's disabled property is true
  - Ensure the element is not detached
    - Applications that rerender the DOM may actually remove the DOM element and insert a new one thus detaching the original element
  - Ensure the element is not readonly
    - Cypress checks whether an element's readonly property is set during `type()`
  - Ensure the element is not animating
    - `animationDistanceThreshold`
    - option to turn this off with `waitForAnimations`
  - Ensure the element is not covered
    - If a child of the element is covering it - that's okay. In fact we'll automatically issue th events we fire to that child.
      - I guess that makes sense? Will need to think if this is really ok
  - Scroll the page if still covered by an element with fixed position
    - The scrolling algorithm works by scrolling the top-left most point of the element we issued the command on to the top-left most scrollable point of its scrollable container.
  - Fire the event at the desired coordinates
    - By default it is centered within an element but can be configured
  - Debugging
    - Hovering over commands will scroll elements into the view, however this may not be the behavior of the actual test. So it is best to use `debugger` or `.debug()` for specific points in time
  - Forcing
    - Bypass checks with force option `{ force: true }`

### Variables and Aliases

- `.as()` to alias a command
- Common pairing would be with fixtures
  - `cy.fixture('user.json').as('users');` ... `this.users`
  - `this` will not work in arrow functions
- Aliases with DOM elements work slightly differently, the alias will preserve a reference to that element, but if that element is no longer in the DOM, then Cypress replays the commands leading up to the alias definition
- Aliasing of routes enables you to
  - ensure your application makes the intended requests
  - wait for your server to send the response
  - access the actual XHR object for assertions
  - `cy.route('POST', '/users', { id: 123 }).as('postUser')`
    - [Guide on Network Requests](https://docs.cypress.io/guides/guides/network-requests.html#Testing-Strategies)
- Aliasing of requests
  - `cy.request('https://jsonplaceholder.cypress.io/comments').as('comments')` ... `cy.get('@comments').should(resp => {})`

### Conditional Testing

- Strategies
  - Remove the need to ever do conditional testing
  - Force the application to behave deterministically
  - Check other sources of truth (like your server or database)
  - Embed data into other places (cookies/localStorage) you could read off
  - Add data to the DOM that you can read off to know how to proceed
- A/B campaign
  - URL params
  - Server response
  - Session cookies
  - Embedded data in the DOM
- Overall gist of this section - write 100% deterministic test and avoid conditional testing

### The Test Runner

- Each command and assertion, when hovered over, restores the Application Under Test to the state it was in when that command executed. This allows you to 'time-travel back to previous states of your application when testing. By default, Cypress keeps 50 tests worth of snapshots and command data for time traveling.
- `numTestsKeptInMemory` config (default = 50)
- Spies and Stub tracker
- Selector playground to find unique selectors for elements
- Shortcuts
  - `r` - rerun tests
  - `s` - stop tests
  - `f` - bring focus to `specs` window

## Dashboard

## Guides

### Command Line

### Module API

- Create custom scripts

### Debugging

- `.debug()`
- `cy.pause()`

### Network Requests

### Continuous Integration

### Parallelization

### Environment Variables

### Stubs, Spies, and Clocks

### Screenshots and Videos

### Launching Browsers

### Cross Browser Testing

### Web Security

## Tooling

### IDE Integration

### Plugins

### Reporters

### TypeScript

### Visual Testing

### Code Coverage

## References

### Assertions

### Configuration

### Proxy Configuration

### Best Practices

#### Organizing Tests, Logging In, Controlling State

- Anti-Pattern: Sharing page objects, using your UI to log in, and not taking shortcuts.
- Best Practice: Test specs in isolation, programmatically log into your application, and take control of your application's state
- [Examples which include OAuth](https://docs.cypress.io/examples/examples/recipes.html#Fundamentals)

#### Selecting Elements

- Anti-Pattern: Using highly brittle selectors that are subject to change
- Best Practice: Use data-* attributes to provide context to your selectors and isolate them from CSS or JS change
- `data-cy` `data-test` `data-testid`

#### Assigning Return Values

- Anti-Pattern: Trying to assign the return value of Commands with `const`, `let`, or `var`
- Best Practice: Use closures to access and store what Commands yield you.

#### Visiting external sites

- Anti-Pattern: Trying to visit or interact with sites or servers you do not control
- Best Practice: Only test what you control. Try to avoid requiring a 3rd party server. When necessary, always `cy.request()` to talk to 3rd party servers via their APIs.

#### Having tests rely on the state of the previous tests

- Anti-Pattern: Coupling multiple tests together.
- Best Practice: Tests should always be able to be run independently from one another and still pass.

#### Creating "tiny" tests with a single assertion

- Anti-Pattern: Acting like you're writing unit tests.
- Best Practice: Add multiple assertions and don't worry about it.

#### Using `after` or `afterEach` hooks

- Anti-Pattern: Using `after` and `afterEach` hooks to clean up state
- Clean up state before tests run.

- Dangling state is your friend
  - Cypress does not clean up its own internal state when the test ends.
- There is no guarantee that `after` and `afterEach` run. Therefore putting in cleanup steps in those will potentially leave you in a state that will require manual clean up
- Is resetting the state necessary?
  - Cypress automatically clears `localStorage`, `cookies`, `sessions`, etc before each test
  - The only times you ever need to clean up state, is if the operations that one test runs affects another test downstream. In only those cases do you need state cleanup.

#### Unnecessary Waiting
- Anti-Pattern: Waiting for arbitrary time periods using `cy.wait(Number)`.
- Best Practice: Use route aliases or assertions to guard Cypress from proceeding until an explicit condition is met.
- Almost never need to wait
  - Good use case of actually waiting is to wait on an aliased route. Example: Wait until response comes back.

#### Web Servers

- Anti-Pattern: Trying to start a web server from within Cypress scripts with `cy.exec()` or `cy.task()`.
- Best Practice: Start a web server prior to running Cypress.

#### Setting a global baseUrl

- Anti-Pattern: Using `cy.visit()` without setting a `baseUrl`.
- Best Practice: Set a `baseUrl` in your configuration file (`cypress.json` by default).

- Adding a `baseUrl` in your configuration allows you to omit passing the `baseUrl` to commands like `cy.visit()` and `cy.request()`. Cypress assumes this is the url you want to use.

### Error Messages

### Bundled Tools

### Trade-offs

### Troubleshooting

