package me.lazerka.meme.gae.old.model;

/**
 * @author amormysh@gmail.com (Andrey Mormysh)
 */
public class Comment {
  public static final String KIND = "Comment";
  public static final String MEME_ID = "memeId";
  public static final String TEXT = "text";
  public static final String DATE = "date";
  public static final String AUTHOR = "author";

  private long memeId;
  private String text;
  private long timestamp;
  private String author;

  public Comment() {
  }

  public Comment(long memeId, String text, long timestamp, String author) {
    this.memeId = memeId;
    this.text = text;
    this.timestamp = timestamp;
    this.author = author;
  }

  public long getMemeId() {
    return memeId;
  }

  public void setMemeId(long memeId) {
    this.memeId = memeId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

}
