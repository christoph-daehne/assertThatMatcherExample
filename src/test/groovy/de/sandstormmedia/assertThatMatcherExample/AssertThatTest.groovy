package de.sandstormmedia.assertThatMatcherExample

import org.junit.Test

import static de.sandstormmedia.assertThatMatcherExample.PersonMatchers.isPersonLike
import static de.sandstormmedia.assertThatMatcherExample.PersonMatchers.isPersonLike2
import static org.junit.Assert.*

/**
 * Created by christoph on 16.01.15.
 */
class AssertThatTest {

	/**
	 * this test should fail but passes
	 * since only the nicknames are compared
	 */
	@Test
	public void checkByEquals() {
		def expected = new Person(nickname: 'alice', name: "Alice")
		def actual = new Person(nickname: 'alice', name: "Bob")

		// this assertion should fail
		assertEquals expected, actual
	}

	/**
	 * here the name is validated as well
	 */
	@Test
	public void checkNameToo() {
		def expected = new Person(nickname: 'alice', name: "Alice")
		def actual = new Person(nickname: 'alice', name: "Alice")

		// we add error messages here to make the test output a bit more readable
		assertEquals "incorrect nickname", expected.nickname, actual.nickname
		assertEquals "incorrect name", expected.name, actual.name
	}

	/**
	 * This test validates the children as well and
	 * starts to grow and look ugly.
	 */
	@Test
	public void checkChildrenAsWell() {
		def expected = new Person(nickname: 'alice', name: "Alice")
		expected.children = [
			new Person(nickname: 'little_bob', name: 'Bob', children: [expected]),
			new Person(nickname: 'little_con', name: 'Constantin', children: [expected])
		]
		def actual = new Person(nickname: 'alice', name: "Alice")
		actual.children = [
			new Person(nickname: 'little_bob', name: 'Bob', children: [actual]),
			new Person(nickname: 'little_con', name: 'Constantin', children: [actual])
		]

		assertEquals "incorrect nickname", expected.nickname, actual.nickname
		assertEquals "incorrect name", expected.name, actual.name
		assertEquals "incorrect number of children", expected.children.size(), actual.children.size()
		for (def actualFriend in actual.children) {
			def expectedFriend = expected.children.find { it.nickname == actualFriend.nickname }
			assertNotNull "unexpected friend '$actualFriend.nickname'", expectedFriend

			assertEquals "incorrect name", actualFriend.name, expectedFriend.name
			// TODO: compare the children of this child as well
		}
	}

	/**
	 * here we already test the content of the children and grand-children as well
	 */
	@Test
	public void comparisonWithMatcher() {
		def expected = new Person(nickname: 'alice', name: "Alice", children: [
			new Person(nickname: 'little_bob', name: 'Bob', children: [
				new Person(nickname: 'tiny_dave', name: 'Dave')
			]),
			new Person(nickname: 'little_con', name: 'Constantin')
		])
		def actual = new Person(nickname: 'alice', name: "Alice", children: [
			new Person(nickname: 'little_bob', name: 'Bob', children: [
				new Person(nickname: 'tiny_dave', name: 'Dave')
			]),
			new Person(nickname: 'little_con', name: 'Constantin')
		])

		assertThat "unexpected Alice", actual, isPersonLike(expected)
	}

	/**
	 * this test fails on purpose to show nice error message
	 */
	@Test
	public void comparisonWithMatcherAndHelpfulErrorMessage() {
		def expected = new Person(nickname: 'alice', name: "Alice", children: [
			new Person(nickname: 'little_bob', name: 'Bob', children: [
				new Person(nickname: 'tiny_dave', name: 'Dave')
			]),
			new Person(nickname: 'little_con', name: 'Constantin')
		])
		def actual = new Person(nickname: 'alice', name: "Alice", children: [
			new Person(nickname: 'little_bob', name: 'Bob', children: [
				new Person(nickname: 'tiny_dave', name: 'David')
			]),
			new Person(nickname: 'little_con', name: 'Constantin')
		])

		// TODO: fix this test if you like
		assertThat "unexpected Alice", actual, isPersonLike2(expected)
	}


}
