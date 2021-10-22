package szeweq.kt

fun <T> ((T) -> Unit).bind(value: T): () -> Unit = { this(value) }

infix fun (() -> Unit).and(other: () -> Unit): () -> Unit = {
    this()
    other()
}
