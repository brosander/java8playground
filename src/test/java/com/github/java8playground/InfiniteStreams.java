package com.github.java8playground;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author nhudak
 */
public class InfiniteStreams {

  private static class FibGen implements Supplier<Integer> {
    LinkedList<Integer> queue = new LinkedList<>();

    {
      queue.add( 0 );
      queue.add( 1 );
    }

    @Override public Integer get() {
      queue.add( queue.getFirst() + queue.getLast() );
      return queue.pop();
    }
  }

  @Test
  public void IterativeLimit() throws Exception {
    List<Integer> values = new ArrayList<>( 10 );
    Supplier<Integer> fibGen = new FibGen();

    while ( values.size() < 10 ) {
      values.add( fibGen.get() );
    }

    assertThat( values, contains( 0, 1, 1, 2, 3, 5, 8, 13, 21, 34 ) );
  }

  @Test
  public void StreamLimit() throws Exception {
    List<Integer> values = Stream.generate( new FibGen() )
      .limit( 10 ) // Short-circuit intermediate operator
      .collect( Collectors.toList() );

    assertThat( values, contains( 0, 1, 1, 2, 3, 5, 8, 13, 21, 34 ) );
  }

  @Test
  public void IterativeFilter() throws Exception {
    List<Integer> values = new ArrayList<>( 10 );
    Supplier<Integer> fibGen = new FibGen();

    while ( values.size() < 10 ) {
      Integer next = fibGen.get();
      if ( next % 2 == 0 ) {
        values.add( next );
      }
    }

    assertThat( values, contains( 0, 2, 8, 34, 144, 610, 2584, 10946, 46368, 196418 ) );
  }

  @Test
  public void StreamFilter() throws Exception {
    List<Integer> values = Stream.generate( new FibGen() )
      .filter( i -> i % 2 == 0 )
      .limit( 10 ) // Short-circuit intermediate operator
      .collect( Collectors.toList() );

    assertThat( values, contains( 0, 2, 8, 34, 144, 610, 2584, 10946, 46368, 196418 ) );
  }

  @Test
  public void IterativeIndex() throws Exception {
    Function<Integer, Integer> indexOf = ( value ) -> {
      Supplier<Integer> fibGen = new FibGen();
      int index = 0;
      while ( true ) {
        Integer next = fibGen.get();
        if ( next == (int) value ) {
          return index;
        } else if ( next > (int) value ) {
          return -1;
        }
        index++;
      }
    };

    assertThat( indexOf.apply( 196418 ), equalTo( 27 ) );
    assertThat( indexOf.apply( 200000 ), equalTo( -1 ) );
  }

  @Test
  public void StreamIndex() throws Exception {
    IntFunction<Integer> indexOf = ( value ) -> {
      AtomicInteger index = new AtomicInteger( 0 );
      return Stream.generate( new FibGen() )
        .map( input -> new int[] { input, index.getAndIncrement() } )
        .filter( tuple -> tuple[0] >= value )
        .findFirst() // Terminal short-circuit, returns Optional
        .filter( tuple -> tuple[0] == value ).map( tuple -> tuple[1] ).orElse( -1 );
    };

    assertThat( indexOf.apply( 196418 ), equalTo( 27 ) );
    assertThat( indexOf.apply( 200000 ), equalTo( -1 ) );
  }

}
