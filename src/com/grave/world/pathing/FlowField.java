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
				int dx = (neighbor.x - x);
				int dy = (neighbor.y - y);
				int g = ((Math.abs(dx + dy) == 1) ? 10 : 14);
				int h = ((Math.abs(neighbor.x - target.x) + Math.abs(neighbor.y - target.y)) * 10);

				costs[neighbor.y][neighbor.x] = (g + h);

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

	private boolean inBounds(int x, int y) {
		if(map == null) return false;
		else return ((x >= 0) && (x < map.getMapWidth()) && (y >= 0) && (y < map.getMapHeight()));
	}
}
