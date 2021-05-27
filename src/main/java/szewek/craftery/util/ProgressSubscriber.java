package szewek.craftery.util;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow;

public class ProgressSubscriber<T> implements HttpResponse.BodySubscriber<T> {
    private final long size;
    private long count = 0;
    private final LongBiConsumer progress;
    private final HttpResponse.BodySubscriber<T> child;

    public ProgressSubscriber(LongBiConsumer progress, long size, HttpResponse.BodySubscriber<T> bodySubscriber) {
        this.progress = progress;
        this.size = size;
        child = bodySubscriber;
    }

    @Override
    public CompletionStage<T> getBody() {
        return child.getBody();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        child.onSubscribe(subscription);
    }

    @Override
    public void onNext(List<ByteBuffer> item) {
        final var s = size == -1 ? count : size;
        var c = 0;
        for (ByteBuffer byteBuffer : item) {
            c += byteBuffer.remaining();
        }
        child.onNext(item);
        count += c;
        progress.accept(count, s);
    }

    @Override
    public void onError(Throwable throwable) {
        child.onError(throwable);
    }

    @Override
    public void onComplete() {
        child.onComplete();
    }

    public static <T> HttpResponse.BodyHandler<T> handle(final HttpResponse.BodyHandler<T> bodyHandler, final LongBiConsumer progress) {
        return ri -> {
            var c = ri.headers().firstValue("Content-Length").map(Long::valueOf).orElse(-1L);
            return new ProgressSubscriber<>(progress, c, bodyHandler.apply(ri));
        };
    }
}
