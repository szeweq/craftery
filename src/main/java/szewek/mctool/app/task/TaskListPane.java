package szewek.mctool.app.task;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import tornadofx.FXKt;

public class TaskListPane extends VBox {
	public TaskListPane() {
		super(2);

		getStyleClass().add("task-list");
		setMinWidth(200);
		setMaxWidth(200);

		var openProp = TaskManager.INSTANCE.getTaskListOpen();
		managedProperty().bind(openProp);
		visibleProperty().bind(openProp);

		FXKt.bindChildren(this, TaskManager.INSTANCE.getTasks(), task -> {
			var l = new Label();
			l.textProperty().bind(task.messageProperty());
			var pb = new ProgressBar();
			pb.progressProperty().bind(task.progressProperty());
			var vb = new VBox(l, pb);
			vb.getStyleClass().add("task");
			return vb;
		});
	}
}
