# How to write better tests using assertThat

The content of this README file is available in German as well at [sandstorm-media.de(TODO:link)](http://sandstorm-media.de).
All example code is written in [Groovy](http://groovy.codehaus.org/).

## testing can be wonderful

To me writing tests and developing software are not two different things.
They belong together and best are part of one easy and quick process.
It can make life much more pleasant to have well-written tests in your project.
They usually increase my joy and productivity at work.

However they do not write and maintain themselves.
To avoid spending hours in fixing (and reading) test-code and let tests become great obstacles during development, they need to be written with care.
I want to be honest: Tests I write are hardly as accurate as the productive code but still I consider them far from being hard-to-read copy-pasted spaghetti code. You know what I mean.

Here I want to show one approach I use to validate complex objects and object graphs without messing up my test code.

## validating primitive variables

Like other test frameworks [jUnit](http://junit.org/) offers a handful of assertions to validate some actual data against the programmers assertions.

I like to use them since they

* make tests more easy to read and
* produce failure message which help narrow down the problem.

```groovy
assertEquals "incorrect nickname", 'alice', 'alice-wrong'
// we get:
// incorrect nickname expected:<alice[]> but was:<alice[-wrong]>
```
In this example comparing the two strings works just fine.
The error message even emphasizes the difference - just great.

Now I want this to work just as good for much more complex data structures with nested objects as well.

## validating complex objects
One way to get there is to use ```assertThat```.
I want to show how and why in the follow example.

Assume you have a simple entity ```Person```.

```groovy
class Person {
	String nickname
	String name
}
```

### lazy approach

```groovy
assertEquals expectedPerson, actualPerson
```

This might actually work depending on the implementation of ```equals()``` and ```hashCode()```
but in our example it would compare the persons by object reference.

Even if the content would be validated the way I want in this test,
the failure message would be only contain the ```toString()``` results
- possibly only the ```nickname```.

### eager approach

```groovy
assertEquals "incorrect nickname", expected.nickname, actual.nickname
assertEquals "incorrect name", expected.name, actual.name
```

This is slightly better than the approach before.
It compares the persons by content and the failure messages are really helpful.

### things get more complex

For now the **eager approach** may just work out.
One could even put the two lines into an extra method ```assertPerson```.

That is why I want to make things a bit more tricky by adding a list of children to my person.

```groovy
class Person {
	String nickname
	String name
	List<Person> children = []
}
```

### eager approach including children

Now my **eager approach** starts to loose attractiveness.

```groovy
assertEquals "incorrect nickname", expected.nickname, actual.nickname
assertEquals "incorrect name", expected.name, actual.name
assertEquals "incorrect number of children", expected.children.size(), actual.children.size()
for (def actualFriend in actual.children) {
	def expectedFriend = expected.children.find { it.nickname == actualFriend.nickname }
	assertNotNull "unexpected friend '$actualFriend.nickname'", expectedFriend

	assertEquals "incorrect name", actualFriend.name, expectedFriend.name
	// TODO: compare the children of this child as well
}
```

Just to understanding this I need more time than to understand the ```Person```
and it is most definitely something I do not want to spread throughout all my person-validating tests.
Since the order of the children does not matter to me the validation becomes even more tedious.

I do not consider this validating code nice any more.
It is error-prone and delivers not helpful failure message and
it does not even test the content of grand-children.

## hamcrest matcher approach

Using [Hamcrest](http://hamcrest.org/JavaHamcrest/) matchers can give the test code a much better shape.
This example contains two ```Person``` matchers.

### simple person matcher

The ```PersonMatchers.PersonMatcher``` contains a straight forward implementation of a matcher validating a ```Person```.
The matcher does already validate all children, grand-children, great-grand-children and so on.
It does not support reference cycles though.

With the comparison code hidden in the ```PersonMatcher``` the test code looks very neat.

```groovy
assertThat "unexpected Alice", actual, isPersonLike(expected)
// in case of a failure we get
// unexpected Alice
// Expected: <alice>
//     but: was <alice>
```

I can very well use ```isPersonLike``` in several test classes avoiding copy-paste code.

This is already very cool except for the failure message.

### better person matcher

As for strings I want to see exactly why the test fails.
I expect the incorrect value somewhere in the actual ```Person``` emphasized which causes my test to fail.
So to say I want to see the path through all children and grand-children to the property with the wrong content.

```groovy
assertThat "unexpected Alice", actual, isPersonLike(expected)
// in case of error we get
// MisMatchException: incorrect child: expected little_bob but is little_bob
// cause:	incorrect child: expected tiny_dave but is tiny_dave
// cause:	name incorrect: expected 'Dave' but is 'David'
```

The matcher ```PersonMatchers.BetterPersonMatcher``` contains an implementation using a special ```MismatchException``` to generate the failure message.

This kind of test code and failure message gives me a nice feeling.
I think that if one of my fellow team members needs to fix broken tests or write some new ones,
she or he starts right away without the need to decrypt long failure messages and tests.
More time for features and more fun for us!

# Questions and Feedback

Remarks and comments are always welcome.
You can find us at [sandstorm-media.de](http://sandstorm-media.de/).
There are probably some typos.


Christoph Dähne  
Sandstorm Media
