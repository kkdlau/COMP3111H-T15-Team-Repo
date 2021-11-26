package comp3111.covid;

import java.util.*;

public class StringIndex {
	private static Map<String, Integer> continents = Map.of(
			"Africa", 0, 
			"Asia", 1, 
			"Europe", 2, 
			"North America", 3, 
			"South America", 4,
			"Oceania", 5
	);
	
	public static int ContinentIndex(String continent) {
		return continents.get(continent);
	}
}