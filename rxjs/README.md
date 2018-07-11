RxJS

RxJS is programming with event streams. Event streams are similar to arrays and have many of the same functions.

Classic example, double click detection.

var clickStream$ = Rx.Observable.fromEvent(buttonEl, 'click');
var doubleClickStream = doubleClickStream$ = clickStream
  .buffer(() => clickStream.throttle(250))
  .map(arr => arr.length)
  .filter(len => len === 2);

It allows you to specify the dynamic behavior of a value completely at the time of declaration.

`flatMap` can be thought of like `Promise.then`

`merge` combines two observable streams into a single observable

`startWith` prepends an observable with a given value

Subscribes happen in chains on the observable.

`shareReplay` will have 1 subscription

`withLatestFrom` takes an observable and emits the latest event from that stream.

--------------------------------

Observable - powerful generalization of a function

// Observable (PUSH)
// Producer
var bar = Rx.Observable.create(observer => {
  console.log('Hello');
  observer.next(42);
});

// Consumer
bar.subscribe(e => console.log(e));

`subscribe` is like `call` for functions. Each subscribe is independent from one another.

// Generator (PULL)
// Producer
function* baz() {
  console.log('hello');
  yield 42;
  yield 100;
  yield 200;
}

// Consumer
var iterator = baz();
console.log(iterator.next().value);
console.log(iterator.next().value);
console.log(iterator.next().value);

How do you handle errors from Observables.
observer.error(new Error('bad'));
`subscribe` takes a second function that will handle errors if any are thrown in the observable.

`observer.complete()` allows for the subscription to know when the observable has been completed.

`subscribe` third callback is for when complete is triggered.

Simple Observable and subscribe.
function subscribe(observer) {
  observer.next(42);
  observer.next(100);
  observer.next(200);
  observer.complete();
}
var observer = {
  next: function(x) { console.log(x) },
  error: function(err) { console.log(err) },
  complete: function() { console.log('complete')},
}
foo.subscribe(observer);

Creation operators from the `Observable` type.

- `of` - takes a splat of arguments and calls observer.next on each.
- `fromArray` - takes an array and calls observer.next on each.
- `fromPromise` - takes a promise as an argument only.
- `from` - takes an argument that is an array or promise and will do the same thing as `fromArray` and `fromPromise`. `from` takes a potential third type of argument, an iterator.
i.e.
function* generator() {
  yield 10;
  yield 20;
  yield 30;
}
var iterator = generator();
var foo = Rx.Observable.from(iterator);
- `fromEventPattern` - takes two arguments, addEventHandler and removeEventHandler
- `empty` - returns an empty observable. Equivalent to...
Rx.Observable.create(function(observer) {
  observer.complete();
})
- `never` - returns an "infinite" obervable.
Equivalent to...
Rx.Observable.create(function(observer) {});
- `throw` - returns an obervable that throws an error to the subscibe
- `interval` - takes one argument that is the period. It sets a separate interval for each subscribe.
- `timer` - similar to interval except it takes two arguments, startTime and the interval. startTime can even be a date. How does this work? Can this be applied as a timer for when the market closes to trigger a theme change?
- `create` - `Rx.Observable.create` is the same as `new Rx.Observable`

A `subscribe` will always return a subscription with will contain a function `unsubscribe`. This function should be declared upon creation.

Operators on Observables. These operators takes a source observable and outputs result observable. Source remains untouched in this process.

Abstracted example.
var foo = Rx.Observable.of(1,2,3,4,5);
function multiplyByTen(source) {
  var result = Rx.Observable.create(function subscribe(observer) {
    source.subscribe(
      function() {
      },
      function() {
      },
      function() {
      })
  });
  return result;
}
var bar = multiplyByTen(foo);

Non-abstracted
var foo = Rx.Observable.of(1,2,3,4,5);
function multiplyBy(multiplier) {
  var source = this;
  var result = Rx.Observable.create(function subscribe(observer) {
    source.subscribe(
      function(x) {
        observer.next(x * multiplier)
      },
      function() {
      },
      function() {
      })
  });
  return result;
}
Rx.Observable.prototype.multipleBy = multiplyBy;
var bar = foo.multiplyBy(100);

`subscription chain` - when subscribing to an observable from another observable etc (subscription upstream)

Marble diagram
(1234) - syncronous events

Operators
- `map` - `transformation`
- `mapTo` - `transformation` - same as map(() => 10)
- `do` - `utility` - similar to map, takes a function and does need to return anything. It will automaticaslly return the same value that is receives. Do does not trigger a subscription, but acts as a spy.
- `filter` - `filtering` - takes a predicate function - a function that returns true or false
- `take` - `filtering` - specify how many events to take and completes the observable afterwards.
- `first` - `filtering` - same as `take(1)`
- `skip` - `filtering` - specify how many events to ignore.

You can only refer to the end of an observable if there is a known completion to that observable.

- `takeLast` - `filtering`
--0--1--2--3--4--5--6--7-...
  take(5)
--0--1--2--3--4|
  takeLast(2)
---------------(34|)
- `last`
- `skipLast`
- `concat` - `combination` - instance operator foo.concat(bar) or static operator Observable.concat(foo, bar)
- `startWith` - `combination`
- `merge` - `OR vertical combination` - two or more Observables running in parallel. Combining like data types.
- `combineLatest` - `AND vertical combination` - two or more Observables running in parallel but needs a value from all observables in order to call next on its observer.
----0----1----2----(3|)
--0--1--2--3--(4|)
  combineLatest((x, y) => x+y)
----01--23-4--(56)-(7|)
Generally used for deriving a value from two or more sources of data. Combining different data types.
- `withLatestFrom` - `AND vertical combination` - mapWithLatestFrom - main observable runs the transformation but takes latest data from other observables
- `zip` - `AND vertical combination` -
First of foo + First of bar => First of output
Second of foo + Second of bar => Second of output
...
n-th of foo + n-th of bar => n-th of output
On first Observable completion, the subscription receives completion.
- `scan` - `horizontal combination` - basically reduce from Array
- `buffer` - `horizontal combination` - takes a closing observable as an argument. Uses that observable to know when to close the buffer and emit the events
- `bufferCount` - `horizontal combination` - takes an argument of how many events it takes before emitting the buffered event. Upon completion, internal buffer is emitted even if count has not been reached.
- `bufferTime` - `horizontal combination` - takes a time interval which closes the buffer and then emits the events after the given time has elapsed
- `bufferToggle`
- `bufferWhen`
- `delay` - `` - takes time interval as argument and delays the start of the observable. Takes a date as well.
- `delayWhen` - `` - takes a function which returns an observable of how long event should be delayed by
- `debounceTime` - `rate limiting transformation` - takes a time as an argument and waits for the given time amount of silence before emitting the most recent event
- `debounce` - `rate limiting filter` -
- `throttleTime` - `rate limiting fitler` - first emits and then causes n silence until next emit
- `auditTime` - `rate limiting filtering` -
- `distinct` - `filtering` - only emits unique values from the Observable. Also takes a custom compare function to declare what equality really means. Takes a second argument which is a flusher argument which is an observable which determines when the registry will be flushed.
- `distinctUntilChanged` - `filtering` -
--a--b--a--a--b|
  distinctUntilChanged
--a--b--a-----b|
- `catch` - `error handling` - takes a function which returns an Observable. function takes err and outputObservable (retry behavior).
- `retry` - `error handling` - takes the number of retries as an argument
- `retryWhen` - `error handling` - takes a function which returns an Observable
- `repeat` - `` - takes an argument of the total number of times to repeat - replace completion with input observable
