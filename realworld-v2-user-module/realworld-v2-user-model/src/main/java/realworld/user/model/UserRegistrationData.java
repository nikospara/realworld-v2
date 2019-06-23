package realworld.user.model;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

/**
 * Data required for user registration.
 */
@Value.Immutable
@Value.Style(builder = "new")
@JsonDeserialize(builder = ImmutableUserRegistrationData.Builder.class)
public interface UserRegistrationData {

	/** Get the user name. */
	@NotNull
	@Size(min=5)
	String getUsername();

	/** Get the user email. */
	@NotNull
	@Pattern(regexp="^.+@.+\\.[a-z]+$", flags=CASE_INSENSITIVE)
	String getEmail();

	/** Get the link to the user image. */
	@Nullable
	String getImageUrl();

	/** Get the biography of the user. */
	@Nullable
	String getBio();
}
