package szeweq.craftery.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.LongStream;

public final class ValueHistory {
    private final long[] values = new long[24];
    private int size = 0;
    private int offset = 0;

    public int size() { return size; }

    public long[] getValues() {
        final long[] v = new long[size];
        if (size == 24) {
            var st = offset + 1;
            System.arraycopy(values, st, v, 0, 24 - st);
            System.arraycopy(values, 0, v, 24 - st, st);
        } else {
            System.arraycopy(values, 0, v, 0, size);
        }
        return v;
    }

    public LongStream stream() { return Arrays.stream(values, 0, size); }

    public long last() {
        return values[size == 24 ? offset : size - 1];
    }

    public void add(long t) {
        if (size == 24) {
            var o = (offset + 1) % 24;
            values[o] = t;
            offset = o;
        } else {
            values[size++] = t;
        }
    }

    public long sum() {
        long s = 0;
        for (int i = 0; i < size; i++) {
            s += values[i];
        }
        return s;
    }

    public long avg() {
        return sum() / size;
    }

    public long max() {
        long m = 0;
        for (int i = 0; i < size; i++) {
            if (m < values[i]) m = values[i];
        }
        return m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueHistory that = (ValueHistory) o;
        return size == that.size && offset == that.offset && Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(size, offset);
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }
}
