package io.vedder.robotics.particlefilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Map {

  private final List<MapWall> walls;

  public Map(String mapPath) {
    try (Stream<String> stream = Files.lines(Paths.get(mapPath))) {
      walls = stream
          // Convert to list of list of 4 doubles.
          .map(l -> Arrays.asList(l.split(",")).stream().map(n -> Double.parseDouble(n)).collect(Collectors.toList()))
          // Convert MapWall.
          .map(t -> new MapWall(new Vector2d(t.get(0), t.get(1)), new Vector2d(t.get(2), t.get(3))))
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new IllegalStateException("Cannot open Map!");
    }
    System.out.println("Read " + walls.size() + " walls!");
  }

  public List<MapWall> getWalls() {
    return walls;
  }
}
