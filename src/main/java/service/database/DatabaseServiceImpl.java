package service.database;

import connector.PostgresConnector;
import enums.LangTemplate;
import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
public class DatabaseServiceImpl implements DatabaseService {
    private final PostgresConnector connector;

    public DatabaseServiceImpl(PostgresConnector connector) {
        this.connector = connector;
    }

    @Override
    public Map<LangTemplate, String> getWord(int day) {
        final String sql = "SELECT english, translation FROM english.words WHERE id = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, day);
            ResultSet rs = statement.executeQuery();
            Map<LangTemplate, String> result = new HashMap<>();
            if (rs.next()) {
                result.put(LangTemplate.WORD, rs.getString("english"));
                result.put(LangTemplate.TARGET, rs.getString("translation"));
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Map<LangTemplate, String>> getWordWithRange(int day, int range) {
        final String sql = """
                SELECT english, translation
                FROM english.words
                WHERE id >= ? and id < (? + ?)
                """;
        try (PreparedStatement statement = connector.getConnection().prepareStatement(sql)) {
            statement.setInt(1, day);
            statement.setInt(2, day);
            statement.setInt(3, range);

            ResultSet rs = statement.executeQuery();
            List<Map<LangTemplate, String>> result = new ArrayList<>(range);
            Map<LangTemplate, String> wordMap;
            while (rs.next()) {
                wordMap = new HashMap<>();
                String word = rs.getString("english");
                String translation = rs.getString("translation");

                wordMap.put(LangTemplate.WORD, word);
                wordMap.put(LangTemplate.TARGET, translation);
                result.add(wordMap);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getUserDay(long userId) {
        final String userExists = "SELECT day FROM telegram.users WHERE chatid = ?";

        try (PreparedStatement statement = connector.getConnection().prepareStatement(userExists)) {
            statement.setLong(1, userId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt("day");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //TODO how it works
        return 1;
    }

    @Override
    public void updateUsersDay(long userId, int day) {
        String query = "UPDATE telegram.users SET day = ? WHERE chatid = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(query)) {
            statement.setInt(1, day);
            statement.setLong(2, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllUsersWithDayAndWord() {
        String selectQuery = """
                select tu.chatid, tu.day, ew.english, ew.translation
                from telegram.users tu
                left join english.words ew on tu.day = ew.id
                """;
        try (Statement statement = connector.getConnection().createStatement()) {
            ResultSet rs = statement.executeQuery(selectQuery);
            List<Map<String, Object>> result = new LinkedList<>();
            while (rs.next()) {
                Map<String, Object> o = new HashMap<>();
                o.put("userId", rs.getLong("chatid"));
                o.put("day", rs.getInt("day"));
                o.put("english", rs.getString("english"));
                o.put("translation", rs.getInt("translation"));
                result.add(o);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateResults(long userId, int week) {
        final String selectQuery = "SELECT result FROM telegram.results WHERE chatid = ? AND week = ?";
        final String updateQuery = "UPDATE telegram.results SET result = ? WHERE chatid = ? AND week = ?";

        int result = 1;

        // select query
        try (PreparedStatement statement = connector.getConnection().prepareStatement(selectQuery)) {
            statement.setLong(1, userId);
            statement.setInt(2, week);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                result += rs.getInt("result");
            }
            log.debug("User id: {} || Week: {} || Result: {}",
                    userId, week, result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // update query
        try (PreparedStatement statement = connector.getConnection().prepareStatement(updateQuery)) {
            statement.setInt(1, result);
            statement.setLong(2, userId);
            statement.setInt(3, week);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createResult(long userId, int week) {
        // maybe need to init zero from database
        final String insertQuery = "INSERT INTO telegram.results VALUES (?, 0, ?)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(insertQuery)) {
            statement.setLong(1, userId);
            statement.setInt(2, week);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasTested(long userId, int week) {
        final String selectQuery = "SELECT * FROM telegram.results WHERE chatid = ? AND week = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(selectQuery)) {
            statement.setLong(1, userId);
            statement.setInt(2, week);

            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getExample(long userId) {
        final String selectQuery = "SELECT * FROM english.words WHERE id = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(selectQuery)) {
            statement.setLong(1, getUserDay(userId));

            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return rs.getString("examples");
            else return "";
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getLastTestResult(long userId, int week) {
        final String selectQuery = "SELECT result FROM telegram.results WHERE chatid = ? AND week = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(selectQuery)) {
            statement.setLong(1, userId);
            statement.setInt(2, week);

            ResultSet res = statement.executeQuery();
            //TODO DOCS
            if (res.next()) {
                return res.getInt("result");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTotalResult(long userId) {
        String selectQuery = "SELECT week, result FROM telegram.results WHERE chatid = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(selectQuery)) {
            statement.setLong(1, userId);

            ResultSet res = statement.executeQuery();
            StringBuilder result = new StringBuilder();
            if (!res.next()) {
                result.append("You haven't taken the test yet");
            } else {
                result.append("Week ")
                        .append(res.getInt("week"))
                        .append(": ")
                        .append(res.getInt("result"))
                        .append("/7\n");
                while (res.next()) {
                    result.append("Week ")
                            .append(res.getInt("week"))
                            .append(": ")
                            .append(res.getInt("result"))
                            .append("/7\n");
                }
            }
            return result.toString();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createUser(long userId) {
        final String selectQuery = "INSERT INTO telegram.users VALUES (?, 1, true)";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(selectQuery)) {
            statement.setLong(1, userId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean checkUser(long id) {
        String userExists = "SELECT chatid FROM telegram.users WHERE chatid = ?";
        try (PreparedStatement statement = connector.getConnection().prepareStatement(userExists)) {
            statement.setLong(1, id);

            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
