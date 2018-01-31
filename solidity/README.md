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

- `struct` - Similar to an object/class in Javascript. `struct`s can only be passed as an argument to `private` or `internal` functions.

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

A function that doesn't change state in Solidity (doesn't change any values or write anything) can be declared as a `view` function. `view` functions do not cost `gas` when they are called externally by a user.

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

## if

`if` statements are the same in Solidity as they are in Javascript.

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

Interfaces are used for contracts to talk to one another on the blockchain. Interfaces only declare functions that are to be exposed outside of the contract. Exposing functions are essentially the same as declaring a function but without any logic - instead of `{` `}` the function ends with `;`. In order to use the interface, the external contract's `address` must be known.

```solidity
// Interface example
contract NumberInferface {
  function getNum(address _myAddress) public view returns (uint);
}

contract MyContract {
  // The address of the contract on Ethereum
  address NumberInterfaceAddress = 0xab38...

  // Now numberContract is pointing to the other contract
  NumberInterface numberContract = NumberInterface(NumberInterfaceAddress);

  function someFunction() public {
    uint num = numberContract.getNum(msg.sender);
  }
}
```

## Multiple Return Values

Solidity's functions can return multiple values.

```solidity
function multipleReturns() internal returns (uint a, uint b, uint c) {
  return (1, 2, 3);
}

function processMultipleReturns() external {
  uint a;
  uint b;
  uint c;

  (a, b, c) = multipleReturns();
}

function getLastReturnValue() external {
  uint c;

  (,,c) = multipleReturns();
}
```

## Immutability

Once contracts are deployed to the blockchain, they are there permanently. If there is a bug in the deployed code, there is no patching the contract. Users would need to be alerted to start using a different smart contract that has the fix.

With external dependencies, its best to have functions to update variables within deployed contracts.

## Ownable

Setting a contract to be Ownable gives the creator special privledges.

## Contructors

A function with the same name as its contract is the contructor. Contructors are executed only one time when the contract is first created.

```solidity
contract MyContract {
  function MyContract() public {
    // Contructor which gets executed once on contract creation
  }
}
```

## Function Modifiers

Modifiers are kind of half-functions that are used to modify other functions, usually to check some requirements prior to execution. To end a modifier the follow syntax is used `_;`.

```solidity
modifier onlyOwner() {
  require(msg.sender == owner);
  _;
}

function someFunc() public onlyOwner {
  ...
}

mapping (uint => uint) public age;

modifier olderThan(uint _age, uint _userId) {
  require(age[_userId] >= _age);
  _;
}

function driveCar(uint _userId) public olderThan(16, _userId) {

}
```

## Gas

Gas - the fuel Ethereum DApps run on.

In Solidity, users have to pay every time they execute a function on a DApp using a currency called `gas`. Users buy gas with Ether, so ETH must be spent to execute functions on a DApp. The amount of gas required to execute a function depends on how complete the function's logic is. Each individual operation has a gas cost based roughly on how much computing resources will be required to perform that operation. The total gas cost of your function is the sum of the gas costs of all its individual operations. Code optimization == Reduced gas costs.

Why is gas necessary? When code is executed, every single node on the network needs to run that same function to verify its output - thousands of nodes verifying every function execution is what makes Ethereum decentralized, and its data immutable and censorship-resistant.

The creators of Ethereum wanted to make sure something couldn't clog up the network with an infinite loop, or hog all the networkd resources with really intensive computations. So they made it so transactions aren't free, and users have to pay for computation time as well as storage.

Struct packing to save gas. Normally there is no benefit to using sub-types because Solidity reserves 256 bits of storage regardless of the `uint` size. For example, using `uint8` instead of `uint256` won't save you any gas. But there's an exception to this: inside `struct`s. If you have multiple `uint`s instead a struct, using a smaller-sized uint when possible will allow Solidity to pack these variables together to take up less storage. For this reason, inside `struct`s, you'd want to use the smallest sub-types you can get away with. Clustering idential data types together (i.e. put them next to each other in the struct) so that Solidity can minimize the required storage space is ideal.

`view` functions do not cost `gas` wehn they are called externally by a user. Whenever possible for `external` functions, mark them as `view` is possible to save on `gas`.

`storage` is one of the more expensive operations in Solidity. In order to keep costs down, you want to avoid writing data to storage except when absolutely necessary. Sometimes this involves seemingly inefficient programming logic - like rebuilding an array in `memory` everytime a function is called instead of simply saving that array in a variable for quick lookups.

In most programming languages, looping over large data sets is expensive. But in Solidity, this is way cheaper than using `storage` if it's in an `external view` function, since view functions don't cost your users any `gas`.

## Time Units

Solidity provides some native units for dealing with time.

`now` will return the current unix timestamp (seconds passed since January 1st, 1970).

`seconds`, `minutes`, `hours`, `days`, `weeks`, and `years` are all keywords that convert to a `uint` of the number of seconds in that length of time. `1 minutes` converts to `60`.

## for loops

The syntax of `for` loops in Solidity is similar to JavaScript.

```solidity
for (uint i = 1; i <= 10; i++) {

}
```

## General Security

All `public` and `external` functions can be called by anyone that wishes to interact with the contract. Make sure these functions cannot be abused.

## Web3

## Questions and Other Notes
