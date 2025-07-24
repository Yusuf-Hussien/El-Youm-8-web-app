package phi.elyoum8.util;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
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
@Profile("migrate")
@RequiredArgsConstructor
public class Migrations {

    private final int BATCH_SIZE = 1000;
    @Value("${sheet.size}") Long sheetSize;

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final StudentRepository studentRepository;
    private final EntityManager entityManager;

    @Transactional
    public void migrateFromCsvToDatabase(String fileName)
    {
        long lineCount =0;
        String line = "";
        List<Future<?>>proccesses = new ArrayList<>();
        boolean isItFirstLine = true;

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
                   proccesses.add(executor.submit(()->{
                       studentRepository.saveAll(tempBatch);
                   }));
                   lineCount+=currentBatch.size();
                   currentBatch.clear();
                   System.out.println(lineCount+" sent to the Database Successfully\n");
               }
           }
           if(!currentBatch.isEmpty())
           {
               var tempBatch = new ArrayList<>(currentBatch);
               proccesses.add(executor.submit(()->{
                   studentRepository.saveAll(tempBatch);
               }));
               lineCount+=currentBatch.size();
               currentBatch.clear();
               System.out.println(lineCount+" sent to the Database Successfully\n");
               log.info("====All Records are sent to The Database and are being inserted right now!====");
           }

           for(Future<?>proccess : proccesses)
           {
               proccess.get();
               log.info("{} rows are inserted in the Database",studentRepository.countStudent());
           }
           log.info("--------All Records inserted Successfully-----------");
           Thread ranksThread = new Thread(()->assignRanksInMemmoryAsync());

           //while (!studentRepository.areAllInserted(sheetSize))
            while (studentRepository.countStudent()!=sheetSize)
           {
               try{
                   log.warn("Waiting to set Ranks, because still inserting rows in the database!!");
                   ranksThread.sleep(60*1000);
               }catch(InterruptedException e){}
           }
            long startTime = System.currentTimeMillis()/1000;
            log.info("Ranks are being setting right now!");
           ranksThread.start();
           try{
               ranksThread.join();
           }catch(InterruptedException e){log.error("ranks thread interrupted!!");}
           long endTime = System.currentTimeMillis()/1000;
           log.info("-----Ranks updated successfully in {} seconds!------",endTime-startTime);

        }catch (IOException ex){
            log.error("Error reading file --> " + fileName);
        }catch (InterruptedException  | ExecutionException ex){
            log.error("Error Handling The Async Process!");
        }finally {
        executor.shutdown();
        }

    }


    public void assignRanks()
    {
        String query = """
            UPDATE student s
            JOIN (
                SELECT seat_number,
                       RANK() OVER (ORDER BY total_degree DESC, arabic_name ASC) AS rank_with_duplicates ,
                       ROW_NUMBER() OVER (ORDER BY total_degree DESC, arabic_name ASC) AS student_rank
                FROM student
            ) r ON s.seat_number = r.seat_number
            SET s.student_rank = r.student_rank,
                s.rank_with_duplicates = r.rank_with_duplicates;
        """;
        entityManager.createNativeQuery(query).executeUpdate();
    }

    @Transactional
    public void assignRanksInBatch()
    {
        long maxSeatNumber =((Number) entityManager.createNativeQuery("SELECT MAX(s.seat_number) FROM student AS s;").getSingleResult()).longValue();
        for(long startSeatNumber=0; startSeatNumber<=maxSeatNumber; startSeatNumber+= BATCH_SIZE)
        {
            studentRepository.setRanks(startSeatNumber,startSeatNumber+BATCH_SIZE);
            entityManager.flush();
            entityManager.clear();
        }
    }


    public void assignRanksInMemmory() {
        log.info("Assigning Ranks......");
        List<Student> students = studentRepository.findAllByOrderByTotalDegreeDescArabicNameAsc(PageRequest.of(0,10));
        log.info("Data Fetched.....");
        long currentRank = 1;
        long rankWithDuplicates = 1;
        Double previousDegree = null;
        long sameDegreeCount = 0;


        for (int i = 0; i < students.size(); i++)
        {
            Student student = students.get(i);
            Double currentDegree = student.getTotalDegree();

            if (previousDegree != null && currentDegree.equals(previousDegree)) {
                student.setStudentRank(currentRank);
                student.setRankWithDuplicates(rankWithDuplicates);
                sameDegreeCount++;
            } else {
                currentRank = i + 1 - sameDegreeCount;
                student.setStudentRank(currentRank);
                student.setRankWithDuplicates(rankWithDuplicates);
                sameDegreeCount = 0;
            }
            previousDegree = currentDegree;
            rankWithDuplicates++;
        }
        log.info("Ranks modified.....");
        studentRepository.saveAll(students);
        log.info("Ranks updated in the Database Successfully!");
    }

    public void assignRanksInMemmoryAsync() {
        log.info("Assigning Ranks......");
        List<Student> students = studentRepository.findAllByOrderByTotalDegreeDescArabicNameAsc(PageRequest.of(0,10));
        log.info("Data Fetched.....");
        long currentRank = 1;
        long rankWithDuplicates = 1;
        Double previousDegree = null;
        long sameDegreeCount = 0;

        List<Future<?>> processes = new ArrayList<>();
        List<Student>currentBatch = new ArrayList<>();

        for (int i = 0; i < students.size(); i++)
        {
            Student student = students.get(i);
            Double currentDegree = student.getTotalDegree();

            if (previousDegree != null && currentDegree.equals(previousDegree)) {
                student.setStudentRank(currentRank);
                student.setRankWithDuplicates(rankWithDuplicates);
                sameDegreeCount++;
            } else {
                rankWithDuplicates = i + 1 - sameDegreeCount;
                student.setStudentRank(currentRank);
                student.setRankWithDuplicates(rankWithDuplicates);
                sameDegreeCount = 0;
            }
            previousDegree = currentDegree;
            currentRank++;

            currentBatch.add(student);
            if(currentBatch.size()>=BATCH_SIZE)
            {
                var tempBatch = new ArrayList<>(currentBatch);
                processes.add(
                        executor.submit(()->studentRepository.saveAll(tempBatch))
                );
                currentBatch.clear();
                log.info("{} Student's rank updated in yhe Database",i);
            }
        }
        if(!currentBatch.isEmpty())
        {
            var tempBatch = new ArrayList<>(currentBatch);
            processes.add(
                    executor.submit(()->studentRepository.saveAll(tempBatch))
            );
            currentBatch.clear();
            log.info("All Student's rank updated in yhe Database");
        }
        log.info("Ranks modified and sent to the Database!");
        try{
        for (var process : processes) process.get();
        }catch (InterruptedException | ExecutionException e){}
        log.info("Ranks updated in the Database Successfully!");
    }



    public void migrateFromExcelToDatabase(String fileName)
    {
        long lineCount = 0;
        List<Future<?>> processes = new ArrayList<>();
        List<Student> currentBatch = new ArrayList<>();

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) throw new IOException("Resource not found: " + fileName);

            IOUtils.setByteArrayMaxOverride(150_000_000); // Override for 131.5 MB file
            try (XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
                 SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWorkbook, 100)) {
                XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
                boolean isItHeader = true;

                for (Row row : sheet)
                {
                    if (isItHeader)
                    {
                        isItHeader = false;
                        continue;
                    }
                    Student student = new Student();
                    try {
                        System.out.println(row.toString() + " row\n");
                        if (row.getCell(0) == null || row.getCell(0).getCellType() != CellType.NUMERIC) continue;
                        if (row.getCell(1) == null || row.getCell(1).getCellType() != CellType.STRING) continue;
                        if (row.getCell(2) == null || row.getCell(2).getCellType() != CellType.NUMERIC) continue;

                        student.setSeatNumber((long) row.getCell(0).getNumericCellValue());
                        student.setArabicName(row.getCell(1).getStringCellValue().trim());
                        student.setTotalDegree(row.getCell(2).getNumericCellValue());

                        currentBatch.add(student);

                        if (currentBatch.size() >= BATCH_SIZE)
                        {
                            var tempBatch = new ArrayList<>(currentBatch);
                            processes.add(executor.submit(() -> {
                                studentRepository.saveAll(tempBatch);
                            }));
                            lineCount += currentBatch.size();
                            currentBatch.clear();
                            System.out.println(lineCount + " inserted to the Database Successfully\n");
                        }
                    } catch (Exception e) {
                        log.warn("Skipping row  name is {} due to parsing error: {}", student.getArabicName(), e.getMessage());
                    }
                }
                if (!currentBatch.isEmpty())
                {
                    int finalBatchSize = currentBatch.size();
                    processes.add(executor.submit(() -> {
                        studentRepository.saveAll(currentBatch);
                    }));
                    lineCount += finalBatchSize;
                    currentBatch.clear();
                    System.out.println(lineCount + " inserted to the Database Successfully\n====Mission Completed Successfully!====");
                }
                for (Future<?> process : processes) process.get();
            }
        } catch (IOException ex) {
            log.error("Error reading file --> " + fileName, ex);
        } catch (InterruptedException | ExecutionException ex) {
            log.error("Error Handling The Async Process!", ex);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

}
