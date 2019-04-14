package realworld.user.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.persistence.EntityManager;

import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import realworld.EntityDoesNotExistException;
import realworld.test.jpa.JpaDaoExtension;
import realworld.test.liquibase.LiquibaseExtension;
import realworld.user.model.ImmutableUserData;
import realworld.user.model.UserData;

/**
 * Tests for the {@link UserDaoImpl}.
 */
@ExtendWith({LiquibaseExtension.class, JpaDaoExtension.class})
@EnabledIfSystemProperty(named = "database-test.active", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoImplTest {

	private static final String USERNAME = "username";
	private static final String EMAIL = "email.one@here.com";
	private static final String IMAGE_URL = "IMAGE.URL";
	private static final String ENCRYPTED_PASSWD = "enc_passwd";
	private static final String UPDATED_USERNAME = "updated_username";
	private static final String UPDATED_EMAIL = "updated_email.one@here.com";
	private static final String UPDATED_IMAGE_URL = "updated_IMAGE.URL";
	private static final String UPDATED_ENCRYPTED_PASSWD = "updated_enc_passwd";

	private EntityManager em;
	private Statistics statistics;
	private UserDaoImpl sut;

	@BeforeEach
	void init(EntityManager em, Statistics statistics) {
		this.em = em;
		this.statistics = statistics;
		sut = new UserDaoImpl(em);
	}

	@AfterEach
	void afterEach() {
		statistics.clear();
	}

	@Test
	@Order(1)
	void testCreate() {
		em.getTransaction().begin();
		UserData result = sut.create(ImmutableUserData.builder().username(USERNAME).email(EMAIL).imageUrl(IMAGE_URL).build(), ENCRYPTED_PASSWD);
		em.getTransaction().commit();
		em.clear();

		assertNotNull(result.getId());

		User u = em.find(User.class, result.getId());
		assertNotNull(u);
		assertEquals(USERNAME, u.getUsername());
		assertEquals(EMAIL, u.getEmail());
		assertEquals(IMAGE_URL, u.getImageUrl());
		assertEquals(ENCRYPTED_PASSWD, u.getPassword());
		assertEquals(1L, statistics.getEntityInsertCount());
		assertEquals(1L, statistics.getEntityLoadCount());
	}

	@Test
	@Order(2)
	void testUsernameExists() {
		assertTrue(sut.usernameExists(USERNAME));
		assertFalse(sut.usernameExists("I do not exist"));
	}

	@Test
	@Order(3)
	void testEmailExists() {
		assertTrue(sut.emailExists(EMAIL));
		assertFalse(sut.emailExists("I do not exist"));
	}

	@Test
	@Order(4)
	void testFindByEmailAndPassword() {
		UserData result1 = sut.findByEmailAndPassword(EMAIL, ENCRYPTED_PASSWD).get();
		assertEquals(USERNAME, result1.getUsername());
		assertEquals(IMAGE_URL, result1.getImageUrl());
		UserData result2 = sut.findByEmailAndPassword(EMAIL.toUpperCase(), ENCRYPTED_PASSWD).get();
		assertEquals(USERNAME, result2.getUsername());
		assertEquals(IMAGE_URL, result2.getImageUrl());
		assertTrue(sut.findByEmailAndPassword("xxx", ENCRYPTED_PASSWD).isEmpty());
		assertTrue(sut.findByEmailAndPassword(EMAIL, "xxx").isEmpty());
	}

	@Test
	@Order(5)
	void testFindByUsername() {
		UserData result = sut.findByUserName(USERNAME).get();
		assertEquals(EMAIL, result.getEmail());
		assertEquals(IMAGE_URL, result.getImageUrl());
		assertTrue(sut.findByUserName("username_that_doesnt_exist").isEmpty());
	}

	@Test
	@Order(6)
	void testUpdateNonExistingUser() {
		em.getTransaction().begin();
		try {
			sut.createUpdate().setImageUrl(true, UPDATED_IMAGE_URL).executeForId("does_not_exist");
			fail("updating a non-existing id should throw EntityDoesNotExistException");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
		em.getTransaction().commit();
	}

	@Test
	@Order(7)
	void testUpdateWithNoRealUpdate() {
		String userid = sut.findByUserName(USERNAME).get().getId();
		em.clear();

		em.getTransaction().begin();
		sut.createUpdate()
				.setUsername(false, UPDATED_USERNAME)
				.setEmail(false, UPDATED_EMAIL)
				.setImageUrl(false, UPDATED_IMAGE_URL)
				.setPassword(false, UPDATED_ENCRYPTED_PASSWD)
				.executeForId(userid);
		em.getTransaction().commit();
		em.clear();

		assertEquals(1, statistics.getPrepareStatementCount());

		User u = em.find(User.class, userid);
		assertNotNull(u);
		assertEquals(USERNAME, u.getUsername());
		assertEquals(EMAIL, u.getEmail());
		assertEquals(IMAGE_URL, u.getImageUrl());
		assertEquals(ENCRYPTED_PASSWD, u.getPassword());
	}

	@Test
	@Order(8)
	void testUpdate() {
		String userid = sut.findByUserName(USERNAME).get().getId();
		em.clear();

		em.getTransaction().begin();
		sut.createUpdate()
				.setUsername(true, UPDATED_USERNAME)
				.setEmail(true, UPDATED_EMAIL)
				.setPassword(true, UPDATED_ENCRYPTED_PASSWD)
				.executeForId(userid);
		em.getTransaction().commit();
		em.clear();

		assertEquals(2, statistics.getPrepareStatementCount());

		User u = em.find(User.class, userid);
		assertNotNull(u);
		assertEquals(UPDATED_USERNAME, u.getUsername());
		assertEquals(UPDATED_EMAIL, u.getEmail());
		assertEquals(IMAGE_URL, u.getImageUrl());
		assertEquals(UPDATED_ENCRYPTED_PASSWD, u.getPassword());
	}
}
