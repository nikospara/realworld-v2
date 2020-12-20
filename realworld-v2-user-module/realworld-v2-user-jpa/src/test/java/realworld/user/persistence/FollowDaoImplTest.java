package realworld.user.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.persistence.EntityManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import realworld.test.jpa.JpaDaoExtension;
import realworld.test.liquibase.LiquibaseExtension;

/**
 * Tests for the {@link FollowDaoImpl}.
 */
@ExtendWith({LiquibaseExtension.class, JpaDaoExtension.class})
@EnabledIfSystemProperty(named = "database-test.active", matches = "true")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FollowDaoImplTest {

	private static final String USER_ID1 = UUID.randomUUID().toString();
	private static final String USERNAME1 = "test1";
	private static final String EMAIL1 = "test1@here.com";
	private static final String USER_ID2 = UUID.randomUUID().toString();
	private static final String USERNAME2 = "test2";
	private static final String EMAIL2 = "test2@here.com";

	private EntityManager em;
	private Statistics statistics;
	private FollowDaoImpl sut;

	@BeforeEach
	void init(EntityManager em, Statistics statistics) {
		this.em = em;
		this.statistics = statistics;
		sut = new FollowDaoImpl(em);
	}

	@AfterEach
	void afterEach() {
		statistics.clear();
	}

	@Test
	@Order(1)
	void testCreate() {
		em.getTransaction().begin();
		UserEntity u1 = new UserEntity();
		u1.setId(USER_ID1);
		u1.setUsername(USERNAME1);
		u1.setEmail(EMAIL1);
		em.persist(u1);
		UserEntity u2 = new UserEntity();
		u2.setId(USER_ID2);
		u2.setUsername(USERNAME2);
		u2.setEmail(EMAIL2);
		em.persist(u2);
		em.getTransaction().commit();
		em.clear();
		statistics.clear();

		em.getTransaction().begin();
		sut.create(USER_ID1, USER_ID2);
		em.getTransaction().commit();

		assertEquals(0, statistics.getEntityLoadCount());

		em.clear();
		FollowEntity f = em.find(FollowEntity.class, new FollowId(USER_ID1, USER_ID2));
		assertNotNull(f);
	}

	@Test
	@Order(2)
	void testExists() {
		em.getTransaction().begin();
		assertFalse(sut.exists(USER_ID2, USER_ID1));
		assertTrue(sut.exists(USER_ID1, USER_ID2));
		em.getTransaction().commit();
	}

	@Test
	@Order(3)
	void testFindAllFollowed() {
		em.getTransaction().begin();
		List<String> followed1 = sut.findAllFollowed(USER_ID1);
		assertEquals(Collections.singletonList(USERNAME2), followed1);
		List<String> followed2 = sut.findAllFollowed(USER_ID2);
		assertTrue(followed2.isEmpty());
		em.getTransaction().commit();
	}

	@Test
	@Order(4)
	void testCheckAllFollowed() {
		em.getTransaction().begin();
		Map<String, Boolean> result = sut.checkAllFollowed(USER_ID1, Arrays.asList(USERNAME2, "does_not_exist"));
		assertEquals(2, result.size());
		assertTrue(result.get(USERNAME2));
		assertFalse(result.get("does_not_exist"));
		em.getTransaction().commit();
	}

	@Test
	@Order(5)
	void testDelete() {
		int r;

		em.getTransaction().begin();
		r = sut.delete(USER_ID2, USER_ID1);
		assertEquals(0, r);
		r = sut.delete("xxx", "yyy");
		assertEquals(0, r);
		em.getTransaction().commit();

		em.getTransaction().begin();
		r = sut.delete(USER_ID1, USER_ID2);
		assertEquals(1, r);
		em.getTransaction().commit();
	}
}
