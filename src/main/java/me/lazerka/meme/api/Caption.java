package me.lazerka.meme.api;

/**
 * Embedded into Meme.
 *
 * @author dzmitry
 */
public class Caption {
	String text;
	int topPx;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getTopPx() {
		return topPx;
	}

	public void setTopPx(int topPx) {
		this.topPx = topPx;
	}
}
