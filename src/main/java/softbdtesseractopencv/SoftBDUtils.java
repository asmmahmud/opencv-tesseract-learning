package softbdtesseractopencv;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SoftBDUtils {
    
    public static String getNationalID(String str) {
        String nId = "";
        Pattern p = Pattern.compile("I?D?(\\s+)?NO:\\s+(\\d{17}|\\d{13})");
        Matcher m = p.matcher(str);
        if (m.find()) {
            // we're only looking for one group, so get it
            nId = m.group(2);
            
            // print the group out for verification
//            System.out.format("Found : '%s'\n'%s'\n", m.group(0), nId);
        }
        return nId;
    }
}
