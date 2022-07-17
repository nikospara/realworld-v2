package realworld.v1.services;

import java.util.Optional;
import javax.security.auth.login.LoginException;

import realworld.OffsetAndLimit;
import realworld.SearchResult;
import realworld.v1.types.ArticleId;
import realworld.v1.types.CommentId;
import realworld.v1.types.UserId;
import realworld.v1.types.Username;
import realworld.v1.model.Tag;
import realworld.v1.model.User;
import realworld.v1.services.types.ArticleUpsertData;
import realworld.v1.services.types.ArticleSearchCriteria;
import realworld.v1.services.types.ArticleSearchResult;
import realworld.v1.services.types.AuthenticatedUser;
import realworld.v1.services.types.CommentCreationData;
import realworld.v1.services.types.CommentSearchResult;
import realworld.v1.services.types.Credentials;
import realworld.v1.services.types.UserRegistrationData;

public interface Realworld2App {
	// TODO Make asynchronous
	AuthenticatedUser authenticate(Credentials credentials) throws LoginException;

	AuthenticatedUser register(UserRegistrationData userRegistrationParam, Credentials credentials);

	void updateUser(AuthenticatedUser loggedInUser, User userToUpdate);

	void updatePassword(Credentials credentials, String newPassword);

	User retrieveProfile(Username username);

	void follow(AuthenticatedUser loggedInUser, UserId userToFollow);

	void unfollow(AuthenticatedUser loggedInUser, UserId userToUnfollow);

	SearchResult<ArticleSearchResult> listArticles(AuthenticatedUser loggedInUser, ArticleSearchCriteria criteria);

	SearchResult<ArticleSearchResult> listArticles(ArticleSearchCriteria criteria);

	SearchResult<ArticleSearchResult> feedArticles(AuthenticatedUser loggedInUser, OffsetAndLimit offsetAndLimit);

	Optional<ArticleSearchResult> fetchArticle(AuthenticatedUser loggedInUser, String slug);

	Optional<ArticleSearchResult> fetchArticle(String slug);

	Optional<ArticleSearchResult> createArticle(AuthenticatedUser loggedInUser, ArticleUpsertData articleData);

	Optional<ArticleSearchResult> updateArticle(AuthenticatedUser loggedInUser, ArticleUpsertData articleData);

	void deleteArticle(AuthenticatedUser loggedInUser, String slug);

	Optional<CommentSearchResult> commentOnArticle(AuthenticatedUser loggedInUser, ArticleId articleId, CommentCreationData commentCreationData);

	SearchResult<CommentSearchResult> listCommentsForArticle(AuthenticatedUser loggedInUser, ArticleId articleId, OffsetAndLimit offsetAndLimit);

	SearchResult<CommentSearchResult> listCommentsForArticle(ArticleId articleId, OffsetAndLimit offsetAndLimit);

	void deleteComment(AuthenticatedUser loggedInUser, CommentId id);

	ArticleSearchResult favoriteArticle(AuthenticatedUser loggedInUser, String slug);

	ArticleSearchResult unfavoriteArticle(AuthenticatedUser loggedInUser, String slug);

	SearchResult<Tag> fetchTags(OffsetAndLimit offsetAndLimit);
}
