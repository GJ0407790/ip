package duke.command;

import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.Ui;
import duke.task.Task;

class DeleteCommand extends Command{

    DeleteCommand(String index) {
        super(null, index, null,null, false);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        Task t = tasks.delete(description);
        ui.showDelete(t.toString(), tasks.getSize());
        storage.save(tasks.listOutTaskInString());
    }
}