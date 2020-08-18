package io.smallrye.mutiny.operators;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.testng.annotations.Test;

import io.reactivex.Flowable;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.test.MultiAssertSubscriber;

public class MultiOnCompletionTest {

    @Test
    public void testOnCompletionContinueWith() {
        AtomicBoolean called = new AtomicBoolean();
        Multi.createFrom().range(1, 5)
                .onCompletion().invoke(() -> called.set(true))
                .onCompletion().continueWith(6, 7, 8)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 6, 7, 8);

        assertThat(called).isTrue();
    }

    @Test
    public void testOnCompletionContinueWithAndUpstreamFailure() {
        AtomicBoolean called = new AtomicBoolean();
        Multi.createFrom().emitter(e -> e.emit(1).emit(2).fail(new IOException("boom")))
                .onCompletion().invoke(() -> called.set(true))
                .onCompletion().continueWith(6, 7, 8)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertHasFailedWith(IOException.class, "boom")
                .assertReceived(1, 2);

        assertThat(called).isFalse();
    }

    @Test
    public void testOnCompletionContinueWithEmpty() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith()
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4);
    }

    @Test
    public void testOnCompletionContinueWithOne() {
        AtomicBoolean called = new AtomicBoolean();
        Multi.createFrom().range(1, 5)
                .onCompletion().invoke(() -> called.set(true))
                .onCompletion().continueWith(25)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 25);

        assertThat(called).isTrue();
    }

    @Test
    public void testOnCompletionContinueWithIterable() {
        AtomicBoolean called = new AtomicBoolean();
        Multi.createFrom().range(1, 5)
                .onCompletion().invoke(() -> called.set(true))
                .onCompletion().continueWith(Arrays.asList(5, 6))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 5, 6);

        assertThat(called).isTrue();
    }

    @Test
    public void testOnCompletionContinueWithEmptyIterable() {
        AtomicBoolean called = new AtomicBoolean();
        Multi.createFrom().range(1, 5)
                .onCompletion().invoke(() -> called.set(true))
                .onCompletion().continueWith(Collections.emptyList())
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4);

        assertThat(called).isTrue();
    }

    @Test
    public void testOnCompletionWithInvokeThrowingException() {
        AtomicBoolean called = new AtomicBoolean();
        Multi.createFrom().range(1, 5)
                .onCompletion().invoke(() -> {
                    called.set(true);
                    throw new RuntimeException("bam");
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertHasFailedWith(RuntimeException.class, "bam");

        assertThat(called).isTrue();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionContinueWithNullItem() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith((Integer) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionContinueWithNullAsIterable() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith((Iterable<Integer>) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionContinueWithItemsContainingNullItem() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith(1, 2, 3, null, 4, 5);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionContinueWithIterableContainingNullItem() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith(Arrays.asList(1, 2, 3, null, 4, 5));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionContinueWithNullSupplier() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith((Supplier<? extends Iterable<? extends Integer>>) null);
    }

    @Test
    public void testOnCompletionContinueWithSupplier() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith(() -> Arrays.asList(25, 26))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(20))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 25, 26);
    }

    @Test
    public void testOnCompletionContinueWithSupplierReturningEmpty() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith((Supplier<Iterable<? extends Integer>>) Collections::emptyList)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(20))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4);
    }

    @Test
    public void testOnCompletionContinueWithSupplierContainingNullItem() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith(() -> Arrays.asList(25, null, 26))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(20))
                .assertHasFailedWith(NullPointerException.class, null)
                .assertReceived(1, 2, 3, 4, 25);
    }

    @Test
    public void testOnCompletionContinueWithSupplierReturningNull() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith(() -> (Iterable<Integer>) null)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(20))
                .assertHasFailedWith(NullPointerException.class, null)
                .assertReceived(1, 2, 3, 4);
    }

    @Test
    public void testOnCompletionContinueWithSupplierThrowingException() {
        Multi.createFrom().range(1, 5)
                .onCompletion().continueWith((Supplier<? extends Iterable<? extends Integer>>) () -> {
                    throw new IllegalStateException("BOOM!");
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(20))
                .assertHasFailedWith(IllegalStateException.class, "BOOM!")
                .assertReceived(1, 2, 3, 4);
    }

    @Test
    public void testOnCompletionFail() {
        Multi.createFrom().range(1, 5)
                .onCompletion().fail()
                .subscribe().withSubscriber(MultiAssertSubscriber.create(Long.MAX_VALUE))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(NoSuchElementException.class, null);
    }

    @Test
    public void testOnCompletionFailWithException() {
        Multi.createFrom().range(1, 5)
                .onCompletion().failWith(new IOException("boom"))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(Long.MAX_VALUE))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(IOException.class, "boom");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionFailWithNullException() {
        Multi.createFrom().range(1, 5)
                .onCompletion().failWith((Throwable) null);

    }

    @Test
    public void testOnCompletionFailWithSupplier() {
        Multi.createFrom().range(1, 5)
                .onCompletion().failWith(() -> new IOException("boom"))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(Long.MAX_VALUE))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(IOException.class, "boom");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testOnCompletionFailWithNullSupplier() {
        Multi.createFrom().range(1, 5)
                .onCompletion().failWith((Supplier<Throwable>) null);
    }

    @Test
    public void testOnCompletionFailWithSupplierThrowingException() {
        Multi.createFrom().range(1, 5)
                .onCompletion().failWith(() -> {
                    throw new IllegalStateException("BOOM!");
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(Long.MAX_VALUE))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(IllegalStateException.class, "BOOM!");
    }

    @Test
    public void testOnCompletionFailWithSupplierReturningNull() {
        Multi.createFrom().range(1, 5)
                .onCompletion().failWith(() -> null)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(Long.MAX_VALUE))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(NullPointerException.class, null);
    }

    @Test
    public void testSwitchTo() {
        Multi.createFrom().range(1, 5)
                .onCompletion().switchTo(Flowable.just(20))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(10))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 20);
    }

    @Test
    public void testSwitchToSupplier() {
        Multi.createFrom().range(1, 5)
                .onCompletion().switchTo(() -> Multi.createFrom().range(5, 8))
                .subscribe().withSubscriber(MultiAssertSubscriber.create(10))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 5, 6, 7);
    }

    @Test
    public void testSwitchToSupplierReturningNull() {
        Multi.createFrom().range(1, 5)
                .onCompletion().switchTo(() -> null)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(10))
                .assertHasFailedWith(NullPointerException.class, null)
                .assertReceived(1, 2, 3, 4);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSwitchToWithConsumerBeingNull() {
        Multi.createFrom().range(1, 5)
                .onCompletion().switchToEmitter(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSwitchToWithSupplierBeingNull() {
        Multi.createFrom().range(1, 5)
                .onCompletion().switchTo((Supplier<Publisher<? extends Integer>>) null);
    }

    @Test
    public void testSwitchToWithConsumer() {
        Multi.createFrom().range(1, 5)
                .onCompletion().switchToEmitter(e -> e.emit(5).emit(6).complete())
                .subscribe().withSubscriber(MultiAssertSubscriber.create(10))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4, 5, 6);
    }

    @Test
    public void testInvokeUni() {
        AtomicBoolean called = new AtomicBoolean();

        Multi.createFrom().range(1, 5)
                .onCompletion().invokeUni(() -> {
                    called.set(true);
                    return Uni.createFrom().item(69);
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertCompletedSuccessfully()
                .assertReceived(1, 2, 3, 4);

        assertThat(called).isTrue();
    }

    @Test
    public void testInvokeUniThatHasFailed() {
        AtomicBoolean called = new AtomicBoolean();

        Multi.createFrom().range(1, 5)
                .onCompletion().invokeUni(() -> {
                    called.set(true);
                    return Uni.createFrom().failure(new RuntimeException("bam"));
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(RuntimeException.class, "bam");

        assertThat(called).isTrue();
    }

    @Test
    public void testInvokeUniThatThrowsException() {
        AtomicBoolean called = new AtomicBoolean();

        Multi.createFrom().range(1, 5)
                .onCompletion().invokeUni(() -> {
                    called.set(true);
                    throw new RuntimeException("bam");
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7))
                .assertReceived(1, 2, 3, 4)
                .assertHasFailedWith(RuntimeException.class, "bam");

        assertThat(called).isTrue();
    }

    @Test
    public void testInvokeUniCancellation() {
        AtomicBoolean called = new AtomicBoolean();
        AtomicBoolean uniCancelled = new AtomicBoolean();
        AtomicInteger counter = new AtomicInteger();

        MultiAssertSubscriber<Integer> ts = Multi.createFrom().range(1, 5)
                .onCompletion().invokeUni(() -> {
                    called.set(true);
                    counter.incrementAndGet();
                    return Uni.createFrom().emitter(e -> {
                        // do nothing
                    })
                            .onCancellation().invoke(() -> {
                                counter.incrementAndGet();
                                uniCancelled.set(true);
                            });
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(7));

        ts.assertReceived(1, 2, 3, 4);
        ts.assertHasNotCompleted();
        assertThat(called.get()).isTrue();
        assertThat(uniCancelled.get()).isFalse();
        assertThat(counter.get()).isEqualTo(1);

        ts.cancel();
        ts.assertHasNotCompleted();
        assertThat(uniCancelled.get()).isTrue();
        assertThat(counter.get()).isEqualTo(2);

        ts.cancel();
        assertThat(counter.get()).isEqualTo(2);
    }

    @Test(invocationCount = 100)
    public void rogueEmittersInvoke() {
        AtomicInteger counter = new AtomicInteger();

        MultiAssertSubscriber<Object> ts = Multi.createFrom()
                .emitter(e -> {
                    Thread t1 = new Thread(e::complete);
                    Thread t2 = new Thread(e::complete);
                    t1.start();
                    t2.start();
                    try {
                        t1.join();
                        t2.join();
                    } catch (InterruptedException interruptedException) {
                        throw new RuntimeException(interruptedException);
                    }
                })
                .onCompletion().invoke(counter::incrementAndGet)
                .subscribe().withSubscriber(MultiAssertSubscriber.create(10));

        ts.assertCompletedSuccessfully();
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test(invocationCount = 100)
    public void rogueEmittersInvokeUni() {
        AtomicInteger counter = new AtomicInteger();

        MultiAssertSubscriber<Object> ts = Multi.createFrom()
                .emitter(e -> {
                    Thread t1 = new Thread(e::complete);
                    Thread t2 = new Thread(e::complete);
                    t1.start();
                    t2.start();
                    try {
                        t1.join();
                        t2.join();
                    } catch (InterruptedException interruptedException) {
                        throw new RuntimeException(interruptedException);
                    }
                })
                .onCompletion().invokeUni(() -> {
                    counter.incrementAndGet();
                    return Uni.createFrom().item(69);
                })
                .subscribe().withSubscriber(MultiAssertSubscriber.create(10));

        ts.assertCompletedSuccessfully();
        assertThat(counter.get()).isEqualTo(1);
    }
}