package jesclient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JESSpoolFile {
	private JESJob jobJES;
	public Integer handle = null;
	public String step;
	public String procedure;
	public String type;
	public String nameDD;
	public Integer byteCount = null;

	public JESSpoolFile(JESJob jobJES) {
		this.jobJES = jobJES;
	}

	public String read() throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		this.jobJES.clientJES.retrieveFile(String.format("%s.%d", this.jobJES.handle, this.handle), outputStream);
		return outputStream.toString();
	}
}
