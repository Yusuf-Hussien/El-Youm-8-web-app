package phi.elyoum8.util.generator;

import org.junit.jupiter.api.Test;
import phi.elyoum8.util.genric.ArabicNameNormalizer;

class ArabicNameNormalizerTest {

    @Test
    void should_normalize_arabic() {
        String input= "حمزة أحمد علي عبدالقادر";
        System.out.println("Before: "+input);
        String output =  ArabicNameNormalizer.normalize(input);
        System.out.println("After: "+output);
    }
}