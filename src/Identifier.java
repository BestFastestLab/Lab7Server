import java.sql.SQLException;

public class Identifier {
    public static Commands Identify(Commands command) throws SQLException {
        switch (command.getName()) {
            case "help":
                command = CommandExecution.help();
                break;
            case "info":
                command = CommandExecution.info();
                break;
            case "show":
                command = CommandExecution.show();
                break;
            case "add":
                CommandExecution.add(command);
                break;
            case "update":
                CommandExecution.updateId(command);
                break;
            case "remove_by_id":
                CommandExecution.removeById(command);
                break;
            case "clear":
                command = CommandExecution.clear();
                break;
            case "execute_script":
                command = CommandExecution.executeScript(command.getFileName());
                break;
            case "add_if_max":
                CommandExecution.addIfMax(command);
                break;
            case "remove_greater":
                CommandExecution.removeGreater(command);
                break;
            case "history":
                command = CommandExecution.history();
                break;
            case "min_by_albums_count":
                command = CommandExecution.minByAlbumsCount();
                break;
            case "count_greater_than_genre":
                command = CommandExecution.countGreaterThanGenre(command.getGenre());
                break;
            case "print_ascending":
                command = CommandExecution.printAscending();
                break;
            case "exit":
                command.setResult("exit");
                break;
            case "checkLogin":
                command.setResult(CommandExecution.checkLogin(command));
                break;
            case "checkUser":
                command.setResult(CommandExecution.checkUser(command));
                break;
            case "addNewOwner":
                command.setResult(CommandExecution.addNewOwner(command));
                break;
        }
        return command;
    }
}
