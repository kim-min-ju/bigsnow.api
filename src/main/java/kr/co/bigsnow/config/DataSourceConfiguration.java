/**
 * Class Summary. <br>
 * DB Controller class.
 * @since 1.00
 * @version 1.00 - 2011. 01. 20
 * @author 정소선
 * @see
 */
package kr.co.bigsnow.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfiguration{

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")  // application.properties 중 prefix 가 들어간 key들을 찾는다.
    public DataSource DataSource() {
    	
    	System.out.println("=======================DataSource()=============================" );
    	
		return new HikariDataSource();
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory( @Qualifier("dataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception 
    {
    	
    	System.out.println("=======================SqlSessionFactory()=============================" );
    	
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("mapper/**/*.xml"));

        return sqlSessionFactory.getObject();
    }

    @Bean(name="sqlSession")
    public SqlSessionTemplate sqlSession(SqlSessionFactory sqlSessionFactory) throws Exception {
    	
    	System.out.println("=======================SqlSessionTemplate()=============================" );
    	
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(DataSource());
    }

}
