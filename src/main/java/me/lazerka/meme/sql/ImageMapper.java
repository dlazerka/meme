package me.lazerka.meme.sql;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author Dzmitry Lazerka
 */
public interface ImageMapper {
	@Insert("INSERT INTO image (blobkey, filename, size) VALUES(#{blobKey}, #{fileName}, #{size})")
	@Options(useGeneratedKeys=true, keyProperty="id")
	long insert(
			@Param("blobKey") String blobKey,
			@Param("fileName") String fileName,
			@Param("size") int size
	);
}
