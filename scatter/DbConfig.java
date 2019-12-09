package transfer;

import com.mysql.cj.jdbc.Driver;
import oracle.jdbc.OracleDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * 数据库配置
 * @author zhanghui
 * @date 2019/5/7
 */
@Configuration
public class DbConfig {

    @Autowired
    private Environment env;

    /**
     * EnableJpaRepositories 注解关联repository与entityManage，entityManage决定数据源
     */
    @EnableJpaRepositories(
            basePackages = "transfer.db1",
            entityManagerFactoryRef = "entityManage1",
            transactionManagerRef = "transactionManager1"
    )
    @Configuration
    class db1{
        @Bean("datasource1")
        @Primary
        public DataSource getDataSource1() {
            return new SimpleDriverDataSource(
                    new OracleDriver(),
                    "jdbc:oracle:thin:@192.168.1.142:1521:ocri",
                    "admin",
                    "123456");
        }

        /**
         * entityManage 在用jpa原生sql 查询时，(非JpaRepository)，注意注入的EntityManage的qualifier值
         * @param dataSource
         * @return
         */
        @Bean("entityManage1")
        @Primary
        public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean1(@Qualifier("datasource1")DataSource  dataSource) {
            return getFactory(dataSource,"transfer.db1.entity","unit1","org.hibernate.dialect.MySQL5InnoDBDialect");
        }


        @Bean("transactionManager1")
        @Primary
        public PlatformTransactionManager transactionManager1(@Qualifier("entityManage1") LocalContainerEntityManagerFactoryBean entityManageFactory) {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManageFactory.getObject());
            return transactionManager;
        }
    }

    @EnableJpaRepositories(
            basePackages = "transfer.db2",
            entityManagerFactoryRef = "entityManage2",
            transactionManagerRef = "transactionManager2"
    )
    @Configuration
    class db2{
        @Bean("datasource2")
        public DataSource getDataSource2() {
            DataSource dataSource = null;
            try {
                dataSource = new SimpleDriverDataSource(
                        new Driver(),
                        "jdbc:mysql://192.168.1.196:3306/bank_3d?useOldAliasMetadataBehavior=true&useSSL=false&zeroDateTimeBehavior=convertToNull",
                        "my_test",
                        "123456");

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return dataSource;
        }

        @Bean("entityManage2")
        public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean2(@Qualifier("datasource2")DataSource  dataSource) {
            return getFactory(dataSource,"transfer.db2.entity2","unit2","org.hibernate.dialect.OracleDialect");
        }

        @Bean("transactionManager2")
        public PlatformTransactionManager transactionManager2(@Qualifier("entityManage2") LocalContainerEntityManagerFactoryBean entityManageFactory) {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManageFactory.getObject());
            return transactionManager;
        }
    }

    private LocalContainerEntityManagerFactoryBean getFactory(DataSource  dataSource, String baseEntityPackage, String unitName, String dialect){
        LocalContainerEntityManagerFactoryBean em
                = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(baseEntityPackage);
        em.setPersistenceUnitName(unitName);

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.ddl-auto", "none");
        properties.put("hibernate.format_sql", "false");
        properties.put("hibernate.dialect", dialect);
//        properties.put("hibernate.default_schema", "BIRD_CCB");
        em.setJpaPropertyMap(properties);

        return em;
    }
}


/* pom.xml

<parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.9.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.0</version>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

<!--  http://www.datanucleus.org/downloads/maven2/oracle/ojdbc6/11.2.0.3/ojdbc6-11.2.0.3.jar      -->
<!--  D:\java\ideaIU-2017.1.3.win\plugins\maven\lib\maven3\bin\mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.2.0.3 -Dpackaging=jar -Dfile=C:\Users\winuser\Downloads\ojdbc6-11.2.0.3.jar -DgeneratePom=true      -->
        <dependency>
            <groupId>com.oracle</groupId>
            <artifactId>ojdbc6</artifactId>
            <version>11.2.0.3</version>
        </dependency>

    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
*/