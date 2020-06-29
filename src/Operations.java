import java.util.*;
import java.time.LocalDate;

class Operations {
    /**
     * @value sorted-Переменная, показывающая, отсортирована ли коллекция
     */
    static boolean sorted = false;

    /**
     * @return Проверка существования объекта с заданным id
     */
    public static MusicBand existence(Integer id) {//Проверка существования объекта с заданным id в коллекции. Нужен для команды uptade_id
        MusicBand band=null;
        synchronized (CommandExecution.set) {
            Optional<MusicBand> stream = CommandExecution.set.stream()
                    .filter(s -> s.getId().equals(id))
                    .findFirst();
            if (stream.isPresent()) {
                band = stream.get();
            }
        }
        return band;
    }

    /**
     * Формирование истории введенных команд
     */
    public static void historyChange(String NewCommand) {//формирование истории запросов
        System.arraycopy(CommandExecution.history, 1, CommandExecution.history, 0, 5);
        CommandExecution.history[5] = NewCommand;
    }

    /**
     * Сортировка коллекции
     */
    public static void sortSet() {//сортировка коллекции
        synchronized (CommandExecution.set) {
            if (!sorted) {
                CommandExecution.set.stream()
                        .sorted()
                        .forEach(s -> {
                            CommandExecution.set.remove(s);
                            CommandExecution.set.add(s);
                        });
                sorted = true;
            }
        }
    }

    /**
     * @return Выявление наибольшего объекта
     */
    public static MusicBand getMax() {
        Operations.sortSet();
        MusicBand[] bands = CommandExecution.set.toArray(new MusicBand[CommandExecution.set.size()]);
        return bands[bands.length - 1];
    }

    /**
     * @return Выявление объекта с наименьшим полем AlbumCount
     */
    public static MusicBand getMinAlbumCount() {
        MusicBand[] bands = CommandExecution.set.toArray(new MusicBand[CommandExecution.set.size()]);
        MusicBand p = bands[0];
        for (int i = 1; i < bands.length; i++) {
            if (bands[i].getAlbumsCount() < bands[i - 1].getAlbumsCount()) {
                p = bands[i];
            }
        }
        return p;
    }

    /**
     * @return Определение количества жанров, больших заданного с помощью кода
     */
    public static Long getQuantityGenres(int code) {
        long Quantity = 0;
        synchronized (CommandExecution.set) {
            if (code < 2) {
                Quantity = CommandExecution.set.stream()
                        .filter(s -> s.getGenre().ordinal() > code)
                        .count();
            }
        }
        return Quantity;
    }

    /**
     * @return Создание нового объекта
     */
    public static Commands creatingNewBandStep2(Commands command) {
        command.getBand().setCreationDate(LocalDate.of((int) (Math.random() * 2021), (int) (Math.random() * 12 + 1), (int) (Math.random() * 28 + 1)));
        command.getBand().setOwner(command.getLogin());
        return command;
    }

    public static int removingGreater(MusicBand targetBand, Commands command) {
        int k = CommandExecution.set.size();
        synchronized (CommandExecution.set) {
            for (MusicBand band : CommandExecution.set) {
                if (band.compareTo(targetBand) > 0 & band.getOwner().equals(command.getLogin())) {
                    TranslaterSQL.delete(band);
                    CommandExecution.set.remove(band);
                }
            }
        }
        return k - CommandExecution.set.size();
    }
    public static String removeChar(String s, char c) {
        StringBuilder r = new StringBuilder();
        s=s.replaceAll(" ", ",");
        for (int i = 0; i < s.length(); i ++) {
            if (s.charAt(i) != c) r.append(s.charAt(i));
        }
        return r.toString().substring(1, r.toString().length()-2);
    }
}