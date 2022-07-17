package realworld.v1.services;

import java.util.concurrent.CompletionStage;
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
	CompletionStage<AuthenticatedUser> authenticate(Credentials credentials) throws LoginException;

	CompletionStage<AuthenticatedUser> register(UserRegistrationData userRegistrationParam, Credentials credentials);

	CompletionStage<Void> updateUser(AuthenticatedUser loggedInUser, User userToUpdate);

	CompletionStage<Void> updatePassword(Credentials credentials, String newPassword);

	CompletionStage<User> retrieveProfile(Username username);

	CompletionStage<Void> follow(AuthenticatedUser loggedInUser, UserId userToFollow);

	CompletionStage<Void> unfollow(AuthenticatedUser loggedInUser, UserId userToUnfollow);

	CompletionStage<SearchResult<ArticleSearchResult>> listArticles(AuthenticatedUser loggedInUser, ArticleSearchCriteria criteria);

	CompletionStage<SearchResult<ArticleSearchResult>> listArticles(ArticleSearchCriteria criteria);

	CompletionStage<SearchResult<ArticleSearchResult>> feedArticles(AuthenticatedUser loggedInUser, OffsetAndLimit offsetAndLimit);

	CompletionStage<ArticleSearchResult> fetchArticle(AuthenticatedUser loggedInUser, String slug);

	CompletionStage<ArticleSearchResult> fetchArticle(String slug);

	CompletionStage<ArticleSearchResult> createArticle(AuthenticatedUser loggedInUser, ArticleUpsertData articleData);

	CompletionStage<ArticleSearchResult> updateArticle(AuthenticatedUser loggedInUser, ArticleUpsertData articleData);

	CompletionStage<Void> deleteArticle(AuthenticatedUser loggedInUser, String slug);

	CompletionStage<CommentSearchResult> commentOnArticle(AuthenticatedUser loggedInUser, ArticleId articleId, CommentCreationData commentCreationData);

	CompletionStage<SearchResult<CommentSearchResult>> listCommentsForArticle(AuthenticatedUser loggedInUser, ArticleId articleId, OffsetAndLimit offsetAndLimit);

	CompletionStage<SearchResult<CommentSearchResult>> listCommentsForArticle(ArticleId articleId, OffsetAndLimit offsetAndLimit);

	CompletionStage<Void> deleteComment(AuthenticatedUser loggedInUser, CommentId id);

	CompletionStage<ArticleSearchResult> favoriteArticle(AuthenticatedUser loggedInUser, String slug);

	CompletionStage<ArticleSearchResult> unfavoriteArticle(AuthenticatedUser loggedInUser, String slug);

	CompletionStage<SearchResult<Tag>> fetchTags(OffsetAndLimit offsetAndLimit);
}
