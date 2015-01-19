package de.sandstormmedia.assertThatMatcherExample

/**
 * A person with children (optional)
 */
class Person {

	/**
	 * nickname is unique among all persons
	 */
	String nickname

	/**
	 * arbitrary name
	 */
	String name

	/**
	 * list of children
	 */
	List<Person> children = []

	public boolean equals(o) {
		if (this.is(o)) {
			return true
		}
		if (!(o instanceof Person)) {
			return false
		}
		Person person = (Person) o
		return nickname == person.nickname
	}

	public int hashCode() {
		return (nickname != null ? nickname.hashCode() : 0)
	}

	@Override
	public String toString() {
		return nickname
	}
}
