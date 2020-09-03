package szewek.mctool.app.task;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
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

        var openProp = TaskManager.INSTANCE.getTaskListOpen();
        tlp.managedProperty().bind(openProp);
        tlp.visibleProperty().bind(openProp);

        FXKt.bindChildren(tlp, TaskManager.INSTANCE.getTasks(), task -> {
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
        sb.getStyleClass().add("task-list");
        sb.setAlignment(Pos.CENTER_LEFT);

        var l = new Label();
        l.setPadding(new Insets(4));
        l.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(l, Priority.ALWAYS);

        var lt = TaskManager.INSTANCE.getLastTask();
        var pb = new ProgressBar();
        pb.setPadding(new Insets(4));
        var tnn = Bindings.isNotNull(lt);
        pb.visibleProperty().bind(tnn);
        pb.managedProperty().bind(tnn);

        var tb = new ToggleButton("Tasks");
        tb.setFocusTraversable(false);
        TaskManager.INSTANCE.getTaskListOpen().bind(tb.selectedProperty());

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
}
