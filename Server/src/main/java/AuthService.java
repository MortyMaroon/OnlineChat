import java.sql.*;

class AuthService {
    private static Connection connection;
    private static Statement statement;
    private static final String databaseName = "Chat.db";
    private static final String url = "jdbc:sqlite:" + databaseName;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:Server/Chat.db");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getNickByLoginAndPass(String login, String password){
        String sql = String.format("SELECT username_fld FROM users_tbl WHERE login_fld = '%s' AND password_fld = '%s'", login, password);
//        String sql = "SELECT username_fld FROM users_tbl WHERE login_fld = 'egor' AND password_fld = '123'";
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