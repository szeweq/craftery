package szeweq.craftery.util;

public record IntPair(int first, int second) {

    @Override
    public String toString() {
        return "(" + first + ", " + second + ')';
    }

    // Methods for use in Kotlin

    public int component1() { return first; }
    public int component2() { return second; }
}
