package duke.storage;

import duke.exception.DukeException;
import duke.task.Deadline;
import duke.task.Event;
import duke.task.Task;
import duke.task.Todo;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import java.time.LocalDate;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * FileReader deals with reading data from the file.
 */
class FileReader {

    /**
     * Returns a list of tasks after reading the data inside the file.
     * If the file is not created yet, will create a new file and write
     * as if the list is empty.
     *
     * @param path The relative address of the data file.
     * @return A list of tasks.
     * @throws DukeException If an I/O error occurs.
     */
    List<Task> readFile(String path) throws DukeException {
        try {
            File f = new File(path);

            if (!f.exists()) {
                //create a new file and write as if the list is empty
                f.createNewFile();
                FileWriter fw = new FileWriter(path);
                fw.write("Done tasks: " + System.lineSeparator() + "Pending tasks: "
                        + System.lineSeparator());
                fw.close();

                f = new File(path);
            }

            Scanner sc = new Scanner(f);
            List<Task> tasks = new ArrayList<>();
            boolean isDone = true;

            while (sc.hasNext()) {
                String currStr = sc.nextLine();

                if (currStr.equals("Done tasks: ") || currStr.equals("Pending tasks: ")) {
                    if (currStr.equals("Pending tasks: ")) {
                        isDone = false;
                    }

                    continue;
                }

                Task t = toTask(currStr);

                if (isDone) {
                    t.markAsDone();
                }
                tasks.add(t);
            }

            return tasks;

        } catch (IOException e) {
            throw new DukeException(e.getMessage());
        }
    }

    private Task toTask(String input) {
        Scanner sc = new Scanner(input);
        String command = sc.next();

        String[] args = sc.nextLine().split("[|]");

        String first = args[0].trim();
        String second = null;
        String preposition = null;
        LocalDate date = null;

        if (args.length == 2) {
            second = args[1].trim();
            assert !second.equals("") : "Missing a date and preposition.";

            String[] prepositionAndDate = second.split("[\\s]");
            preposition = prepositionAndDate[0];
            date = LocalDate.parse(prepositionAndDate[1]);

            assert date != null : "Missing a date here.";
        }

        switch (command) {
        case "todo":
            return new Todo(first);
        case "event":
            return new Event(first, preposition, date);
        case "deadline":
            return new Deadline(first, preposition, date);
        default:
            return null;
        }
    }

}
