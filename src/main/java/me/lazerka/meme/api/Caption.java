package me.lazerka.meme.api;

import com.google.common.base.Strings;
import com.googlecode.objectify.annotation.Index;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Embedded into Meme.
 *
 * @author dzmitry
 */
public class Caption {
	public static int TEXT_MAX_LENGTH = 255;

	@Index
	String text;

	Caption.Position position;

	int topPx;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		checkArgument(!Strings.isNullOrEmpty(text));
		checkArgument(text.length() <= TEXT_MAX_LENGTH);
		this.text = text;
	}

	public int getTopPx() {
		return topPx;
	}

	public void setTopPx(int topPx) {
		checkArgument(topPx >= 0);
		checkArgument(topPx < 10000);
		this.topPx = topPx;
	}

	public static enum Position {
		TOP, BOTTOM, MIDDLE, CUSTOM
	}
}
