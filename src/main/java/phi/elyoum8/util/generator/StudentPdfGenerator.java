package phi.elyoum8.util.generator;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import phi.elyoum8.model.Student;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentPdfGenerator {

    private final HtmlGenerator htmlGenerator;

    public byte[] generatePdf(Student student) {
        try {
            String html = htmlGenerator.generateStudentHtml(student, false);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating PDF for student ID: {}: {}", student.getSeatNumber(), e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF for student ID: " + student.getSeatNumber(), e);
        }
    }

    public byte[] generatePdf(List<Student> students) {
        try {
            String html = htmlGenerator.generateStudentsHtml(students, false);
            return convertHtmlToPdf(html);
        } catch (Exception e) {
            log.error("Error generating PDF : {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF for students begins with ID: " + students.getFirst().getSeatNumber(), e);
        }
    }

    private byte[] convertHtmlToPdf(String htmlContent) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // Create converter properties for Arabic support
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = createArabicFontProvider();
            converterProperties.setFontProvider(fontProvider);


            // Optimize HTML for PDF generation while keeping your beautiful styling
            String enhancedHtml = htmlContent;//optimizeHtmlForPdf(htmlContent);

            // Convert HTML to PDF
            HtmlConverter.convertToPdf(enhancedHtml, outputStream, converterProperties);

            log.info("Successfully generated PDF with Arabic content for student");
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error generating PDF from HTML", e);
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }


    /**
     * Creates a font provider that supports Arabic text
     */
    private FontProvider createArabicFontProvider() {
        FontProvider fontProvider = new DefaultFontProvider(true, true, true);

        try {
            // Add Arabic fonts
            addArabicFonts(fontProvider);
        } catch (Exception e) {
            log.warn("Could not load custom Arabic fonts, using default fonts", e);
        }

        return fontProvider;
    }

    /**
     * Adds Arabic-compatible fonts to the font provider
     * Updated to handle your specific font needs
     */
    private void addArabicFonts(FontProvider fontProvider) {
        boolean fontLoaded = false;

        try {
            // Priority order: Resource fonts first, then system fonts
            String[] arabicFonts = {
                    "fonts/NotoSansArabic-Regular.ttf",     // Your project resources
                    "fonts/NotoSansArabic-Bold.ttf",        // Bold variant
                    "fonts/Amiri-Regular.ttf",              // Alternative Arabic font
                    "fonts/Tahoma.ttf"                      // Tahoma from resources
            };

            // Try resource fonts first
            for (String fontPath : arabicFonts) {
                try {
                    InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontPath);
                    if (fontStream != null) {
                        byte[] fontBytes = fontStream.readAllBytes();
                        FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);
                        fontProvider.addFont(fontProgram);
                        log.info("Successfully loaded Arabic font from resources: {}", fontPath);
                        fontLoaded = true;
                        fontStream.close();
                    }
                } catch (Exception e) {
                    log.debug("Could not load resource font: {} - {}", fontPath, e.getMessage());
                }
            }

            // If no resource fonts loaded, try system fonts
            if (!fontLoaded) {
                String[] systemFonts = {
                        "/System/Library/Fonts/GeezaPro.ttc",     // macOS
                        "/System/Library/Fonts/Tahoma.ttc",       // macOS Tahoma
                        "C:/Windows/Fonts/tahoma.ttf",            // Windows
                        "C:/Windows/Fonts/arial.ttf",             // Windows fallback
                        "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf" // Linux
                };

                for (String fontPath : systemFonts) {
                    try {
                        FontProgram fontProgram = FontProgramFactory.createFont(fontPath);
                        fontProvider.addFont(fontProgram);
                        log.info("Successfully loaded system Arabic font: {}", fontPath);
                        fontLoaded = true;
                        break;
                    } catch (Exception e) {
                        log.debug("Could not load system font: {} - {}", fontPath, e.getMessage());
                    }
                }
            }

            if (!fontLoaded) {
                log.warn("No custom Arabic fonts loaded - will use iText default fonts");
            }

        } catch (Exception e) {
            log.error("Error in Arabic font loading process", e);
        }
    }
}