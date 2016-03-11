
import java.lang.reflect.Method;

import javax.swing.JMenuBar;

import org.apache.pdfbox.PDFReader;

public class CustomPDFReader extends PDFReader {

	/**
	 * 
	 */
	private static final long serialVersionUID = 678451510308887925L;
	
	public CustomPDFReader() {
		super();
	}
	
	public void setCurrentFile(String file) {
		try {
            Method m = getClass().getSuperclass().getDeclaredMethod("openPDFFile", 
            		new Class<?>[]{String.class, String.class});
            m.setAccessible(true);
            m.invoke(this, file, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String []args) {
		CustomPDFReader reader = new CustomPDFReader();
		// to remove menu open another file
		JMenuBar menu = reader.getJMenuBar();
		menu.remove(0);
		// set default opened file
		reader.setCurrentFile("test.pdf");
		reader.setVisible(true);
	}
}
