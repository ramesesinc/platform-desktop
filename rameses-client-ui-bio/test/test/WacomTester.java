/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.florentis.signature.DynamicCapture;
import com.florentis.signature.SigCtl;
import com.florentis.signature.SigObj;
import com.rameses.io.IOStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import junit.framework.TestCase;

/**
 *
 * @author rameses
 */
public class WacomTester extends TestCase {

    public WacomTester(String testName) {
        super(testName);
    }

    public void testMain() throws Exception {
        SigCtl sigCtl = new SigCtl();
        DynamicCapture dc = new DynamicCapture();
        int rescap = dc.capture(sigCtl, "Approving Officer", "Juan Dela Cruz", null, null);
        switch (rescap) {
            case 0:
                SigObj sig = sigCtl.signature();
                sig.extraData("SignatoryID", "JDC");
                sig.extraData("SignatoryName", "JUAN DELA CRUZ");
                int flags = SigObj.outputBinary | SigObj.color32BPP | SigObj.encodeData;
                Object result = sig.renderBitmap(null, 300, 150, "image/png", 0.5f, 0xff0000, 0xffffff, 0.0f, 0.0f, flags);
                save( result );
                break;

            case 1:
                System.out.println("Cancelled");
                break;
            case 100:
                System.out.println("Signature tablet not found");
                break;
            case 103:
                System.out.println("Capture not licensed");
                break;
        }
    }
    
    private void save( Object data ) throws Exception {
        byte[] bytes = (byte[]) data; 
        File file = new File("sig.png");
        System.out.println( file.getAbsolutePath() );
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        FileOutputStream fos = new FileOutputStream( file );
        IOStream.write( bais, fos );
    }
}
