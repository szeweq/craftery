package szeweq.craftery.util;

import java.util.Objects;

@FunctionalInterface
public interface LongBiConsumer {
	void accept(long l, long r);

	default LongBiConsumer andThen(LongBiConsumer after) {
		Objects.requireNonNull(after);

		return (l, r) -> {
			accept(l, r);
			after.accept(l,r);
		};
	}

	LongBiConsumer DUMMY = (l, r) -> {};
}
