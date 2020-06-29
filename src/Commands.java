import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class Commands implements Serializable { //Строковые команды->объекты, хранящие имя команды и ее аргумент
    private String name;
    private ArrayList<Commands> masOfCommands = new ArrayList<>();
    private String fileName;
    private Integer id;
    private MusicBand band;
    private MusicGenre genre;
    private String bandName;
    private String result;
    private LocalDate creationDate;
    private byte code;
    private String login;
    private byte[] password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public void setGenre(MusicGenre genre) {
        this.genre = genre;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public MusicBand getBand() {
        return band;
    }

    public void setBand(MusicBand band) {
        this.band = band;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getBandName() {
        return bandName;
    }

    public void setBandName(String bandName) {
        this.bandName = bandName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public ArrayList<Commands> getMasOfCommands() {
        return masOfCommands;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public void setMasOfCommands(ArrayList<Commands> masOfCommands) {
        this.masOfCommands = masOfCommands;
    }

    @Override
    public String toString() {
        return "Commands{" +
                "name='" + name + '\'' +
                ", masOfCommands=" + masOfCommands +
                ", fileName='" + fileName + '\'' +
                ", id=" + id +
                ", band=" + band +
                ", genre=" + genre +
                ", bandName='" + bandName + '\'' +
                ", result='" + result + '\'' +
                ", creationDate=" + creationDate +
                ", code=" + code +
                ", login='" + login + '\'' +
                ", password=" + Arrays.toString(password) +
                '}';
    }
}