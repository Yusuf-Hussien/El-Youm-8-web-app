package phi.elyoum8.util.genric;

import java.util.Map;
import java.util.Set;

public class ArabicNameNormalizer {

    private static final Map<Character, Character> REPLACEMENTS = Map.of(
            '\u0624', '\u0648', // Waw Hamza Above → Waw
            '\u0629', '\u0647', // Ta Marbuta → Ha
            '\u064A', '\u0649', // Ya → Alif Maksura
            '\u0626', '\u0649', // Ya Hamza Above → Alif Maksura
            '\u0622', '\u0627', // Alif Madda Above → Alif
            '\u0623', '\u0627', // Alif Hamza Above → Alif
            '\u0625', '\u0627'  // Alif Hamza Below → Alif
    );

    private static final Set<Character> REMOVALS = Set.of(
            '\u0610','\u0611','\u0612','\u0613','\u0614',
            '\u0615','\u0616','\u0617','\u0618','\u0619','\u061A',
            '\u06D6','\u06D7','\u06D8','\u06D9','\u06DA','\u06DB','\u06DC','\u06DD','\u06DE',
            '\u06DF','\u06E0','\u06E1','\u06E2','\u06E3','\u06E4','\u06E5','\u06E6','\u06E7','\u06E8',
            '\u06E9','\u06EA','\u06EB','\u06EC','\u06ED',
            '\u0640','\u064B','\u064C','\u064D','\u064E','\u064F','\u0650','\u0651','\u0652',
            '\u0653','\u0654','\u0655','\u0656','\u0657','\u0658','\u0659','\u065A','\u065B','\u065C',
            '\u065D','\u065E','\u065F','\u0670'
    );

    public static String normalize(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray())
        {
            if (REMOVALS.contains(c)) continue;
            sb.append(REPLACEMENTS.getOrDefault(c, c));
        }
        return sb.toString();
    }

}