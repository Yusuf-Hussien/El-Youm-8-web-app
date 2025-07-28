package phi.elyoum8.util.migration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataMigrator {

    private final StudentRepository studentRepository;
    private final RankSetter rankSetter;
    private final ExecutorService executor ;

    private final int BATCH_SIZE = 1000;
    @Value("${sheet.size}") Long sheetSize;



    @Transactional
    public void migrateFromCsvToDatabase(String fileName)
    {
        long lineCount =0;
        String line = "";
        List<Future<?>>processes = new ArrayList<>();
        boolean isItFirstLine = true;

        long dataMigrateStartTime = System.currentTimeMillis()/1000;
        try(BufferedReader  reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName), StandardCharsets.UTF_8)
        )){
           List<Student> currentBatch = new ArrayList<>();
           while ((line = reader.readLine()) != null)
           {
               String []values = line.split(";");
               if (values.length < 3 || isItFirstLine)
               {
                   isItFirstLine=false;
                   continue;
               }
               Student student = new Student();
               student.setSeatNumber(Long.parseLong(values[0]));
               student.setArabicName(values[1]);
               student.setTotalDegree(Double.parseDouble(values[2]));

               currentBatch.add(student);
               if(currentBatch.size()>=BATCH_SIZE)
               {
                   var tempBatch = new ArrayList<>(currentBatch);
                   processes.add(executor.submit(()->{
                       studentRepository.saveAll(tempBatch);
                   }));
                   lineCount+=currentBatch.size();
                   currentBatch.clear();
               }
           }
           if(!currentBatch.isEmpty())
           {
               var tempBatch = new ArrayList<>(currentBatch);
               processes.add(executor.submit(()->{
                   studentRepository.saveAll(tempBatch);
               }));
               lineCount+=currentBatch.size();
               currentBatch.clear();
               log.info("{} Record was sent to the Database Successfully and they are being inserted right now",lineCount);
           }

           for(Future<?>process : processes)  process.get();

            long dataMigrateEndTime = System.currentTimeMillis()/1000;
            log.info("-----All Records inserted Successfully in {} seconds------",dataMigrateEndTime-dataMigrateStartTime);

            long rankStartTime = System.currentTimeMillis()/1000;
            log.info("Ranks are being setting right now!");
            rankSetter.assignRanksUsingNativeQuery();
            long rankEndTime = System.currentTimeMillis()/1000;
            log.info("-----Ranks updated successfully in {} seconds------",rankEndTime-rankStartTime);

        }catch (IOException ex){
            log.error("Error reading file --> " + fileName);
        }catch (InterruptedException  | ExecutionException ex){
            log.error("Error Handling The Async Process!");
        }
    }


}
