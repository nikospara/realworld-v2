package realworld.keycloak.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.keycloak.events.admin.ResourceType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static realworld.user.model.events.UserModificationEventType.CREATE;
import static realworld.user.model.events.UserModificationEventType.UPDATE;

import java.util.UUID;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakTransactionManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import realworld.user.model.UserUpdateData;
import realworld.user.model.events.UserModificationEvent;

/**
 * Tests for the {@link RealworldEventListenerProvider}.
 */
@ExtendWith(MockitoExtension.class)
public class RealworldEventListenerProviderTest {

	private static final String TOPIC_NAME = "TOPIC_NAME";
	private static final String ADMIN_USER_ID = UUID.randomUUID().toString();

	@Mock
	private KeycloakSession session;

	@Mock
	private Producer<String, String> producer;

	@Mock
	private ObjectMapper om;

	@Mock
	private KeycloakTransactionManager kcTxManager;

	private RealworldEventListenerProvider sut;

	@BeforeEach
	@SuppressWarnings("unchecked")
	void beforeEach() {
		sut = new RealworldEventListenerProvider(session, producer, om, TOPIC_NAME);
//		when(session.realms()).thenReturn(mock(RealmProvider.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS)));
		lenient().when(session.getTransactionManager()).thenReturn(kcTxManager);
		when(producer.send(any())).thenReturn(mock(Future.class));
	}

	@Test
	void testOnAdminEventWithAllAttributes() throws Exception {
		String representation = "{" +
				"\"id\":\"5ec92c22-212c-493f-8efe-dc5d54dca6aa\"," +
				"\"createdTimestamp\":1560199040575," +
				"\"username\":\"squidward\"," +
				"\"enabled\":true," +
				"\"totp\":false," +
				"\"emailVerified\":false," +
				"\"firstName\":\"Squidward\"," +
				"\"lastName\":\"Tentacles\"," +
				"\"email\":\"squidward.tentacles@krusty-krab.com\"," +
				"\"attributes\":{\"imageUrl\":[\"http://image/url\"],\"bio\":[\"I am grumpy and sorehead.\"]}," +
				"\"disableableCredentialTypes\":[\"password\"]," +
				"\"requiredActions\":[]," +
				"\"notBefore\":0," +
				"\"access\":{\"manageGroupMembership\":true,\"view\":true,\"mapRoles\":true,\"impersonate\":true,\"manage\":true}}";
		AdminEvent event = makeAdminEvent(OperationType.UPDATE, representation);

		sut.onEvent(event, true);

		ArgumentCaptor<UserModificationEvent> modEventCaptor = ArgumentCaptor.forClass(UserModificationEvent.class);
		verify(om).writeValueAsString(modEventCaptor.capture());

		UserModificationEvent result = modEventCaptor.getValue();
		assertNotNull(result);
		assertEquals(ADMIN_USER_ID, result.getInitiatingUserId());
		assertEquals(UPDATE, result.getType());
		UserUpdateData uud = result.getPayload();
		assertEquals("5ec92c22-212c-493f-8efe-dc5d54dca6aa", uud.getId());
		assertEquals("squidward", uud.getUsername());
		assertEquals("squidward.tentacles@krusty-krab.com", uud.getEmail());
		assertEquals("http://image/url", uud.getImageUrl());
		assertEquals("I am grumpy and sorehead.", uud.getBio());
	}

	@Test
	void testOnAdminEventWithoutOneAttribute() throws Exception {
		String representation = "{" +
				"\"id\":\"e2e3210e-8a5c-4baf-b139-c9026512687c\"," +
				"\"createdTimestamp\":1560094935472," +
				"\"username\":\"patrick\"," +
				"\"enabled\":true," +
				"\"totp\":false," +
				"\"emailVerified\":false," +
				"\"firstName\":\"Patrick\"," +
				"\"lastName\":\"Star\"," +
				"\"email\":\"patrick.star@bikini-bottom.com\"," +
				"\"attributes\":{\"bio\":[\"The dummest starfish in the deep.\"]}," +
				"\"disableableCredentialTypes\":[\"password\"]," +
				"\"requiredActions\":[]," +
				"\"notBefore\":0," +
				"\"access\":{\"manageGroupMembership\":true,\"view\":true,\"mapRoles\":true,\"impersonate\":true,\"manage\":true}}";
		AdminEvent event = makeAdminEvent(OperationType.CREATE, representation);

		sut.onEvent(event, true);

		ArgumentCaptor<UserModificationEvent> modEventCaptor = ArgumentCaptor.forClass(UserModificationEvent.class);
		verify(om).writeValueAsString(modEventCaptor.capture());

		UserModificationEvent result = modEventCaptor.getValue();
		assertNotNull(result);
		assertEquals(ADMIN_USER_ID, result.getInitiatingUserId());
		assertEquals(CREATE, result.getType());
		UserUpdateData uud = result.getPayload();
		assertEquals("e2e3210e-8a5c-4baf-b139-c9026512687c", uud.getId());
		assertEquals("patrick", uud.getUsername());
		assertEquals("patrick.star@bikini-bottom.com", uud.getEmail());
		assertNull(uud.getImageUrl());
		assertFalse(uud.isExplicitlySet(UserUpdateData.PropName.IMAGE_URL));
		assertEquals("The dummest starfish in the deep.", uud.getBio());
	}

	@Test
	void testOnAdminEventWithoutAttributes() throws Exception {
		String representation = "{" +
				"\"id\":\"54e73f2d-bb6b-45e9-8099-7bf66886db14\"," +
				"\"createdTimestamp\":1560102787333," +
				"\"username\":\"sandy\"," +
				"\"enabled\":true," +
				"\"totp\":false," +
				"\"emailVerified\":false," +
				"\"firstName\":\"Sandy\"," +
				"\"lastName\":\"Cheeks\"," +
				"\"email\":\"sandy.cheeks@bikini-bottom.org\"," +
				"\"disableableCredentialTypes\":[\"password\"]," +
				"\"requiredActions\":[]," +
				"\"notBefore\":0," +
				"\"access\":{\"manageGroupMembership\":true,\"view\":true,\"mapRoles\":true,\"impersonate\":true,\"manage\":true}}";
		AdminEvent event = makeAdminEvent(OperationType.CREATE, representation);

		sut.onEvent(event, true);

		ArgumentCaptor<UserModificationEvent> modEventCaptor = ArgumentCaptor.forClass(UserModificationEvent.class);
		verify(om).writeValueAsString(modEventCaptor.capture());

		UserModificationEvent result = modEventCaptor.getValue();
		assertNotNull(result);
		assertEquals(ADMIN_USER_ID, result.getInitiatingUserId());
		assertEquals(CREATE, result.getType());
		UserUpdateData uud = result.getPayload();
		assertEquals("54e73f2d-bb6b-45e9-8099-7bf66886db14", uud.getId());
		assertEquals("sandy", uud.getUsername());
		assertEquals("sandy.cheeks@bikini-bottom.org", uud.getEmail());
		assertNull(uud.getImageUrl());
		assertFalse(uud.isExplicitlySet(UserUpdateData.PropName.IMAGE_URL));
		assertNull(uud.getBio());
		assertFalse(uud.isExplicitlySet(UserUpdateData.PropName.BIO));
	}

	private AdminEvent makeAdminEvent(OperationType opType, String representation) {
		AdminEvent event = new AdminEvent();
		event.setResourceType(USER);
		event.setOperationType(opType);
		event.setRepresentation(representation);
		AuthDetails authDetails = new AuthDetails();
		authDetails.setUserId(ADMIN_USER_ID);
		event.setAuthDetails(authDetails);
		return event;
	}
}
