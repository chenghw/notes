# Solidity

## Version Pragma

All solidity files should start with a `version pragma` - a declaration of the Solidity compiler this code should use.

```solidity
pragma solidity ^0.4.19;
```

## Contracts

Contract declaration:

```solidity
contract HelloWorld {

}
```

### Contract State

State variables are permanently stored in contract storage (written to the Ethereum blockchain).

```solidity
contract HelloWorld {
  string greeting = "Hello World!"
}
```

### Contact Events

Events are a way for your contract to communicate that something happened on the blockchain to your app front-end, which can be "listening" for certain events and take aciton when they happen.

```solidity
contract MyContract {
  event IntegersAdded(uint x, uint y, uint result);

  function add(uint _x, uint _y) public {
    uint result = _x + _y;

    IntegersAdded(_x, _y, result);
    return result;
  }
}
```

Front-end app would be listening...

```javascript
Contract.IntegersAdded(function(error, result) {

});
```

## Types

- `uint` - unsigned integer - its value must be non-negative. `uint` is an alias for `uint256` (256-bit unsigned integer), there are also `uint8`, `uint16`, `uint32`, etc...

- `string` - UTF-8 data string

- `struct` - Similar to an object/class in Javascript.

```solidity
struct Person {
  uint age;
  string name;
}

Person jon = Person(29, "Jon");
jon.age;
jon.name;
```

- Arrays - There are two types of arrays in Solidity: fixed arrays and dynamic arrays.

```solidity
// Array with a fixed length of 2 elements
uint[2] fixedArray;

// Array with a fixed length of 5 strings
strinf[5] stringArray;

// Dynamic Array - had no fixed size, can keep growing
uint[] dynamicArray;

// Dynamic Array of type Person struct
Person[] people;
```

- `address` - `msg.sender` - Global to retrieve the address of the account accessing the contract.

### Arrays

Array methods.

- `push` - Add given variable to the end of the array. Returns the count of total elements. - `people.push(jon);`

## Public/Private
Contract attributes can either be set as public of private. Setting an attribute to be public will automatically create a getter method for it. Other contract would then be able to read (but not write) to this attribute.

Syntax:

```solidity
Person[] public people;
```

## Internal/External

Two additional visibility types for functions are `internal` and `external`.

`internal` - is the same as `private`, except that its also accessible to contract that inherit from this contract.

`external` - is similar to public, except that these functions can ONLY be called outside the contract (cannot be called by other functions inside that contract).


## Math

- Addition - `x + y`
- Subtraction - `x - y`
- Multiplication - `x * y`
- Division - `x / y`
- Modulus - `x % y`
- Exponential - `x ** y`

## Functions

Functions are by default public. Convention for private methods have function names leading with an underscore. Declaration examples:

```solidity
// Public function
// Returns undefined
function giveGift(string _from, string _to, uint _amount) {

}

// Private function
// Returns undefined
function _giveGift(string _from, string _to, uint _amount) private {

}

// Private function
// Returns string
function _giveGift(string _from, string _to, uint _amount) private returns (string) {
  string baz = "baz";
  return baz;
}

// Function execution
giveGift("foo", "bar", 8);
_giveGift("foo", "bar", 8);
```

It is convention to start function parameter variable names with an underscore in order to differentiate them from global variables.

### Function Modifiers (view/pure)

A function that doesn't change state in Solidity (doesn't change any values or write anything) can be declared as a `view` function.

```solidity
function _giveGift(string _from, string _to, uint _amount) private view returns (string) {

}
```

Solidity also contains `pure` functions, which means you're not accessing any data within the app/contract. It's return value is only dependent on the parameters passed to it.

```solidity
function _giveGift(string _from, string _to, uint _amount) private pure returns (string) {

}
```

## keccak256

Ethereum has the hash function `keccak256` build in, which is a version of SHA3. `keccak256` maps an inputted string into a random 256-bit hexidecimal number.

## Typecasting

Typecasting - converting between data types.

```solidity
uint8 foo = 5;
uint bar = 8;

uint baz = foo * bar;
// error thrown because foo * bar returns a uint, not uint8

uint baz = foo * uint8(bar);
// success
```

## Address

The Ethereum blockchain is made up of accounts. An account has a balance of Ether and you can send and receive Ether payments to/from other accounts. Each account has an `address`.

`msg.sender` - Global to retrieve the address of the account accessing the contract.

## Mapping

`Mappings` are a way of storing organized data in Solidity. `Mappings` are essentially key-value pairs.

```solidity
mapping (address => uint) public accountBalance;
maaping (uint => string) userIdToName;

accountBalance[msg.sender] = 100;
accountBalance[msg.sender]; // returns 100;
```

## Require

`require` makes it so that a function will throw an error and stop executing if some condition is not true.

```solidity
function foo(string _bar) public returns (string) {
  // Solidity doesn't have native string comparison,
  // so keccak256 must be used to compare hashes
  require(keccak256(bar) == keccak256("baz"));

  return "baz";
}
```

## Inheritance

Inheritance does not inherit privates.

```solidity
contract Animal {
  function breathe() public {

  }
}

contract Cat is Animal {
  function meow() public returns (string) {
    returns "Meow!";
  }
}
```

## Import

Code can be split into many files and imported with the `import` keyword.

```solidiy
import "./FooContract.sol";

contact BarContract is FooContract {

}
```

## Storage vs Memory

There are two places where variables can be stored - in storage and in memory.

`storage` - variables stored permanently on the blockchain.

`memory` - temporary variables, erased between external function calls to the contract.

## Interface



## Web3

## Questions and Other Notes
