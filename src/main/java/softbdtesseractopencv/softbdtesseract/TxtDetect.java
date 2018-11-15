package softbdtesseractopencv.softbdtesseract;

import net.sourceforge.tess4j.Tesseract;

public class TxtDetect extends Tesseract {
    public TxtDetect() {
        super();
        setDatapath("tessdata");
//        setLanguage("ben");
    }
}
