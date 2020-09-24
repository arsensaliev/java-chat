    package server;

    import java.sql.*;
    import java.util.ArrayList;
    import java.util.List;

    public class SimpleAuthService implements AuthService {
        private Connection connection;
        private Statement statement;
        private PreparedStatement psInsert;
        private class UserData {
            String login;
            String password;
            String nickname;

            public UserData(String login, String password, String nickname) {
                this.login = login;
                this.password = password;
                this.nickname = nickname;
            }
        }

        List<UserData> users;

        public List<UserData> getUsers() {
            return users;
        }

        public SimpleAuthService() {
            try {
                connect();

                users = new ArrayList<>();

                try {
                    connect();

                    ResultSet rs = statement.executeQuery("SELECT login, password, nick FROM users;");
                    while (rs.next()) {
                        users.add(new UserData(rs.getString("login"), rs.getString("password"), rs.getString("nick")));
                    }
                    rs.close();

                } catch (ClassNotFoundException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    disconnect();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }

        private void disconnect() {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        private void connect() throws ClassNotFoundException, SQLException {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            statement = connection.createStatement();
        }

        @Override
        public String getNicknameByLoginAndPassword(String login, String password) {
            for (UserData user : users) {
                if (user.login.equals(login) && user.password.equals(password)) {
                    return user.nickname;
                }
            }
            return null;
        }


        @Override
        public boolean registration(String login, String password, String nickname) {
            for (UserData user : users) {
                if (user.login.equals(login) || user.nickname.equals(nickname)) {
                    return false;
                }
            }

            users.add(new UserData(login, password, nickname));

            try {
                connect();

                psInsert = connection.prepareStatement("INSERT INTO users (login, password, nick) VALUES (?, ?, ?);");
                psInsert.setString(1, login);
                psInsert.setString(2, password);
                psInsert.setString(3, nickname);

                psInsert.executeUpdate();

                psInsert.close();

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }

            return true;
        }
    }
