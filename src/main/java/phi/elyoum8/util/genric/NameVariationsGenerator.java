package phi.elyoum8.util.genric;

import java.util.*;
import java.util.stream.Collectors;

public class NameVariationsGenerator {
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

    public static List<String> generateNameVariations(String name)
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
}
