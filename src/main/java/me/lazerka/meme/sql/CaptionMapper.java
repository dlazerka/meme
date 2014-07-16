package me.lazerka.meme.sql;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author Dzmitry Lazerka
 */
public interface CaptionMapper {
	@Insert("INSERT INTO caption (meme_id, `text`, top_px) VALUES(#{memeId}, #{text}, #{topPx})")
	long insert(
			@Param("memeId") long memeId,
			@Param("text") String text,
			@Param("topPx") int topPx
	);
}
