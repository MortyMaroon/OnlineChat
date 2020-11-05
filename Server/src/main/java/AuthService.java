import java.sql.*;

class AuthService {
    private static Connection connection;
    private static Statement statement;
    private static final String databaseName = "Chat.db";
    private static final String url = "jdbc:sqlite:Server/" + databaseName;

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

    public static String checkNickName(String nickName) {
        String sql = String.format("SELECT username_fld FROM users_tbl WHERE username_fld = '%s'", nickName);
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

    public static String checkLogin(String login) {
        String sql = String.format("SELECT username_fld FROM users_tbl WHERE login_fld = '%s'", login);
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

    public static String tryRegister(String firstName, String lastName, String nickName, String password, String login) {
        String sql = String.format("INSERT INTO users_tbl(firstname_fld,lastname_fld,username_fld,password_fld,login_fld) VALUES('%s','%s','%s','%s','%s')", firstName, lastName, nickName, password, login);
        try {
            statement.executeQuery(sql);
            return nickName;
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