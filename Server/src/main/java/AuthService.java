import java.sql.*;

class AuthService {
    private static Connection connection;
    private static Statement statement;
    private static String databaseName = "Chat_db.db";
    private static String url = "jdbc:sqlite:" + databaseName;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String password){
        String sql = String.format("SELECT username_fld FROM users_tbl WHERE login_fld = '%s' AND password_fld = '%s'", login, password);
        try {
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                return rs.getString("username_fld");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}