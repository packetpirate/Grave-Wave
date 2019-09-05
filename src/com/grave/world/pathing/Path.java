package com.grave.world.pathing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import com.grave.misc.OrderPair;
import com.grave.tmx.TMap;

public class Path {
	private List<OrderPair<Integer>> path;
	public List<OrderPair<Integer>> getPath() { return path; }
	public boolean pathPossible() { return !path.isEmpty(); }

	/**
	 * Creates a path between the source tile and the target tile.
	 * @param map The map used to calculate the path.
	 * @param src A position in the given map from where to start the path.
	 * @param target The destination in the given map to end the path.
	 */
	public Path(TMap map, OrderPair<Integer> src, OrderPair<Integer> target) {
		path = new ArrayList<OrderPair<Integer>>();
		calculate(map, src, target);
	}

	public void calculate(TMap map, OrderPair<Integer> src, OrderPair<Integer> target) {
		PriorityQueue<OrderPair<Integer>> frontier = new PriorityQueue<OrderPair<Integer>>();
		OrderPair<Integer> start = new OrderPair<Integer>(src, 0);
		OrderPair<Integer> goal = new OrderPair<Integer>(target, 0);
		frontier.add(start);

		HashMap<OrderPair<Integer>, OrderPair<Integer>> came_from = new HashMap<OrderPair<Integer>, OrderPair<Integer>>();
		came_from.put(start, null);

		HashMap<OrderPair<Integer>, Integer> cost_so_far = new HashMap<OrderPair<Integer>, Integer>();
		cost_so_far.put(start, 0);

		while(!frontier.isEmpty()) {
			OrderPair<Integer> current = frontier.poll();

			if(current == goal) break;

			List<OrderPair<Integer>> neighbors = getNeighbors(map, current);
			for(OrderPair<Integer> next : neighbors) {
				int new_cost = (cost_so_far.get(current) + cost(map, current, next));
				if(!cost_so_far.containsKey(next) || (new_cost < cost_so_far.get(next))) {
					cost_so_far.put(next, new_cost);
					int priority = (new_cost + heuristic(map, goal, next));
					next.setPriority(priority);
					frontier.add(next);
					came_from.put(next, current);
				}
			}
		}

		if(came_from.containsKey(goal)) { // If false, no path is possible and path will be empty.
			// Backtrack from the goal node to construct the path.
			path.clear();
			OrderPair<Integer> current = goal;
			while(!current.equals(start)) {
				path.add(current);
				current = came_from.get(current);
			}
		}
	}

	private int cost(TMap map, OrderPair<Integer> src, OrderPair<Integer> target) {
		int dx = (target.x - src.x);
		int dy = (target.y - src.y);
		int off = Math.abs(dx + dy);

		return ((off == 1) ? 10 : 14);
	}

	private int heuristic(TMap map, OrderPair<Integer> src, OrderPair<Integer> target) {
		return ((Math.abs(src.x - target.x) + Math.abs(src.y - target.y)) * 10);
	}

	private List<OrderPair<Integer>> getNeighbors(TMap map, OrderPair<Integer> current) {
		List<OrderPair<Integer>> neighbors = new ArrayList<OrderPair<Integer>>();

		int mWidth = map.getMapWidth();
		int mHeight = map.getMapHeight();

		for(int yOff = -1; yOff <= 1; yOff++) {
			for(int xOff = -1; xOff <= 1; xOff++) {
				if((xOff == 0) && (yOff == 0)) continue;
				int x = (current.x + xOff);
				int y = (current.y + yOff);

				if((x >= 0) && (x < mWidth) && (y >= 0) && (y < mHeight) && map.isWalkable(x, y)) {
					neighbors.add(new OrderPair<Integer>(x, y, 0));
				}
			}
		}

		return neighbors;
	}
}
