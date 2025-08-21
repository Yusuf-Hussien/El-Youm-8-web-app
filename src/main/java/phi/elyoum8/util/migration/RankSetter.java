package phi.elyoum8.util.migration;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import phi.elyoum8.config.aspects.TrackExecutionTime;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankSetter {
    private final ExecutorService executor ;
    private final StudentRepository studentRepository;
    private final EntityManager entityManager;

    private final int BATCH_SIZE = 1000;
    @Value("${sheet.size}") Long sheetSize;


    @TrackExecutionTime
    public void assignRanksUsingNativeQuery()
    {
        studentRepository.setRanks();
        entityManager.flush();
        entityManager.clear();
    }


    public void assignRanksUsingMemoryInBatches() {
        int page=0;
        while(true) {
            List<Student> students = studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page++, 100000)).getContent();
            if (students.isEmpty()) break;
            long currentRank = 1;
            long rankWithDuplicates = 1;
            Double previousPercentage = null;

            for (int i = 0; i < students.size(); i++)
            {
                Student student = students.get(i);
                Double currentPercentage = student.getTotalDegree();

                if (previousPercentage != null && !currentPercentage.equals(previousPercentage))  rankWithDuplicates++;

                student.setStudentRank(currentRank++);
                student.setRankWithDuplicates(rankWithDuplicates);

                previousPercentage = currentPercentage;
            }
            studentRepository.saveAll(students);
        }
    }

    public void assignRanksUsingMemoryInBatchesAsync() {
        int page=0;
        while (true)
        {
            List<Student> students = studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page++, 50000)).getContent();
            if (students.isEmpty())break;
            long currentRank = 1;
            long rankWithDuplicates = 1;
            Double previousPercentage = null;

            List<Future<?>> processes = new ArrayList<>();
            List<Student> currentBatch = new ArrayList<>();

            for (int i = 0; i < students.size(); i++)
            {
                Student student = students.get(i);
                Double currentPercentage = student.getPercentage();

                if (previousPercentage != null && !currentPercentage.equals(previousPercentage))  rankWithDuplicates++;

                student.setStudentRank(currentRank++);
                student.setRankWithDuplicates(rankWithDuplicates);

                previousPercentage = currentPercentage;

                currentBatch.add(student);
                if (currentBatch.size() >= BATCH_SIZE) {
                    var tempBatch = new ArrayList<>(currentBatch);
                    processes.add(
                            executor.submit(() -> studentRepository.saveAll(tempBatch))
                    );
                    currentBatch.clear();
                }
            }

            if (!currentBatch.isEmpty()) {
                var tempBatch = new ArrayList<>(currentBatch);
                processes.add(
                        executor.submit(() -> studentRepository.saveAll(tempBatch))
                );
                currentBatch.clear();
            }

            try {
                for (var process : processes) process.get();
            } catch (InterruptedException | ExecutionException e) {}
        }
    }


}

