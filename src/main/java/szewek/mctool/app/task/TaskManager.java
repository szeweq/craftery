package szewek.mctool.app.task;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;

public class TaskManager {
    private TaskManager() {}

    public static final BooleanProperty taskListOpen = new SimpleBooleanProperty(false);
    public static final ObservableList<Task<?>> tasks = FXCollections.observableArrayList();
    public static final ObjectBinding<Task<?>> lastTask = Bindings.createObjectBinding(TaskManager::findTask, tasks);

    public static void addTask(final Task<?> t) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> addTask(t));
        }
        System.out.println("ADDING TASK " + t);
        tasks.add(t);
        removeOnFinish(t);
    }

    private static void removeOnFinish(final Task<?> t) {
        var csl = new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State os, Worker.State ns) {
                if (ns == Worker.State.SUCCEEDED || ns == Worker.State.CANCELLED || ns == Worker.State.FAILED) {
                    tasks.remove(t);
                    ov.removeListener(this);
                }
            }
        };
        t.stateProperty().addListener(csl);
    }

    private static Task<?> findTask() {
        var iter = tasks.listIterator(tasks.size());
        while (iter.hasPrevious()) {
            var t = iter.previous();
            if (!t.isDone()) {
                return t;
            }
        }
        return null;
    }
}
