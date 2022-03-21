/*
 *
 * Copyright (c) 2006-2020, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.common.singletonstream;

import static com.speedment.common.singletonstream.internal.SingletonUtil.MESSAGE_STREAM_CONSUMED;
import static com.speedment.common.singletonstream.internal.SingletonUtil.STRICT;
import static java.util.Objects.requireNonNull;

import com.speedment.common.singletonstream.internal.SingletonPrimitiveIteratorOfLong;
import com.speedment.common.singletonstream.internal.SingletonPrimitiveSpliteratorOfLong;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * An implementation of a LongStream that takes exactly one element as its
 * source.
 *
 * This implementation supports optimized implementations of most terminal
 * operations and a some number of intermediate operations. Un-optimized
 * operations just returns a wrapped standard LongStream implementation.
 *
 * For performance reasons, the LongStream does not throw an
 * IllegalStateOperation if methods are called after a terminal operation has
 * been called on the Stream. This could be implemented using a boolean value
 * set by each terminating op. All other ops could then assert this flag.
 *
 * @author Per Minborg
 * @since  1.0.0
 */
public class SingletonLongStream implements LongStream {

    private final long element;

    private boolean parallel;

    private boolean consumed;

    private List<Runnable> closeHandlers;

    private SingletonLongStream(long element) {
        this.element = element;
    }

    public static LongStream of(long element) {
        return new SingletonLongStream(element);
    }

    @Override
    public LongStream filter(LongPredicate predicate) {
        requireNonNull(predicate);
        if (STRICT) {
            return toStream().filter(predicate);
        }
        return predicate.test(element) ? this : empty();
    }

    @Override
    public LongStream map(LongUnaryOperator mapper) {
        requireNonNull(mapper);
        if (STRICT) {
            return toStream().map(mapper);
        }
        return of(mapper.applyAsLong(element));
    }

    @Override
    public <U> Stream<U> mapToObj(LongFunction<? extends U> mapper) {
        requireNonNull(mapper);
        if (STRICT) {
            return toStream().mapToObj(mapper);
        }
        return SingletonStream.of(mapper.apply(element));
    }

    @Override
    public IntStream mapToInt(LongToIntFunction mapper) {
        requireNonNull(mapper);
        if (STRICT) {
            return toStream().mapToInt(mapper);
        }
        return SingletonIntStream.of(mapper.applyAsInt(element));
    }

    @Override
    public DoubleStream mapToDouble(LongToDoubleFunction mapper) {
        requireNonNull(mapper);
        if (STRICT) {
            return toStream().mapToDouble(mapper);
        }
        return DoubleStream.of(mapper.applyAsDouble(element));
    }

    @Override
    public LongStream flatMap(LongFunction<? extends LongStream> mapper) {
        requireNonNull(mapper);
        if (STRICT) {
            return toStream().flatMap(mapper);
        }
        return mapper.apply(element);
    }

    @Override
    public LongStream distinct() {
        return this;
    }

    @Override
    public LongStream sorted() {
        return this;
    }

    @Override
    public LongStream peek(LongConsumer action) {
        requireNonNull(action);
        return toStream().peek(action);
    }

    @Override
    public LongStream limit(long maxSize) {
        if (maxSize == 0) {
            return empty();
        }
        if (maxSize > 0) {
            return this;
        }
        throw new IllegalArgumentException(Long.toString(maxSize));
    }

    @Override
    public LongStream skip(long n) {
        if (n == 0) {
            return this;
        }
        if (n > 0) {
            return empty();
        }
        throw new IllegalArgumentException(Long.toString(n));
    }

    @Override
    public void forEach(LongConsumer action) {
        checkConsumed();
        requireNonNull(action);
        action.accept(element);
    }

    @Override
    public void forEachOrdered(LongConsumer action) {
        checkConsumed();
        requireNonNull(action);
        action.accept(element);
    }

    @Override
    public long[] toArray() {
        checkConsumed();
        final long[] result = new long[1];
        result[0] = element;
        return result;
    }

    @Override
    public long reduce(long identity, LongBinaryOperator op) {
        checkConsumed();
        requireNonNull(op);
        return op.applyAsLong(identity, element);
    }

    @Override
    public OptionalLong reduce(LongBinaryOperator op) {
        checkConsumed();
        // Just one element so the accumulator is never called.
        return toOptional();
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        checkConsumed();
        requireNonNull(supplier);
        requireNonNull(accumulator);
        final R value = supplier.get();
        accumulator.accept(value, element);
        // the combiner is never used in a non-parallell stream
        return value;
    }

    @Override
    public long sum() {
        checkConsumed();
        return element;
    }

    @Override
    public OptionalLong min() {
        checkConsumed();
        return toOptional();
    }

    @Override
    public OptionalLong max() {
        checkConsumed();
        return toOptional();
    }

    @Override
    public long count() {
        checkConsumed();
        return 1;
    }

    @Override
    public OptionalDouble average() {
        checkConsumed();
        return OptionalDouble.of(element);
    }

    @Override
    public LongSummaryStatistics summaryStatistics() {
        checkConsumed();
        final LongSummaryStatistics result = new LongSummaryStatistics();
        result.accept(element);
        return result;
    }

    @Override
    public boolean anyMatch(LongPredicate predicate) {
        checkConsumed();
        requireNonNull(predicate);
        return predicate.test(element);
    }

    @Override
    public boolean allMatch(LongPredicate predicate) {
        checkConsumed();
        requireNonNull(predicate);
        return predicate.test(element);
    }

    @Override
    public boolean noneMatch(LongPredicate predicate) {
        checkConsumed();
        requireNonNull(predicate);
        return !predicate.test(element);
    }

    @Override
    public OptionalLong findFirst() {
        checkConsumed();
        return toOptional();
    }

    @Override
    public OptionalLong findAny() {
        checkConsumed();
        return toOptional();
    }

    @Override
    public DoubleStream asDoubleStream() {
        return DoubleStream.of(element);
    }

    @Override
    public Stream<Long> boxed() {
        return mapToObj(Long::valueOf);
    }

    @Override
    public LongStream sequential() {
        this.parallel = false;
        return this;
    }

    @Override
    public LongStream parallel() {
        this.parallel = true;
        return this;
    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        checkConsumed();
        return new SingletonPrimitiveIteratorOfLong(element);
    }

    @Override
    public Spliterator.OfLong spliterator() {
        checkConsumed();
        return new SingletonPrimitiveSpliteratorOfLong(element);
    }

    @Override
    public boolean isParallel() {
        return parallel;
    }

    @Override
    public LongStream unordered() {
        return this;
    }

    @Override
    public LongStream onClose(Runnable closeHandler) {
        checkConsumed(false);

        if (closeHandler == null) {
            return this;
        }

        if (closeHandlers == null) {
            this.closeHandlers = new ArrayList<>();
        }

        closeHandlers.add(closeHandler);
        return this;
    }

    @Override
    public void close() {
        consumed = true;

        if (closeHandlers == null || closeHandlers.isEmpty()) {
            return;
        }

        closeHandlers.forEach(Runnable::run);
        closeHandlers = null;
    }

    private LongStream toStream() {
        final LongStream stream = LongStream.of(this.element);
        return parallel ? stream.parallel() : stream;
    }

    private OptionalLong toOptional() {
        // if element is null, Optional will throw an NPE 
        // just as the standard Stream implementation does.
        return OptionalLong.of(element);
    }

    private static LongStream empty() {
        return LongStream.empty();
    }

    private void checkConsumed() {
        checkConsumed(true);
    }

    private void checkConsumed(boolean setConsumed) {
        if (consumed) {
            throw new IllegalStateException(MESSAGE_STREAM_CONSUMED);
        }

        if (setConsumed) {
            consumed = true;
        }
    }

    // Java 9 Stream features
    /**
     * Returns, if this stream is ordered, a stream consisting of the longest
     * prefix of elements taken from this stream that match the given predicate.
     *
     * @param predicate - a non-interfering, stateless predicate to apply to
     * elements to determine the longest prefix of elements.
     * @return the new stream
     */
    public LongStream takeWhile(LongPredicate predicate) {
        requireNonNull(predicate);
        if (predicate.test(element)) {
            return this;
        } else {
            return empty();
        }
    }

    /**
     * Returns, if this stream is ordered, a stream consisting of the remaining
     * elements of this stream after dropping the longest prefix of elements
     * that match the given predicate. Otherwise returns, if this stream is
     * unordered, a stream consisting of the remaining elements of this stream
     * after dropping a subset of elements that match the given predicate.
     *
     * @param predicate - a non-interfering, stateless predicate to apply to
     * elements to determine the longest prefix of elements.
     *
     * @return new new stream
     */
    public LongStream dropWhile(LongPredicate predicate) {
        requireNonNull(predicate);
        if (predicate.test(element)) {
            return empty();
        } else {
            return this;
        }
    }
}
