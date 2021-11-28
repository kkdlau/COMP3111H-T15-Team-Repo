package comp3111.covid;

import java.util.*;

/**
 * Utility class to convert strings and indices
 *
 */
public class StringIndexUtils {
    private static Map<String, Integer> continents = Map.of(
            "Africa", 0,
            "Asia", 1,
            "Europe", 2,
            "North America", 3,
            "South America", 4,
            "Oceania", 5
    );

    /**
     * Return index for continent
     * @param continent
     * @return index in continents map
     */
    public static int ContinentIndex(String continent) {
        return continents.get(continent);
    }
}