package phi.elyoum8.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import phi.elyoum8.model.Student;
import phi.elyoum8.repository.StudentRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {
    private static final Map<Character, List<Character>> ARABIC_CHAR_VARIATIONS = new HashMap<>();
    static {
        ARABIC_CHAR_VARIATIONS.put('ي', Arrays.asList('ى'));
        ARABIC_CHAR_VARIATIONS.put('ى', Arrays.asList('ي'));
        ARABIC_CHAR_VARIATIONS.put('ة', Arrays.asList('ه'));
        ARABIC_CHAR_VARIATIONS.put('ه', Arrays.asList('ة'));
        ARABIC_CHAR_VARIATIONS.put('ا', Arrays.asList('أ','إ'));
        ARABIC_CHAR_VARIATIONS.put('أ', Arrays.asList('ا'));
        ARABIC_CHAR_VARIATIONS.put('إ', Arrays.asList('ا'));
    }

    private final StudentRepository studentRepository;


    public Student findBySeatNumber(Long seatNumber) {
        return studentRepository.findBySeatNumber(seatNumber);
    }

    public List<Student> findByName(String name) {
        return studentRepository.findAllStudentsByNameStartsWith(name);
    }

    public List<Student> findByNameIgnoreSpillCheck(String name) {
        List<String>variations = generateNameVariations(name);
        List<Student>results = new ArrayList<>();
        for (String variation : variations)
            results.addAll(studentRepository.findAllStudentsByNameStartsWith(variation));
        return results.stream().distinct().collect(Collectors.toList());
    }

    private List<String> generateNameVariations(String name)
    {
        List<String> variations = new ArrayList<>();
        variations.add(name);

        for(int i=0 ; i<name.length() ; i++)
        {
            char ch = name.charAt(i);
            if(
                    ARABIC_CHAR_VARIATIONS.containsKey(ch) &&
                    (
                            ((ch=='ي'||ch=='ى'||ch=='ه'||ch=='ة')&& (i==name.length()-1||name.charAt(i+1)==' '))
                            ||((ch=='ا'||ch=='أ'||ch=='إ')&& (i!=name.length()-1&&name.charAt(i+1)!=' '))
                    )
            )
            {
                List<String>tempVariations = new ArrayList<>();
                for(String variation:variations)
                {
                    for(char replace : ARABIC_CHAR_VARIATIONS.get(ch))
                    {
                        String newVariation = variation.substring(0, i) + replace + variation.substring(i + 1);
                        tempVariations.add(newVariation);
                    }
                }
                variations.addAll(tempVariations);
            }

        }
        //System.out.println(variations.stream().distinct().collect(Collectors.toList()));
        return variations.stream().distinct().collect(Collectors.toList());
    }


    public List<Student> findBySeatNumberRange(Long startSeat, Long endSeat) {
        return studentRepository.findAllBetween(Math.min(startSeat,endSeat),Math.max(startSeat,endSeat));
    }

    public Page<Student> findAll(Integer page, Integer size) {
        return studentRepository.findAllByOrderByPercentageDescArabicNameAsc(PageRequest.of(page,size));
    }
}