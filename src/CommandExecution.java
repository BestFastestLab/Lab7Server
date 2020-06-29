import java.io.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class CommandExecution {
    private static LocalDate date;
    static List<File> tempList = new LinkedList<>();
    static LinkedHashSet<MusicBand> set1 = new LinkedHashSet<>();//сама коллекция
    static Set<MusicBand> set = Collections.synchronizedSet(set1);
    static String[] history = new String[6];//массив для хранения истории команд
    static LinkedHashMap<String, String> owners1 = new LinkedHashMap<>();
    static Map<String,String> owners = Collections.synchronizedMap(owners1);


    public CommandExecution() throws Exception {
        TranslaterSQL.creatingSet();
        TranslaterSQL.creatingOwners();
        System.out.println(owners);
        date = LocalDate.now();
        tempList.clear();

    }

    public static Commands help() {
        Commands command = new Commands();
        command.setResult("help : вывести справку по доступным командам\n" +
                "info : вывести в стандартный поток вывода информацию о коллекции (тип, количество элементов и т.д.)\n" +
                "show : вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                "add name : добавить новый элемент в коллекцию c заданным названием\n" +
                "update id : обновить значение элемента коллекции, id которого равен заданному\n" +
                "remove_by_id id : удалить элемент из коллекции по его id\n" +
                "clear : очистить коллекцию\n" +
                "execute_script file_name : считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.\n" +
                "exit : завершить программу (без сохранения в файл)\n" +
                "add_if_max name : добавить новый элемент в коллекцию с заданным именем, если его значение превышает значение наибольшего элемента этой коллекции\n" +
                "remove_greater name : удалить из коллекции все элементы, превышающие заданный. Ввод начинается с имени группы\n" +
                "history : вывести последние 6 команд (без их аргументов)\n" +
                "min_by_albums_count : вывести любой объект из коллекции, значение поля albumsCount которого является минимальным\n" +
                "count_greater_than_genre genre : вывести количество элементов, значение поля genre которых больше заданного. Жанры:RAP, POST_ROCK, BRIT_POP\n" +
                "print_ascending : вывести элементы коллекции в порядке возрастания\n");
        Operations.historyChange("help");
        System.out.println(command.getMasOfCommands().size());
        return command;
    }

    public static Commands info() {
        Commands command = new Commands();
        command.setResult("Тип коллекции: " + set.getClass() + "\n" +
                "Размер коллекции: " + set.size() + "\n" +
                "Дата инициализации коллекции: " + date.toString());
        Operations.historyChange("info");
        return command;
    }

    public static Commands show() {
        Commands command = new Commands();
        synchronized(set) {
            if (set.size() != 0) {
                command.setResult("");
                set.forEach(s -> command.setResult(command.getResult() + s.toString() + "\n"));
                if (command.getCode() != 5) {
                    Operations.historyChange("show");
                }
            } else {
                command.setResult("А элементов-то в коллекции тютю(");
            }
        }
        return command;
    }

    public static void add(Commands command1) throws SQLException {
        if (command1.getResult() == null || command1.getResult().equals("Waiting for name")) {
            command1.setResult("It's creation time!");
            System.out.println(command1.getResult());
            command1.setName("add");
        } else {
            if (command1.getResult().equals("It's creation time!")) {
                Operations.creatingNewBandStep2(command1);
                Operations.sorted = false;
                TranslaterSQL.insertBand(command1.getBand());
                set.add(TranslaterSQL.getLastAdded());
                command1.setResult("Объект с названием " + command1.getBandName() + " успешно добавлен!");
                if (command1.getCode() == 0) {
                    Operations.historyChange("add");
                }
            }
        }
    }

    public static void updateId(Commands command) {
        if (Operations.existence(command.getId()) != null) {
            synchronized (set) {
                if (Operations.existence(command.getId()).getOwner().equals(command.getLogin())) {
                    set.stream()
                            .filter(s -> s.getId().equals(command.getId()))
                            .limit(1)
                            .forEach((s) ->
                            {
                                TranslaterSQL.delete(s);
                                set.remove(s);
                            });
                    command.setResult("Waiting for name");
                } else {
                    command.setResult("Это не ваш объект, вы не можетеизменять его");
                }
            }
        } else {
            command.setResult("Объекта с заданным id не существует(");
        }
        Operations.historyChange("update");
    }

    public static void removeById(Commands command) {
        synchronized (set) {
            Optional<MusicBand> target = set.stream()
                    .filter(s -> s.getId().equals(command.getId()))
                    .findFirst();
            if (target.isPresent()) {
                if (!target.get().getOwner().equals(command.getLogin())) {
                    command.setResult("Это не ваш объект! Вы не можете его изменять");
                } else {
                    TranslaterSQL.delete(target.get());
                    set.remove(target.get());
                    command.setResult("Элемент id=" + command.getId() + " успешно удален!");
                }
            } else {
                command.setResult("Данного элемента не было изначально");
            }
        }
        Operations.historyChange("remove_by_id");
    }

    public static void addIfMax(Commands command1) {
        if (command1.getResult() == null) {
            command1.setName("add_if_max");
            command1.setResult("It's creation time!");
        } else if (command1.getResult().equals("It's creation time!")) {
            MusicBand t = Operations.getMax();
            MusicBand p = Operations.creatingNewBandStep2(command1).getBand();
            if (set.size() > 0 && p.compareTo(t) < 0) {
                command1.setResult("Увы, введенный вами элемент не будет самым большим в коллекции");
            } else {
                set.add(p);
                command1.setResult("Элемент с названием " + command1.getBandName() + " успешно добавлен");
            }
            Operations.historyChange("add_if_max");
        }
    }

    public static Commands removeGreater(Commands command1) {
        if (command1.getResult() == null) {
            command1.setResult("It's creation time!");
            command1.setName("remove_greater");
        } else if (command1.getResult().equals("It's creation time!")) {
            command1.setResult("Было удалено " + Operations.removingGreater(Operations.creatingNewBandStep2(command1).getBand(), command1) + " объектов по вашему запросу");
            Operations.historyChange("remove_greater");
        }
        return command1;
    }


    public static Commands clear() {
        Commands command = new Commands();
        set.clear();
        Operations.historyChange("clear");
        command.setResult("Коллекция очищена!");
        return command;
    }

    public static Commands executeScript(String file) {
        Commands command = new Commands();
        File script_file = new File(file);
            try {
                if (!script_file.exists() || !script_file.isFile()) {
                    command.setResult("Файла не существует");
                }
                if (!script_file.canRead()) {
                    command.setResult("Файл не читается");
                }
                if (script_file.length() == 0) {
                    command.setResult("Файл пуст");
                }
                Scanner scanner = new Scanner(script_file);
                while (scanner.hasNext()) {
                    try {
                        int k = 0;
                        String tempStr = scanner.nextLine();
                        String[] tempStrArray = tempStr.split("\\s");
                        String comparison = tempStrArray[0];
                        Commands objectCommand = new Commands();
                        switch (comparison) {
                            case "help":
                                objectCommand.setName("help");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "info":
                                objectCommand.setName("info");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "show":
                                objectCommand.setName("show");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "add":
                                if (tempStrArray[tempStrArray.length - 1].equals("add")) {//случай, когда нет параметра там, где он нужен
                                    objectCommand.setResult("А что добавлять?");
                                } else {
                                    objectCommand.setName(comparison);
                                    objectCommand.setBandName(tempStrArray[tempStrArray.length - 1]);
                                    objectCommand.setCode((byte) 0);
                                }
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "update":
                                if (tempStrArray[tempStrArray.length - 1].equals("update")) {
                                    objectCommand.setResult("А что обновлять?");
                                } else {
                                    try {
                                        k = Integer.parseInt(tempStrArray[tempStrArray.length - 1]);
                                    } catch (Exception e) {
                                        objectCommand.setResult("Попробуйте еще раз ввести команду, используя число в качесте id");
                                    }
                                    objectCommand.setName(comparison);
                                    objectCommand.setId(k);
                                    objectCommand.setCode((byte) 1);
                                }
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "remove_by_id":
                                if (tempStrArray[tempStrArray.length - 1].equals("remove_by_id")) {
                                    objectCommand.setResult("А что обновлять?");
                                } else {
                                    try {
                                        k = Integer.parseInt(tempStrArray[tempStrArray.length - 1]);
                                    } catch (Exception e) {
                                        objectCommand.setResult("Попробуйте еще раз ввести команду, используя число в качесте id");
                                    }
                                    objectCommand.setName("remove_by_id");
                                    objectCommand.setId(k);
                                }
                                command.getMasOfCommands().add(objectCommand);
                                break;

                            case "clear":
                                objectCommand.setName("clear");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "execute_script":
                                if (tempStrArray[tempStrArray.length - 1].equals("execute_script")) {
                                    objectCommand.setResult("А какой скрипт выполнить?");
                                } else if(!tempStrArray[tempStrArray.length - 1].equals(script_file.getAbsolutePath().replaceAll("\\\\", ""))) {
                                    objectCommand.setName("execute_script");
                                    objectCommand.setFileName(tempStrArray[tempStrArray.length - 1]);
                                } else{
                                    objectCommand.setResult("Обнаржуен цилк");
                                }
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "exit":
                                objectCommand.setResult("exit");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "add_if_max":
                                if (tempStrArray[tempStrArray.length - 1].equals("add_if_max")) {
                                    objectCommand.setResult("А что добавлять?");
                                } else {
                                    objectCommand.setName("add_if_max");
                                    objectCommand.setBandName(tempStrArray[tempStrArray.length - 1]);
                                }
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "remove_greater":
                                if (tempStrArray[tempStrArray.length - 1].equals("remove_greater")) {
                                    objectCommand.setResult("А что удалять?");
                                } else {
                                    objectCommand.setBandName(tempStrArray[tempStrArray.length - 1]);
                                    objectCommand.setName("remove_greater");
                                }
                                command.getMasOfCommands().add(removeGreater(objectCommand));
                                break;
                            case "history":
                                objectCommand.setName("history");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "min_by_albums_count":
                                objectCommand.setName("min_by_albums_count");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "count_greater_than_genre":
                                if (tempStrArray[tempStrArray.length - 1].equals("count_greater_than_genre")) {
                                    objectCommand.setResult("А с чем сравнивать?");
                                } else {
                                    if (MusicGenre.existence(tempStrArray[tempStrArray.length - 1])) {
                                        objectCommand.setName("count_greater_than_genre");
                                        objectCommand.setGenre(MusicGenre.valueOf(tempStrArray[tempStrArray.length - 1]));
                                    } else {
                                        objectCommand.setResult("Такого жанра не существует! Попробуйте еще раз ввести команду");
                                    }
                                }
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            case "print_ascending":
                                objectCommand.setName("print_ascending");
                                command.getMasOfCommands().add(objectCommand);
                                break;
                            default:
                                objectCommand.setResult("Введенная вами команда не соответстувет требованиям. Попробуйте еще раз");
                                command.getMasOfCommands().add(objectCommand);
                        }
                    } catch (Exception e) {
                        System.out.println("Что-то пошло не так :(");
                    }
                }
            } catch (Exception e) {
                command.setResult("Файл не найден!");
            }
            //command.setResult("Скрипт выполнен!");
        Operations.historyChange("execute_script");
        return command;
    }


    public static Commands history() {
        Commands command = new Commands();
        command.setResult("");
        for (String c : history) {
            if (c != null) {
                command.setResult(command.getResult() + c + "\n");
            }
        }
        Operations.historyChange("history");
        return command;
    }

    public static Commands minByAlbumsCount() {
        Commands command = new Commands();
        command.setResult(Operations.getMinAlbumCount().toString());
        Operations.historyChange("min_by_albums_count");
        return command;
    }

    public static Commands countGreaterThanGenre(MusicGenre genre) {
        Commands command = new Commands();
        Operations.historyChange("count_greater_than_genre");
        command.setResult(Long.toString(Operations.getQuantityGenres(genre.ordinal())));
        return command;
    }

    public static Commands printAscending() {
        Commands command = new Commands();
        Operations.sortSet();
        command.setCode((byte) 5);
        command = show();
        Operations.historyChange("print_ascending");
        return command;
    }

    public

    static String checkLogin(Commands command) {
        String result = "Выбранный логин свободен, можно продолжать регистрацию";
        for (String login : owners.keySet()) {
            if (login.equals(command.getLogin())) {
                result = "Пользователь с таким логином уже существует";
                break;
            }
        }
        return result;
    }

    public static String checkUser(Commands command) {
        String result = "Неверное имя пользователя или пароль. Попробуйте еще раз";
        System.out.println(command.getLogin()+" "+Operations.removeChar(Arrays.toString(command.getPassword()), ','));
        if ((Operations.removeChar(Arrays.toString(command.getPassword()), ',').equals(owners.get(command.getLogin())))) {
            result = "Авторизация прошла успешно!";
        }
        return result;
    }

    public static String addNewOwner(Commands command) {
        String result = "Регистрация прошла успешно!";
        TranslaterSQL.insertOwner(command.getLogin(), Operations.removeChar(Arrays.toString(command.getPassword()), ','));
        owners.put(command.getLogin(), Operations.removeChar(Arrays.toString(command.getPassword()), ','));
        return result;
    }

}