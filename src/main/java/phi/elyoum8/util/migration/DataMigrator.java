package phi.elyoum8.util.migration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import phi.elyoum8.config.aspects.TrackExecutionTime;
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
    @TrackExecutionTime
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

               if (!studentRepository.existsBySeatNumber(student.getSeatNumber())) currentBatch.add(student);
               else log.debug("Skipping duplicate student with seatNumber: {}", student.getSeatNumber());


               if(currentBatch.size()>=BATCH_SIZE)
               {
                   var tempBatch = new ArrayList<>(currentBatch);
                   /*processes.add(executor.submit(()->{
                       studentRepository.saveAll(tempBatch);
                   }));*/
                   processes.add(submitBatchWithRetry(tempBatch));
                   lineCount+=currentBatch.size();
                   currentBatch.clear();
               }
           }
           if(!currentBatch.isEmpty())
           {
               var tempBatch = new ArrayList<>(currentBatch);
               processes.add(submitBatchWithRetry(tempBatch));
               lineCount+=currentBatch.size();
               currentBatch.clear();
               log.info("{} Record was sent to the Database Successfully and they are being inserted right now",lineCount);
           }

           for(Future<?>process : processes)
           {
               try {
                   process.get();
               }catch (InterruptedException e) {
                   log.error("Thread interrupted during batch processing: {}", e.getMessage());
                   Thread.currentThread().interrupt();
                   throw new RuntimeException("Migration interrupted", e);
               } catch (Exception e) {
                   log.error("Error completing batch: {}", e.getMessage());
                   throw new RuntimeException("Batch processing failed", e);
               }
           }

            long dataMigrateEndTime = System.currentTimeMillis()/1000;
            log.info("-----All Records inserted Successfully in {} seconds------",dataMigrateEndTime-dataMigrateStartTime);

            long rankStartTime = System.currentTimeMillis()/1000;
            log.info("Ranks are being setting right now!");
            assignRanksWithRetry();
            long rankEndTime = System.currentTimeMillis()/1000;
            log.info("-----Ranks updated successfully in {} seconds------",rankEndTime-rankStartTime);

        }catch (IOException ex){
            log.error("Error reading file--> {}: {}", fileName, ex.getMessage());
            throw new RuntimeException("Failed to read CSV file", ex);
        }
    }


    @Retryable(value = {DataAccessException.class},maxAttempts = 5,backoff = @Backoff(delay = 7*1000, multiplier = 2))
    private void saveBatch(List<Student> students)
    {
        studentRepository.saveAll(students);
    }

    @Retryable(value = {DataAccessException.class},maxAttempts = 5,backoff = @Backoff(delay = 7*1000, multiplier = 2))
    private void assignRanksWithRetry()
    {
        rankSetter.assignRanksUsingNativeQuery();
    }

    private Future<?>submitBatchWithRetry(List<Student> students)
    {
        return executor.submit(()->{
            try {
                saveBatch(students);
                log.info("Successfully saved batch of {} students", students.size());
            }catch (Exception ex){
                log.error("Error saving batch: {}", ex. getMessage());
                throw ex;
            }
        });
    }

}
