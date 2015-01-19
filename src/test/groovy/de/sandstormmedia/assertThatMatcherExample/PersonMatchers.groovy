package de.sandstormmedia.assertThatMatcherExample

import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
 * Created by christoph on 16.01.15.
 */
class PersonMatchers {

	/**
	 * Provides a simple matcher to validate a {@link Person}.
	 * It does not provides detailed information why two persons mismatch.
	 *
	 * @param expected expected value
	 * @return matcher
	 */
	public static Matcher<Person> isPersonLike(Person expected) {
		return new PersonMatcher(expected: expected)
	}

	/**
	 * Provides a matcher which helps to find the incorrect property of an incorrect person.
	 * It does not implement the {@link Matcher} as originally intended but it is
	 * still more helpful than {@link PersonMatcher}.
	 *
	 * @param expected expected value
	 * @return matcher
	 */
	public static Matcher<Person> isPersonLike2(Person expected) {
		return new BetterPersonMatcher(expected: expected)
	}

	/**
	 * see {@link #isPersonLike(de.sandstormmedia.assertThatMatcherExample.Person)}
	 */
	private static class PersonMatcher extends TypeSafeMatcher<Person> {

		/**
		 * the expected person
		 */
		Person expected

		@Override
		protected boolean matchesSafely(Person actual) {
			if (actual.nickname != expected.nickname) {
				return false
			}
			if (actual.name != expected.name) {
				return false
			}
			if (actual.children.size() != expected.children.size()) {
				return false
			}

			def expectedChildren = expected.children.sort({ it.nickname })
			def actualChildren = actual.children.sort({ it.nickname })
			for (int i = 0; i < expectedChildren.size(); i++) {
				def matcher = new PersonMatcher(
					expected: expectedChildren[i],
				)
				if (!matcher.matches(actualChildren[i])) {
					return false
				}
			}

			return true
		}

		/**
		 * Here we can describe the expected value in case the assertion failed.
		 * Though in theory I can show the difference between the (last) actual values
		 * and the expected one here, it feels difficult and I neither want to leak
		 * state from {@link #matchesSafely(de.sandstormmedia.assertThatMatcherExample.Person)}
		 * (like last argument it was called with) nor do I want to re-implement the comparison
		 * logic.
		 *
		 * @param description description to append information to
		 */
		@Override
		public void describeTo(Description description) {
			description.appendValue(expected)
		}
	}

	/**
	 * see {@link #isPersonLike2(de.sandstormmedia.assertThatMatcherExample.Person)}
	 */
	private static class BetterPersonMatcher extends TypeSafeMatcher<Person> {
		/**
		 * the expected person
		 */
		Person expected

		/**
		 * Compares the given actual value to an expected one.
		 *
		 * @param actual the actual value
		 * @return always true
		 * @exception MismatchException the actual value is incorrect
		 */
		@Override
		protected boolean matchesSafely(Person actual) {
			if (actual.nickname != expected.nickname) {
				throw new MismatchException("nickname incorrect", expected.nickname, actual.nickname)
			}
			if (actual.name != expected.name) {
				throw new MismatchException("name incorrect", expected.name, actual.name)
			}
			if (actual.children.size() != expected.children.size()) {
				throw new MismatchException("number of children incorrect", expected.children*.nickname, actual.children*.nickname)
			}

			def expectedChildren = expected.children.sort({ it.nickname })
			def actualChildren = actual.children.sort({ it.nickname })
			for (int i = 0; i < expectedChildren.size(); i++) {
				def expectedChild = expectedChildren[i]
				def actualChild = actualChildren[i]
				def matcher = new BetterPersonMatcher(expected: expectedChild)
				try {
					matcher.matches(actualChild)
				} catch (MismatchException cause) {
					throw new MismatchException("incorrect child", expectedChild, actualChild, cause)
				}
			}

			return true
		}

		/**
		 * This method is never called since the {@link MismatchException} breaks the
		 * test before the assertion fails.
		 */
		@Override
		public void describeTo(Description description) {
			throw new NotImplementedException()
		}
	}
}
