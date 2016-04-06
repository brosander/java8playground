package com.github.java8playground;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * @author nhudak
 */
public class SpliteratorForkJoinDemo {

  private class Point {
    private final int x, y;

    Point( int x, int y ) {
      this.x = x;
      this.y = y;
    }

    RecursiveTask<Collection<Point>> findClosestRecursive( final Spliterator<Point> points ) {
      return new RecursiveTask<Collection<Point>>() {
        @Override protected Collection<Point> compute() {
          ArrayList<ForkJoinTask<Collection<Point>>> forks = new ArrayList<>();
          Spliterator<Point> split;

          while ( ( split = points.trySplit() ) != null ) {
            forks.add( findClosestRecursive( split ).fork() );
          }

          Stream<Point> join = forks.stream()
            .map( ForkJoinTask::join )
            .flatMap( Collection::stream );
          Stream<Point> remainder = StreamSupport.stream( points, false );

          return findClosest( Stream.concat( remainder, join ) );
        }
      };

    }

    List<Point> findClosest( Stream<Point> stream ) {
      return stream
        .collect( Collectors.groupingBy(
          point -> Math.hypot( x - point.x, y - point.y ),
          TreeMap::new,
          Collectors.toList()
        ) )
        .entrySet().stream()
        .findFirst()
        .map( Map.Entry::getValue )
        .orElseGet( Collections::emptyList );
    }

    @Override public boolean equals( Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }
      Point point = (Point) o;
      return x == point.x &&
        y == point.y;
    }

    @Override public int hashCode() {
      return Objects.hash( x, y );
    }
  }

  private final List<Point> points = Arrays.asList(
    new Point( 1, 2 ), new Point( 1, 4 ), new Point( 17, 9 ), new Point( 13, 1 ), new Point( 15, 6 ),
    new Point( 19, 15 ), new Point( 15, 15 ), new Point( 5, 10 ), new Point( 14, 11 ), new Point( 12, 5 ),
    new Point( 16, 3 ), new Point( 13, 5 ), new Point( 11, 9 ), new Point( 11, 7 ), new Point( 16, 3 ),
    new Point( 2, 4 ), new Point( 8, 17 ), new Point( 3, 20 ), new Point( 3, 2 ), new Point( 13, 12 )
  );

  @Test
  public void searchIterative() throws Exception {
    assertThat( new Point( 9, 17 ).findClosest( points.stream() ), contains( new Point( 8, 17 ) ) );
    assertThat( new Point( 1, 3 ).findClosest( points.stream() ),
      containsInAnyOrder( new Point( 1, 2 ), new Point( 1, 4 ) ) );
  }

  @Test
  public void searchRecursive() throws Exception {
    RecursiveTask<Collection<Point>> closestRecursive = new Point( 9, 17 ).findClosestRecursive( points.spliterator() );
    new ForkJoinPool().execute( closestRecursive );

    assertThat( closestRecursive.get(), contains( new Point( 8, 17 ) ) );
  }
}
