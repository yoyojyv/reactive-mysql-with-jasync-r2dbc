package me.jerry.example.rx;

import com.github.jasync.r2dbc.mysql.JasyncConnectionFactory;
import com.github.jasync.sql.db.mysql.pool.MySQLConnectionFactory;
import com.github.jasync.sql.db.mysql.util.URLParser;
import io.r2dbc.spi.ConnectionFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootApplication
public class ReactiveMysqlWithJasyncR2dbcApplication {

    public static void main(String[] args) {
        System.setProperty("spring.main.lazy-initialization", "true");
        SpringApplication.run(ReactiveMysqlWithJasyncR2dbcApplication.class, args);
    }

    @Bean
    ApplicationRunner run(OrderRepository repository) {
        return args -> repository.findAll().subscribe(it -> log.info("{}", it));
    }

}


@Table("orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
class Order {

    @Id
    private Long id;
    private String name;

}

interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

}

@Configuration
@EnableR2dbcRepositories
class MysqlApplicationConfiguration extends AbstractR2dbcConfiguration {

    @Override
    public ConnectionFactory connectionFactory() {
        String url = "mysql://test:test@127.0.0.1:3306/test";
        return new JasyncConnectionFactory(new MySQLConnectionFactory(URLParser.INSTANCE.parseOrDie(url, StandardCharsets.UTF_8)));
    }

}
