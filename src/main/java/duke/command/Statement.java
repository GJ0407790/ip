package duke.command;

import duke.exception.DukeException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class Statement {
    private final String statement;
    private final Hashtable<String, Integer> argsTable;

    public Statement(String statement) {
        this.statement = statement;
        argsTable = new Hashtable<>();

        argsTable.put("list", 0);
        argsTable.put("bye", 0);
        argsTable.put("done", 1);
        argsTable.put("todo", 1);
        argsTable.put("delete", 1);
        argsTable.put("deadline", 2);
        argsTable.put("event", 2);
    }

    //return a list of String with the first element as the command
    //the second element is description and third element is time (if applicable)
    private List<String> parseStatement() throws DukeException {
        Scanner sc = new Scanner(statement);
        String command = sc.next();

        List<String> result = new ArrayList<>();
        result.add(command);

        if (!argsTable.containsKey(command))  //unknown command
            throw new DukeException("Sorry, but I don't know what " + command + " means. :(");

        int numOfArgs = argsTable.get(command);

        //there are no more arguments
        if(numOfArgs == 0) return result;

        if(!sc.hasNext()){
            switch(command){
                case "done": case "delete":
                    throw new DukeException("OOPS! " + command + " requires the index of the task.");
                case "todo":
                    throw new DukeException("OOPS! " + command + " requires a description.");
                default:
                    throw new DukeException("OOPS! " + command + " requires a description and a time.");
            }
        }

        String rest = sc.nextLine();
        String[] args = rest.split("[/]");

        //missing arguments
        if (args.length != numOfArgs){
            throw new DukeException("OOPS! " + command + " requires a description and a time");
        }

        for(int i = 0; i < numOfArgs; i++){
            result.add(args[i]);
        }

        return result;
    }

    public Command parse() throws DukeException{
        try {
            List<String> parsedArgs = parseStatement();
            String command = parsedArgs.get(0);
            String first = null;
            String second = null;
            String preposition = null;
            LocalDate date = null;

            if(parsedArgs.size() == 2) {
                first = parsedArgs.get(1).trim();
            }

            if(parsedArgs.size() == 3) {
                second = parsedArgs.get(2).trim();
                String[] prepositionAndDate = second.split("[\\s]");

                if(prepositionAndDate.length != 2) {
                    throw new DukeException("Please provide a preposition and a date after '/'.");
                }

                preposition = prepositionAndDate[0];
                date = LocalDate.parse(prepositionAndDate[1]);
            }

            switch (command) {
                case "list":
                    return new ListCommand();
                case "bye":
                    return new ExitCommand();
                case "done":
                    return new DoneCommand(first);
                case "delete":
                    return new DeleteCommand(first);
                case "todo": case "deadline": case "event":
                    return new AddCommand(command, first, preposition, date);
                default:
                    return null;
            }
        } catch(DateTimeParseException e) {
            throw new DukeException("Date must be in the format yyyy-mm-dd.");
        }

    }
}