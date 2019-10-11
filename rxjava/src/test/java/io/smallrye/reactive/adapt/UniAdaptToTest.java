package io.smallrye.reactive.adapt;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

import io.reactivex.*;
import io.reactivex.subscribers.TestSubscriber;
import io.smallrye.reactive.Uni;
import io.smallrye.reactive.adapt.converters.*;

public class UniAdaptToTest {

    @Test
    public void testCreatingACompletable() {
        Completable completable = Uni.createFrom().item(1).adapt().with(new ToCompletable<>());
        assertThat(completable).isNotNull();
        completable.test().assertComplete();
    }

    @Test
    public void testThatSubscriptionOnCompletableProducesTheValue() {
        AtomicBoolean called = new AtomicBoolean();
        Completable completable = Uni.createFrom().deferred(() -> {
            called.set(true);
            return Uni.createFrom().item(2);
        }).adapt().with(new ToCompletable<>());

        assertThat(completable).isNotNull();
        assertThat(called).isFalse();
        completable.test().assertComplete();
        assertThat(called).isTrue();
    }

    @Test
    public void testCreatingACompletableFromVoid() {
        Completable completable = Uni.createFrom().item(null).adapt().with(new ToCompletable<>());
        assertThat(completable).isNotNull();
        completable.test().assertComplete();
    }

    @Test
    public void testCreatingACompletableWithFailure() {
        Completable completable = Uni.createFrom().failure(new IOException("boom")).adapt().with(new ToCompletable<>());
        assertThat(completable).isNotNull();
        completable.test().assertError(e -> {
            assertThat(e).hasMessage("boom").isInstanceOf(IOException.class);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingle() {
        Single<Optional<Integer>> single = Uni.createFrom().item(1).adapt().with(new ToSingle<>());
        assertThat(single).isNotNull();
        single.test()
                .assertValue(Optional.of(1))
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleByConverter() {
        Single<Optional<Integer>> single = Uni.createFrom().item(1).adapt().with(new ToSingle<>());
        assertThat(single).isNotNull();
        single.test()
                .assertValue(Optional.of(1))
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleWithDefaultFromNull() {
        Single<Integer> single = Uni.createFrom().item((Integer) null).adapt().with(new ToSingleWithDefault<>(22));
        assertThat(single).isNotNull();
        single.test()
                .assertValue(22)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleWithDefaultWithValue() {
        Single<Integer> single = Uni.createFrom().item(5).adapt().with(new ToSingleWithDefault<>(22));
        assertThat(single).isNotNull();
        single.test()
                .assertValue(5)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleWithDefaultFromNullAlternative() {
        Single<Integer> single = Uni.createFrom().item((Integer) null).adapt().with(ToSingle.withDefault(22));
        assertThat(single).isNotNull();
        single.test()
                .assertValue(22)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleWithDefaultFromNullByConverter() {
        Single<Integer> single = Uni.createFrom().item((Integer) null).adapt().with(new ToSingleWithDefault<>(22));
        assertThat(single).isNotNull();
        single.test()
                .assertValue(22)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFailureToCreateSingleFromNull() {
        Single<Integer> single = Uni.createFrom().item((Integer) null).adapt().with(ToSingle.failOnNull());
        assertThat(single).isNotNull();
        single.test().assertError(e -> {
            assertThat(e).isInstanceOf(NoSuchElementException.class);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleFromNull() {
        Single<Optional<Integer>> single = Uni.createFrom().item((Integer) null).adapt().with(new ToSingle<>());
        assertThat(single).isNotNull();
        single
                .test()
                .assertValue(Optional.empty())
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingASingleWithFailure() {
        Single<Optional<Integer>> single = Uni.createFrom().<Integer> failure(new IOException("boom")).adapt()
                .with(new ToSingle<>());
        assertThat(single).isNotNull();
        single.test().assertError(e -> {
            assertThat(e).hasMessage("boom").isInstanceOf(IOException.class);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAMaybe() {
        Maybe<Integer> maybe = Uni.createFrom().item(1).adapt().with(new ToMaybe<>());
        assertThat(maybe).isNotNull();
        maybe.test()
                .assertValue(1)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAMaybeFromNull() {
        Maybe<Integer> maybe = Uni.createFrom().item((Integer) null).adapt().with(new ToMaybe<>());
        assertThat(maybe).isNotNull();
        maybe
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAMaybeWithFailure() {
        Maybe<Integer> maybe = Uni.createFrom().<Integer> failure(new IOException("boom")).adapt().with(new ToMaybe<>());
        assertThat(maybe).isNotNull();
        maybe.test().assertError(e -> {
            assertThat(e).hasMessage("boom").isInstanceOf(IOException.class);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAnObservable() {
        Observable<Integer> observable = Uni.createFrom().item(1).adapt().with(new ToObservable<>());
        assertThat(observable).isNotNull();
        observable.test()
                .assertValue(1)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAnObservableFromNull() {
        Observable<Integer> observable = Uni.createFrom().item((Integer) null).adapt().with(new ToObservable<>());
        assertThat(observable).isNotNull();
        observable
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAnObservableWithFailure() {
        Observable<Integer> observable = Uni.createFrom().<Integer> failure(new IOException("boom")).adapt()
                .with(new ToObservable<>());
        assertThat(observable).isNotNull();
        observable.test().assertError(e -> {
            assertThat(e).hasMessage("boom").isInstanceOf(IOException.class);
            return true;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAFlowable() {
        Flowable<Integer> flowable = Uni.createFrom().item(1).adapt().with(new ToFlowable<>());
        assertThat(flowable).isNotNull();
        flowable.test()
                .assertValue(1)
                .assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAFlowableWithRequest() {
        AtomicBoolean called = new AtomicBoolean();
        Flowable<Integer> flowable = Uni.createFrom().deferred(() -> {
            called.set(true);
            return Uni.createFrom().item(1);
        }).adapt().with(new ToFlowable<>());
        assertThat(flowable).isNotNull();
        TestSubscriber<Integer> test = flowable.test(0);
        assertThat(called).isFalse();
        test.assertNoValues().assertSubscribed();
        test.request(2);
        test.assertValue(1).assertComplete();
        assertThat(called).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAFlowableFromNull() {
        Flowable<Integer> flowable = Uni.createFrom().item((Integer) null).adapt().with(new ToFlowable<>());
        assertThat(flowable).isNotNull();
        flowable
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreatingAFlowableWithFailure() {
        Flowable<Integer> flowable = Uni.createFrom().<Integer> failure(new IOException("boom")).adapt()
                .with(new ToFlowable<>());
        assertThat(flowable).isNotNull();
        flowable.test().assertError(e -> {
            assertThat(e).hasMessage("boom").isInstanceOf(IOException.class);
            return true;
        });
    }
}
