package main.java.zenit.util;

/**
 * General purpose boilerplate for a pair or 2-tuple.
 * @author Pontus Laos
 *
 * @param <T1> The first element.
 * @param <T2> The second element.
 */
public class Tuple<T1, T2> {
	private T1 fst;
	private T2 snd;
	
	/**
	 * Constructs a new 2-tuple with no initial values.
	 * @author Pontus Laos
	 */
	public Tuple() {}
	
	/**
	 * Constructs a new 2-tuple with the supplied values. Calls the set()-method.
	 * @param t1 The first value.
	 * @param t2 The second value.
	 * @author Pontus Laos
	 */
	public Tuple(T1 fst, T2 snd) {
		this();
		set(fst, snd);
	}
	
	/**
	 * Sets the tuples' values.
	 * @param t1 The first value.
	 * @param t2 The second value.
	 * @author Pontus Laos
	 */
	public void set(T1 fst, T2 snd) {
		this.fst = fst;
		this.snd = snd;
	}
	
	/**
	 * Gets the first value.
	 * @return The first value in the tuple.
	 * @author Pontus Laos
	 */
	public T1 fst() {
		return fst;
	}
	
	/**
	 * Gets the second value.
	 * @return The second value in the tuple.
	 * @author Pontus Laos
	 */
	public T2 snd() {
		return snd;
	}
	
	/**
	 * Represents the tuple as a String on the form (fst, snd).
	 * @author Pontus Laos
	 */
	@Override
	public String toString() {
		return String.format("(%s, %s", fst, snd);
	}
}
