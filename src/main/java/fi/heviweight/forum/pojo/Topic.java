/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.heviweight.forum.pojo;

public class Topic {
    private final int boardId;
    private final int topicId;
    private final String topicName;
    private final String boardName;
    private final int postCount;
    private final String timestamp;

    public Topic(int boardId, int id, String name, String Board, int postCount, String timestamp) {
        this.boardId = boardId;
        this.topicId = id;
        this.topicName = name;
        this.boardName = Board;
        this.postCount = postCount;
        this.timestamp = timestamp;
    }

    public int getId() {
        return topicId;
    }

    public String getName() {
        return topicName;
    }

    public String getBoard() {
        return boardName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getBoardId() {
        return boardId;
    }

    public int getPostCount() {
        return postCount;
    }
}
