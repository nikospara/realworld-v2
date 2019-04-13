package realworld.user.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.persistence.EntityManager;

import java.util.UUID;

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

/**
 * Tests for the {@link BiographyDaoImpl}.
 */
@ExtendWith({LiquibaseExtension.class, JpaDaoExtension.class})
@EnabledIfSystemProperty(named = "database-test.active", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BiographyDaoImplTest {

	private static final String USER_ID = UUID.randomUUID().toString();
	private static final String USERNAME = "bio_test";
	private static final String EMAIL = "bio_test@here.com";
	private static final String PASSWORD = "pwd";
	private static final String BIO = "Biography of bio_test";

	private EntityManager em;
	private Statistics statistics;
	private BiographyDaoImpl sut;

	@BeforeEach
	void init(EntityManager em, Statistics statistics) {
		this.em = em;
		this.statistics = statistics;
		sut = new BiographyDaoImpl(em);
	}

	@Test
	@Order(1)
	void testCreate() {
		em.getTransaction().begin();
		User u = new User();
		u.setId(USER_ID);
		u.setUsername(USERNAME);
		u.setEmail(EMAIL);
		u.setPassword(PASSWORD);
		em.persist(u);
		em.getTransaction().commit();
		em.clear();
		statistics.clear();

		em.getTransaction().begin();
		sut.create(USER_ID, BIO);
		em.getTransaction().commit();

		assertEquals(0, statistics.getEntityLoadCount());

		em.clear();
		Biography b = em.find(Biography.class, USER_ID);
		assertNotNull(b);
		assertEquals(BIO, b.getBio());
	}
}
