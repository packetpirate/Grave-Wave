package com.grave.world.pathing;

import java.util.ArrayList;
import java.util.List;

import com.grave.misc.Pair;
import com.grave.tmx.TMap;

public class FlowField {
	private TMap map;
	private Pair<Integer> target;
	private int [][] costs;

	public FlowField(TMap map_, Pair<Integer> target_) {
		target = target_;
		map = map_;

		costs = new int[map.getMapHeight()][map.getMapWidth()];

		recalculate(target);
	}

	public void recalculate(Pair<Integer> target_) {
		target = target_;
		costs[target.y][target.x] = 0;

		List<Pair<Integer>> visited = new ArrayList<Pair<Integer>>();
		List<Pair<Integer>> unexplored = new ArrayList<Pair<Integer>>();

		visited.add(target);
		unexplored.add(target);

		// set the cost of non-traversable tiles to Integer.MAX_VALUE
		for(int y = 0; y < map.getMapHeight(); y++) {
			for(int x = 0; x < map.getMapWidth(); x++) {
				if(!map.isWalkable(x, y)) {
					costs[y][x] = Integer.MAX_VALUE;
				}
			}
		}

		while(!unexplored.isEmpty()) {
			List<Pair<Integer>> toExplore = new ArrayList<Pair<Integer>>();

			for(Pair<Integer> neighbor : unexplored) {
				List<Pair<Integer>> neighbors = explore(neighbor.x, neighbor.y, visited);
				visited.addAll(neighbors);
				toExplore.addAll(neighbors);
			}

			unexplored.clear();
			unexplored.addAll(toExplore);
		}
	}

	private List<Pair<Integer>> explore(int x, int y, List<Pair<Integer>> visited) {
		List<Pair<Integer>> explored = new ArrayList<Pair<Integer>>();
		List<Pair<Integer>> neighbors = getNeighbors(x, y);

		for(Pair<Integer> neighbor : neighbors) {
			if(!visited.contains(neighbor)) {
				int cost = (costs[y][x] + 1);
				costs[neighbor.y][neighbor.x] = cost;
				explored.add(neighbor);
			}
		}

		return explored;
	}

	private List<Pair<Integer>> getNeighbors(int x, int y) {
		List<Pair<Integer>> neighbors = new ArrayList<Pair<Integer>>();

		for(int dy = -1; dy <= 1; dy++) {
			for(int dx = -1; dx <= 1; dx++) {
				if((dx == 0) && (dy == 0)) continue;

				Pair<Integer> pos = new Pair<Integer>((x + dx), (y + dy));
				if(inBounds(pos.x, pos.y) && map.isWalkable(pos.x, pos.y)) {
					neighbors.add(pos);
				}
			}
		}

		return neighbors;
	}

	public Pair<Integer> cheapestNeighbor(int x, int y) {
		List<Pair<Integer>> neighbors = getNeighbors(x, y);
		Pair<Integer> cheapest = null;
		int lowestCost = Integer.MAX_VALUE;

		for(int i = 0; i < neighbors.size(); i++) {
			Pair<Integer> neighbor = neighbors.get(i);
			int cost = costs[neighbor.y][neighbor.x];

			if(cost < lowestCost) {
				lowestCost = cost;
				cheapest = neighbor;
			}
		}

		return cheapest;
	}

	private boolean inBounds(int x, int y) {
		if(map == null) return false;
		else return ((x >= 0) && (x < map.getMapWidth()) && (y >= 0) && (y < map.getMapHeight()));
	}

	public void print() {
		if(map != null) {
			for(int y = 0; y < map.getMapHeight(); y++) {
				String row = "";
				for(int x = 0; x < map.getMapWidth(); x++) {
					row += costs[y][x];
					if(x < (map.getMapWidth() - 1)) row += ", ";
				}

				System.out.println(row);
			}
		}
	}
}
