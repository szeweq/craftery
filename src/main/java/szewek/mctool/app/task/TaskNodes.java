package szewek.mctool.app.task;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import tornadofx.FXKt;

public class TaskNodes {
    private TaskNodes() {}

    private static final VBox taskListPane = new VBox(2);
    private static final HBox statusBar = new HBox();

    public static Node getTaskListPane() {
        return taskListPane;
    }

    public static Node getStatusBar() {
        return statusBar;
    }

    static {
        taskListPane();
        statusBar();
    }

    private static void taskListPane() {
        final var tlp = taskListPane;
        tlp.getStyleClass().add("task-list");
        tlp.setMinWidth(200);
        tlp.setMaxWidth(200);

        showNodeWhen(tlp, TaskManager.taskListOpen);

        FXKt.bindChildren(tlp, TaskManager.tasks, task -> {
            var l = new Label();
            l.textProperty().bind(task.messageProperty());
            var pb = new ProgressBar();
            pb.prefWidthProperty().bind(tlp.widthProperty());
            pb.progressProperty().bind(task.progressProperty());
            var vb = new VBox(l, pb);
            vb.getStyleClass().add("task");
            return vb;
        });
    }

    private static void statusBar() {
        final var sb = statusBar;
        sb.getStyleClass().add("status-bar");
        sb.setAlignment(Pos.CENTER_LEFT);

        var l = new Label();
        l.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(l, Priority.ALWAYS);

        var lt = TaskManager.lastTask;
        var pb = new ProgressBar();
        showNodeWhen(pb, Bindings.isNotNull(lt));

        var tb = new ToggleButton("Tasks");
        tb.setFocusTraversable(false);
        TaskManager.taskListOpen.bind(tb.selectedProperty());

        sb.getChildren().addAll(l, pb, tb);
        lt.addListener((observable, ov, nv) -> {
            var txt = l.textProperty();
            var prog = pb.progressProperty();
            txt.unbind();
            prog.unbind();
            if (nv != null) {
                txt.bind(nv.messageProperty());
                prog.bind(nv.progressProperty());
            } else {
                l.setText("");
            }
        });
    }

    private static void showNodeWhen(Node n, ObservableValue<Boolean> ov) {
        n.visibleProperty().bind(ov);
        n.managedProperty().bind(ov);
    }
}
