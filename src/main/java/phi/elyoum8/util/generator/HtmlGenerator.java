package phi.elyoum8.util.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import phi.elyoum8.model.Student;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtmlGenerator {

    private static final String DIRECTORY_NAME = "responseTemplates/";
    private static final String STUDENT_EXPORTING_TEMPLATE_NAME = DIRECTORY_NAME+"student_for_exporting_template";
    private static final String STUDENT_CONVERTING_TEMPLATE_NAME = DIRECTORY_NAME+"student_for_converting_template";
    private static final String STUDENTS_EXPORTING_TEMPLATE_NAME = DIRECTORY_NAME+"students_for_exporting_template";
    private static final String STUDENTS_CONVERTING_TEMPLATE_NAME = DIRECTORY_NAME+"students_for_converting_template";
    private final SpringTemplateEngine templateEngine;

    private Context warpData(Student student, StudentTemplateFields templateFields)
    {
        Context context = new Context();
        context.setVariable("student", student);
        if(templateFields != null)
            context.setVariable("fields", templateFields);
        return context;
    }

    private Context warpData(String objectName,Object object, StudentTemplateFields templateFields)
    {
        Context context = new Context();
        context.setVariable(objectName, object);
        if(templateFields != null)
            context.setVariable("fields", templateFields);
        return context;
    }


    public String generateStudentHtml(Student student, boolean forExporting)
    {
        Context context;
        if(!forExporting){
        student.setArabicName(shapeArabicText(student.getArabicName()));
        StudentTemplateFields fields =  StudentTemplateFields.builder()
                .title(shapeArabicText("نتيجة الطالب"))
                .seatNumberField(shapeArabicText("رقم الجلوس:"))
                .arabicNameField(shapeArabicText("الاسم:"))
                .totalDegreeField(shapeArabicText("المجموع:"))
                .percentageField(shapeArabicText("النسبة:"))
                .rankField(shapeArabicText("الترتيب:"))
                .rankWithDuplicatesField(shapeArabicText("الترتيب مكرر:"))
                .footerMessage(shapeArabicText("تم إنشاء هذا المستند بواسطة اليوم الثامن"))
                .build();
        context = warpData("student",student,fields);
        }
        else context = warpData("student",student,null);
        return forExporting?
                templateEngine.process(STUDENT_EXPORTING_TEMPLATE_NAME,context):
                templateEngine.process(STUDENT_CONVERTING_TEMPLATE_NAME,context);
    }

    public String generateStudentsHtml(List<Student> students, boolean forExporting)
    {
        Context context;
        if(!forExporting){
            students.forEach(student -> {
                student.setArabicName(shapeArabicText(student.getArabicName()));
            });
            StudentTemplateFields fields =  StudentTemplateFields.builder()
                    .title(shapeArabicText("نتيجة البحث"))
                    .seatNumberField(shapeArabicText("رقم الجلوس"))
                    .arabicNameField(shapeArabicText("الاسم"))
                    .totalDegreeField(shapeArabicText("المجموع"))
                    .percentageField(shapeArabicText("النسبة"))
                    .rankField(shapeArabicText("الترتيب"))
                    .rankWithDuplicatesField(shapeArabicText("الترتيب مكرر"))
                    .footerMessage(shapeArabicText("تم إنشاء هذا المستند بواسطة اليوم الثامن"))
                    .build();

            context = warpData("students",students,fields);
        }
        else context = warpData("students",students,null);
        return forExporting?
                templateEngine.process(STUDENTS_EXPORTING_TEMPLATE_NAME,context):
                templateEngine.process(STUDENTS_CONVERTING_TEMPLATE_NAME,context);
    }

    private String shapeArabicText(String input) {
        try {
            // Shape Arabic characters (connect letters)
            ArabicShaping arabicShaping = new ArabicShaping(ArabicShaping.LETTERS_SHAPE);
            String shaped = arabicShaping.shape(input);

            // Apply BiDi algorithm for RTL order
            Bidi bidi = new Bidi(shaped, Bidi.DIRECTION_RIGHT_TO_LEFT);
            return bidi.writeReordered(Bidi.DO_MIRRORING);
        } catch (ArabicShapingException e) {
            log.warn("Arabic shaping failed, falling back to raw text", e);
            return input;
        }
    }


    @Deprecated
    public static String generateFileName(String seatNumber,String extention)
    {
        return String.format("student_%s.%s", seatNumber, extention);
    }
}
