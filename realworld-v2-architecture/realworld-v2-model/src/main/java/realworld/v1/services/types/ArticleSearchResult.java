package realworld.v1.services.types;

import java.util.Optional;

import realworld.v1.model.Article;

public interface ArticleSearchResult {
	Article getArticle();

	/**
	 * Return if the current user has favorited the author of the article.
	 * If the call was made anonymously, the {@code Optional} is empty.
	 *
	 * @return If the current user has favorited the author of the article
	 */
	Optional<Boolean> isFavorited();

	/**
	 * Return if the current user is following the author of the article.
	 * If the call was made anonymously, the {@code Optional} is empty.
	 *
	 * @return If the current user is following the author of the article
	 */
	Optional<Boolean> isFollowingAuthor();

	int getFavoritesCount();
}
