package phi.elyoum8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import phi.elyoum8.repository.StudentRepository;
import phi.elyoum8.util.Migrations;

@SpringBootApplication
public class ElYoum8Application {

    public static void main(String[] args) {
        var context = SpringApplication.run(ElYoum8Application.class, args);
        var migerations = context.getBean(Migrations.class);
        var studentRepository = context.getBean(StudentRepository.class);
        var environment = context.getEnvironment();
        if(studentRepository.countStudent()!=Long.parseLong(environment.getProperty("sheet.size")))
        {
        long startTimeInSeconds = System.currentTimeMillis()/1000;
            studentRepository.deleteAllStudents();
            migerations.migrateFromCsvToDatabase("natega_2025_arabic.csv");
        long endTimeInSeconds = System.currentTimeMillis()/1000;
        System.out.println("Process done in " + (endTimeInSeconds - startTimeInSeconds) + " seconds");
        }
        if(studentRepository.getMinRank()==null)
            migerations.assignRanksUsingNativeQuery();
    }



}
