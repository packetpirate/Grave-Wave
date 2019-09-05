package com.grave.misc;

public class OrderPair<T> extends Pair<T> implements Comparable<OrderPair<T>> {
	private int priority;
	public int getPriority() { return priority; }
	public void setPriority(int new_priority) { priority = new_priority; }

	public OrderPair(T x_, T y_, int priority_) {
		super(x_, y_);

		this.priority = priority_;
	}

	public OrderPair(Pair<T> pair_, int priority_) {
		super(pair_.x, pair_.y);

		this.priority = priority_;
	}

	@Override
	public int compareTo(OrderPair<T> other) {
		return Integer.compare(priority, other.getPriority());
	}
}
