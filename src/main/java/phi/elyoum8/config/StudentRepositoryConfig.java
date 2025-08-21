package phi.elyoum8.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentMysqlRepository;
import phi.elyoum8.repository.StudentPostgresRepository;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.service.StudentMvcService;
import phi.elyoum8.service.StudentRestService;
import phi.elyoum8.service.StudentService;

import java.util.List;

//@Configuration
public class StudentRepositoryConfig {


    //@Bean
    public StudentRepository studentRepository(@Value("${user.database.name}") String databaseName,
                                               @Qualifier("mysql") StudentMysqlRepository mysqlRepository,
                                               @Qualifier("postgres") StudentPostgresRepository postgresRepository
    ) {
        StudentRepository repo;
        if ("mysql".equalsIgnoreCase(databaseName)) {
            System.out.println("Using MySQL");
            repo = mysqlRepository;
        } else if ("postgres".equalsIgnoreCase(databaseName)) {
            System.out.println("Using Postgres");
            repo = postgresRepository;
        } else {
            throw new IllegalArgumentException("Unsupported database: " + databaseName);
        }
        return mysqlRepository;
    }


}