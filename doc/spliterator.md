# Spliterators! #

An object for traversing and partitioning elements of a source.

A Spliterator may traverse elements individually (tryAdvance()) or sequentially in bulk (forEachRemaining()).

A Spliterator may also partition off some of its elements (using trySplit()) as another Spliterator, to be used in possibly-parallel operations. Operations using a Spliterator that cannot split, or does so in a highly imbalanced or inefficient manner, are unlikely to benefit from parallelism. Traversal and splitting exhaust elements; each Spliterator is useful for only a single bulk computation.
