package szeweq.craftery.util;

import androidx.compose.runtime.MutableState;
import org.jetbrains.annotations.Nullable;

import static androidx.compose.runtime.SnapshotStateKt.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class Selection<T> {
    private final MutableState stateSelected = mutableStateOf(null, referentialEqualityPolicy());

    public T getSelected() {
        return (T) stateSelected.getValue();
    }

    public void setSelected(@Nullable T value) {
        stateSelected.setValue(value);
    }
}
