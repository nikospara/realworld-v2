package realworld.user.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;

import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

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

	private EntityManager em;
	private Statistics statistics;
	private UserDaoImpl sut;

	@BeforeEach
	void init(EntityManager em, Statistics statistics) {
		this.em = em;
		this.statistics = statistics;
		sut = new UserDaoImpl(em);
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
	@Order(5)
	void testFindByUsername() {
		UserData result = sut.findByUserName(USERNAME).get();
		assertEquals(EMAIL, result.getEmail());
		assertEquals(IMAGE_URL, result.getImageUrl());
		assertTrue(sut.findByUserName("username_that_doesnt_exist").isEmpty());
	}
}
