import java.sql.*;

public class TranslaterSQL {
    static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/postgres";
    static final String DB_USER = "postgres";
    static final String DB_PASSWORD = "12345";


    public static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName("org.postgresql.Driver");
            dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
            return dbConnection;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    public static void insertBand(MusicBand band) {
        String sql = "INSERT INTO musicbands (name, coordinateX, coordinateY, date, numberOfParticipants, singlesCount, albumsCount, genre, bestAlbum, owner) VALUES ('" + band.getName() + "','" + band.getCoordinates().getX() + "','" + band.getCoordinates().getY() + "','" + band.getCreationDate() + "','" + band.getNumberOfParticipants() + "','" + band.getSinglesCount() + "','" + band.getAlbumsCount() + "','" + band.getGenre().name() + "','" + band.getBestAlbum().getName() + "'," + band.getOwner() + ")";
        try (Connection dbConnection = getDBConnection(); Statement statement = dbConnection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertOwner(String owner, String pass) {
        String sql = "INSERT INTO owners (login, password) VALUES (" + owner + ",'" + pass + "')";
        try (Connection dbConnection = getDBConnection(); Statement statement = dbConnection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void delete(MusicBand band) {
        String sql = "DELETE FROM musicbands WHERE id = " + band.getId() + ";";
        try (Connection dbConnection = getDBConnection(); Statement statement = dbConnection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void creatingSet() throws SQLException {
        ResultSet rs = creatingSQLSet();
        while (rs.next()) {
            CommandExecution.set.add(creatingJavaObject(rs));
        }
    }

    public static void creatingOwners() throws SQLException {
        try (Connection dbConnection = getDBConnection()) {
            PreparedStatement pst = dbConnection.prepareStatement("SELECT * FROM owners");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                CommandExecution.owners.put(rs.getString(1), rs.getString(2));
            }
        }
    }

    public static MusicBand creatingJavaObject(ResultSet rs) throws SQLException {
        MusicBand band = new MusicBand();
        band.setId(rs.getInt(1));
        band.setName(rs.getString(2));
        Coordinates coordinates = new Coordinates();
        coordinates.setX(rs.getDouble(3));
        coordinates.setY(rs.getLong(4));
        band.setCoordinates(coordinates);
        band.setCreationDate(rs.getDate(5).toLocalDate());
        band.setNumberOfParticipants(rs.getLong(6));
        band.setSinglesCount(rs.getLong(7));
        band.setAlbumsCount(rs.getLong(8));
        band.setGenre(MusicGenre.valueOf(rs.getString(9)));
        Album album = new Album();
        album.setName(rs.getString(10));
        band.setBestAlbum(album);
        band.setOwner(rs.getString(11));
        return band;
    }

    public static ResultSet creatingSQLSet() {
        ResultSet rs = null;
        try (Connection dbConnection = getDBConnection()) {
            PreparedStatement pst = dbConnection.prepareStatement("SELECT * FROM musicbands");
            rs = pst.executeQuery();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rs;
    }

    public static MusicBand getLastAdded() throws SQLException {
        MusicBand band=null;
        ResultSet rs = creatingSQLSet();
        while (rs.next()){
            band=creatingJavaObject(rs);
        }
        return band;
    }
}