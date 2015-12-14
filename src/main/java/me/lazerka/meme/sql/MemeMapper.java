package me.lazerka.meme.sql;

import me.lazerka.meme.api.Meme;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Dzmitry Lazerka
 */
public interface MemeMapper {
	@Select("SELECT meme.id, meme.created_by, meme.created_at, image.blobkey, image.filename, image.size " +
			"FROM meme " +
			"    INNER JOIN image ON image.id = meme.image_id " +
			"ORDER BY created_at " +
			"LIMIT #{count}")
	List<Meme> getLatest(
			@Param("count") int count
	);

	@Insert("INSERT INTO meme (created_by, image_id) VALUES(#{createdBy}, #{imageId})")
	long insert(
			@Param("createdBy") String createdBy,
			@Param("imageId") long imageId
	);

	@Delete("DELETE FROM meme WHERE id = #{id}")
	void delete(@Param("id") long id);

	@Select("SELECT * FROM meme WHERE id = #{id}")
	Meme get(long id);
}
