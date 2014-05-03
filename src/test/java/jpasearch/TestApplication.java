package jpasearch;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import io.blep.spysql.SpyDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author speralta
 */
@Configuration
@ComponentScan
@EnableTransactionManagement
public class TestApplication {

    @Bean
    public SpyDataSource dataSource() {
        return new SpyDataSource(new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build());
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setPackagesToScan("jpasearch.domain");
        entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.search.analyzer", "org.apache.lucene.analysis.fr.FrenchAnalyzer");
        entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.search.default.directory_provider", "ram");
        entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.search.default.worker.execution", "async");
        entityManagerFactoryBean.getJpaPropertyMap().put("hibernate.search.default.worker.execution", "3");
        entityManagerFactoryBean.afterPropertiesSet();
        entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        return entityManagerFactoryBean.getObject();
    }

    @Bean
    public HibernateJpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setDatabase(Database.H2);
        adapter.setGenerateDdl(true);
        adapter.getJpaPropertyMap().put("hibernate.ddl-auto", "none");
        adapter.getJpaPropertyMap().put("hibernate.naming-strategy", "org.hibernate.cfg.DefaultNamingStrategy");
        return adapter;
    }
}
