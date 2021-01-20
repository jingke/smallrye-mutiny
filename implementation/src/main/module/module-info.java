module io.smallrye.mutiny {
    requires org.reactivestreams;

    exports io.smallrye.mutiny;

    exports io.smallrye.mutiny.converters;
    exports io.smallrye.mutiny.converters.multi;
    exports io.smallrye.mutiny.converters.uni;

    exports io.smallrye.mutiny.groups;

    exports io.smallrye.mutiny.helpers;
    exports io.smallrye.mutiny.helpers.queues;
    exports io.smallrye.mutiny.helpers.spies;
    exports io.smallrye.mutiny.helpers.test;

    exports io.smallrye.mutiny.infrastructure;

    exports io.smallrye.mutiny.operators;
    exports io.smallrye.mutiny.operators.multi;
    exports io.smallrye.mutiny.operators.multi.builders;
    exports io.smallrye.mutiny.operators.multi.multicast;
    exports io.smallrye.mutiny.operators.multi.overflow;
    exports io.smallrye.mutiny.operators.multi.processors;
    exports io.smallrye.mutiny.operators.uni.builders;

    exports io.smallrye.mutiny.subscription;
    exports io.smallrye.mutiny.tuples;
    exports io.smallrye.mutiny.unchecked;

    uses io.smallrye.mutiny.infrastructure.MultiInterceptor;
    uses io.smallrye.mutiny.infrastructure.ExecutorConfiguration;
    uses io.smallrye.mutiny.infrastructure.UniInterceptor;
    uses io.smallrye.mutiny.infrastructure.CallbackDecorator;
}