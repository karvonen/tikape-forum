package fi.heviweight.forum.pojo;

import fi.heviweight.forum.db.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class TopicDao {

    private Database db;

    public TopicDao(Database db) {
        this.db = db;
    }

    public List<Topic> getTopics(int boardId) throws SQLException {
        try (Connection con = db.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT top.tId AS topicId, top.N AS topicName, "
                    + "pos.Ts AS Viimeisin, pos.c AS Viesteja, "
                    + "top.Id AS boardId, top.bN AS boardName FROM ("
                    + "SELECT board.id AS Id, board.name AS bN, "
                    + "topic.id AS tId, topic.name AS N "
                    + "FROM topic, board "
                    + "WHERE topic.board_id = board.id "
                    + "AND topic.id = ?) top LEFT JOIN ("
                    + "SELECT post.topic_id AS tId, COUNT(*) AS c, "
                    + "MAX(timestamp) AS Ts FROM post "
                    + "WHERE post.topic_id = ? "
                    + "GROUP BY post.topic_id) pos "
                    + "ON pos.tId = top.tId;");
            stmt.setInt(1, boardId);
            stmt.setInt(2, boardId);
            return db.queryAndCollect(stmt, rs -> {
                return new Topic(
                        rs.getInt("boardId"),
                        rs.getInt("topicId"),
                        rs.getString("topicName"),
                        rs.getString("boardName"),
                        rs.getInt("Viesteja"),
                        rs.getString("Viimeisin"));
            });
        }
    }

    public List<Topic> getTopic(int topicId) throws SQLException {
        try (Connection con = db.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT board.id AS boardId, "
                    + "board.name AS boardName, topic.id As topicId, "
                    + "topic.name AS topicName, COUNT(Post.id) AS Viesteja, "
                    + "post.timestamp AS Viimeisin "
                    + "FROM board, topic, post "
                    + "WHERE board.id = topic.board_id "
                    + "AND topic.id = ? "
                    + "AND post.topic_id = ? "
                    + "GROUP BY topic.name "
                    + "ORDER BY post.timestamp DESC LIMIT 10;");
            stmt.setInt(1, topicId);
            stmt.setInt(2, topicId);
            return db.queryAndCollect(stmt, rs -> {
                return new Topic(
                        rs.getInt("boardId"),
                        rs.getInt("topicId"),
                        rs.getString("topicName"),
                        rs.getString("boardName"),
                        rs.getInt("Viesteja"),
                        rs.getString("Viimeisin"));
            });
        }
    }

    public int addTopic(String message, int boardId) throws SQLException {
        try (Connection con = db.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO topic (board_id, name) VALUES (?, ?);");
            stmt.setInt(1, boardId);
            stmt.setString(2, message);
            db.execute(stmt);
            return lastTopic();
        }
    }

    private Integer lastTopic() throws SQLException {
        try (Connection con = db.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT topic.id FROM topic ORDER BY topic.id DESC LIMIT 1;");
            List<Integer> topics = db.queryAndCollect(stmt, rs -> {
                return new Integer(rs.getInt("id"));
            });
            return topics.get(0);
        }
    }
}
