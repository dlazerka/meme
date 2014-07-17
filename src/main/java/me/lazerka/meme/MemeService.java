package me.lazerka.meme;

import me.lazerka.meme.api.Meme;

import java.util.List;

/**
 * @author Dzmitry Lazerka
 */
public interface MemeService {

	List<Meme> getLatest(int count);

	void create(Meme meme);

	void delete(long id);
}
