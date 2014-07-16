package me.lazerka.meme.sql;

import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment;
import com.google.inject.AbstractModule;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.builtin.PooledDataSourceProvider;

import java.util.Properties;

import static com.google.appengine.api.utils.SystemProperty.Environment.Value.Production;
import static com.google.inject.name.Names.bindProperties;
import static com.google.inject.name.Names.named;

/**
 * @author Dzmitry Lazerka
 */
public class SqlModule extends AbstractModule {
	private static final String CLOUD_SQL_INSTANCE_NAME = "i1";

	@Override
	protected void configure() {
		// Figure out driver class name and url, as per GAE docs.
		String className;
		String url;
		if (SystemProperty.environment.value() == Production) {
			SystemProperty appId = Environment.applicationId;
			className = "com.mysql.jdbc.GoogleDriver";
			// Load the class that provides the new "jdbc:google:mysql://" prefix.

			url = "jdbc:google:mysql://" + appId + ":" + CLOUD_SQL_INSTANCE_NAME + "/meme?user=meme";
		} else {
			// Local MySQL instance to use during development.
			className = "com.mysql.jdbc.Driver";
			url = "jdbc:mysql://127.0.0.1:3306/meme?user=dl";

			// Alternatively, connect to a Google Cloud SQL instance using:
			// jdbc:mysql://ip-address-of-google-cloud-sql-instance:3306/guestbook?user=root
		}

		// Try to load the class.
		try {
			Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		// Bind.
		bindConstant().annotatedWith(named("JDBC.driver")).to(className);
		bindConstant().annotatedWith(named("JDBC.url")).to(url);

		install(new BatisModule());

	}

	private static class BatisModule extends MyBatisModule {
		@Override
		protected void initialize() {
			bindDataSourceProviderType(PooledDataSourceProvider.class);
			bindTransactionFactoryType(JdbcTransactionFactory.class);


			//	DataSource dataSource = BlogDataSourceFactory.getBlogDataSource();
			//	TransactionFactory transactionFactory = new JdbcTransactionFactory();
			//	Environment environment = new Environment("development", transactionFactory, dataSource);
			//	Configuration configuration = new Configuration(environment);
			//	configuration.addMapper(BlogMapper.class);
			//	SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);

			addMapperClass(MemeMapper.class);
			addMapperClass(ImageMapper.class);
			addMapperClass(CaptionMapper.class);


			Properties myBatisProperties = new Properties();
			myBatisProperties.setProperty("mybatis.environment.id", "test");
			myBatisProperties.setProperty("JDBC.schema", "mybatis-guice_TEST");
			myBatisProperties.setProperty("derby.create", "true");
			myBatisProperties.setProperty("JDBC.username", "dl");
			myBatisProperties.setProperty("JDBC.password", "");
			myBatisProperties.setProperty("JDBC.autoCommit", "false");
			bindProperties(this.binder(), myBatisProperties);
		}
	}
}
