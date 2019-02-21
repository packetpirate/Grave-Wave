package com.gzsr.misc;

public class Pair<T> {
	public static final Pair<Float> ZERO = new Pair<Float>(0.0f, 0.0f);

	public T x, y;

	public Pair(T x, T y) {
		this.x = x;
		this.y = y;
	}

	public Pair(Pair<T> p) {
		this.x = p.x;
		this.y = p.y;
	}

	@Override
	public String toString() {
		return ("(" + x + ", " + y + ")");
	}

	/*
	 * IMPORTANT TO REMEMBER!!!!!
	 * Must implement hashCode and equals if you want to retrieve a HashMap value
	 * using an object as a key!
	 */

	@Override
	public int hashCode() {
		final int prime = 31;
        int result = 1;
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Pair other = (Pair)obj;
        if (x == null) {
            if (other.x != null) return false;
        } else if (!x.equals(other.x)) return false;
        if (y == null) {
            if (other.y != null) return false;
        } else if (!y.equals(other.y)) return false;
        return true;
    }
}
