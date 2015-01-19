package de.sandstormmedia.assertThatMatcherExample

/**
 * thrown if there is a mismatch found by a {@link org.hamcrest.Matcher}
 */
class MismatchException extends Exception {

	private final Object expected
	private final Object actual

	/**
	 * constructor
	 *
	 * @param message custom message to help fix the broken test
	 * @param expected expected value
	 * @param actual actual value
	 * @param throwable cause
	 */
	public MismatchException(String message, Object expected, Object actual, Throwable throwable = null) {
		super(message, throwable)
		this.actual = actual
		this.expected = expected
	}

	@Override
	public String getMessage() {
		def message = "${super.getMessage()}: expected ${expected?.inspect()} but is ${actual?.inspect()}"
		if (cause == null) {
			return message
		} else {
			return "$message\ncause:\t$cause.message"
		}
	}

}
