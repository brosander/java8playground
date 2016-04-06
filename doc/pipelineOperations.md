# Pipeline Operations #

A stream pipeline consists of
* A source (A Collection, an array, a generator function, or an I/O channel)
* Zero or more intermediate operations (Stream.filter or Stream.map)
* A terminal operation such as Stream.forEach or Stream.reduce.

## Intermediate operations ##
Intermediate operations return a new stream.
They are always lazy; executing an intermediate operation such as filter() does not actually perform any filtering, but instead creates a new stream that, when traversed, contains the elements of the initial stream that match the given predicate.
Traversal of the pipeline source does not begin until the terminal operation of the pipeline is executed.

## Terminal operations ##
Terminal operations, such as Stream.forEach or IntStream.sum, may traverse the stream to produce a result or a side-effect.
After the terminal operation is performed, the stream pipeline is considered consumed, and can no longer be used.
If you need to traverse the same data source again, you must return to the data source to get a new stream.

### Iteration ###
In almost all cases, terminal operations are eager, completing their traversal of the data source and processing of the pipeline before returning.
Only the terminal operations iterator() and spliterator() are not; these are provided as an "escape hatch" to enable arbitrary client-controlled pipeline traversals in the event that the existing operations are not sufficient to the task.

### Short-circuiting ###
Intermediate and terminal operations can be short-circuiting if, given infinite input, can return a finite stream or finite time.

## Lazy Processing ##
Processing streams lazily allows for significant efficiencies;
in a pipeline such as the filter-map-sum, filtering, mapping, and summing can be fused into a single pass on the data, with minimal intermediate state.
Laziness also allows avoiding examining all the data when it is not necessary;

for operations such as "find the first string longer than 1000 characters",
it is only necessary to examine just enough strings to find one that has the desired characteristics
without examining all of the strings available from the source.
(This behavior becomes even more important when the input stream is infinite and not merely large.)

### Stateless vs. Stateful ###
Intermediate operations are further divided into stateless and stateful operations.

* Stateless operations, such as filter and map, retain no state from previously seen element when processing a new element
  * Each element can be processed independently of operations on other elements.
* Stateful operations, such as distinct and sorted, may incorporate state from previously seen elements when processing new elements.
  * Stateful operations may need to process the entire input before producing a result.
  * For example, one cannot produce any results from sorting a stream until one has seen all elements of the stream.
