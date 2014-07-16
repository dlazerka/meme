package me.lazerka.meme.sql;

import me.lazerka.meme.api.Meme;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Dzmitry Lazerka
 */
public interface MemeMapper {
	@Select("SELECT * FROM meme ORDER BY created_at LIMIT #{count}")
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
}
