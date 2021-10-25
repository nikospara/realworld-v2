package realworld.test.quarkus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Start Postgres as a test resource, implement injection of the Postgres test container,
 * expose settings to the Quarkus test environment, apply DB migrations.
 */
public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

	private PostgreSQLContainer<?> postgres;

	@Override
	public Map<String, String> start() {
		postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14-alpine"));
		postgres.start();
		applyDbMigrations();
		Map<String, String> sysprops = new HashMap<>();
		sysprops.put("quarkus.datasource.jdbc.url", postgres.getJdbcUrl());
		sysprops.put("quarkus.datasource.username", postgres.getUsername());
		sysprops.put("quarkus.datasource.password", postgres.getPassword());
		return sysprops;
	}

	@Override
	public void stop() {
		postgres.stop();
	}

	private void applyDbMigrations() {
		try( Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()) ) {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
			Liquibase liquibase = new Liquibase("db.changelog.xml", new ClassLoaderResourceAccessor(PostgresTestResource.class.getClassLoader()), database);
			liquibase.update("");
		} catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void inject(TestInjector testInjector) {
		testInjector.injectIntoFields(postgres, new TestInjector.AnnotatedAndMatchesType(InjectPostgres.class, PostgreSQLContainer.class));
	}
}
