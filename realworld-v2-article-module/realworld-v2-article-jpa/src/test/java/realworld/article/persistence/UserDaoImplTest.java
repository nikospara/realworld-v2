package realworld.article.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.UUID;

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

/**
 * Tests for the {@link UserDaoImpl}.
 */
@ExtendWith({LiquibaseExtension.class, JpaDaoExtension.class})
@EnabledIfSystemProperty(named = "database-test.active", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserDaoImplTest {

	private static final String ID = UUID.randomUUID().toString();
	private static final String USERNAME = "username";
	private static final String UPDATED_USERNAME = "updated_username";

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
	void testAdd() {
		em.getTransaction().begin();
		sut.add(ID, USERNAME);
		em.getTransaction().commit();
		em.clear();

		UserEntity u = em.find(UserEntity.class, ID);
		assertNotNull(u);
		assertEquals(USERNAME, u.getUsername());
		assertEquals(1L, statistics.getEntityInsertCount());
		assertEquals(1L, statistics.getEntityLoadCount());
	}

	@Test
	@Order(2)
	void testFindByUsername() {
		Optional<String> result = sut.findByUserName(USERNAME);
		assertEquals(ID, result.get());
		assertTrue(sut.findByUserName("username_that_doesnt_exist").isEmpty());
	}

	@Test
	@Order(3)
	void testFindById() {
		Optional<String> result = sut.findByUserId(ID);
		assertEquals(USERNAME, result.get());
		assertTrue(sut.findByUserId("user_id_that_doesnt_exist").isEmpty());
	}

	@Test
	@Order(4)
	void testUpdateUsername() {
		em.getTransaction().begin();
		sut.updateUsername(ID, UPDATED_USERNAME);
		em.getTransaction().commit();
		em.clear();

		UserEntity u = em.find(UserEntity.class, ID);
		assertNotNull(u);
		assertEquals(UPDATED_USERNAME, u.getUsername());

		em.getTransaction().begin();
		try {
			sut.updateUsername("nonexisting id", UPDATED_USERNAME);
			fail("updating a non-existing id should have thrown");
		}
		catch( EntityDoesNotExistException expected ) {
			// expected
		}
		em.getTransaction().rollback();
	}
}
