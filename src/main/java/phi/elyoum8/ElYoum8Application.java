package phi.elyoum8;

import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.util.migration.DataMigrator;
import phi.elyoum8.util.migration.RankSetter;

@SpringBootApplication
public class ElYoum8Application {

    public static void main(String[] args) {
    try{
        var context = SpringApplication.run(ElYoum8Application.class, args);

        var dataMigrator = context.getBean(DataMigrator.class);
        var rankSetter = context.getBean(RankSetter.class);
        var studentRepository = context.getBean(StudentRepository.class);
        var logger = LoggerFactory.getLogger(ElYoum8Application.class);
        var environment = context.getEnvironment();

        if (studentRepository.countStudent() != Long.parseLong(environment.getProperty("sheet.size")))
        {
            long startTimeInSeconds = System.currentTimeMillis() / 1000;
            studentRepository.deleteAllStudents();
            dataMigrator.migrateFromCsvToDatabase("natega_2025.csv");
            long endTimeInSeconds = System.currentTimeMillis() / 1000;
            logger.info("Data was migrated And ranks were set successfully in {} seconds", endTimeInSeconds - startTimeInSeconds);
            logger.info("Now The App is Ready To Be Used :)");
        }

        if (studentRepository.getMinRank() == null)
            rankSetter.assignRanksUsingNativeQuery();
    } catch (JDBCConnectionException e) {
        throw new RuntimeException("JDBC Connection Error, Please check your database connection and try again!");
    } catch (Exception e) {
        System.err.println("Failed to start application: " + e.getMessage());
        System.exit(1);
    }
}
}
