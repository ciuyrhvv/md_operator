package telecom;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private File file;
	private FileWriter fw;
	private BufferedWriter bw;
	private boolean printToConsole;
	private String allMessages;
	//String dateFormat;
	private String date;
	public int errCount;
	public int wrnCount;

	public Logger() throws IOException {
		printToConsole = true;
		String dateFormat = "dd.MM.yyyy;HH:mm:ss.SSS";

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
		date = formatter.format(new Date());

		file = new File("messages.log");

		if (!file.exists())
			file.createNewFile();

		fw = new FileWriter(file.getName(), true);
		bw = new BufferedWriter(fw);
		
		this.allMessages = "";
		
		this.errCount = 0;

	}

	public void close() {
		if (bw != null) {
			try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (fw != null)
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public String getAllMessages() {
		return this.allMessages;
	}

	public void add(String mType, String mess) throws IOException {

		String message;
		switch (mType) {
		case "error":
			message = "[ERROR]" + " " + mess; this.errCount++;
			break;
		case "info":
			message = "[INFO]" + " " + mess;
			break;
		case "warning":
			message = "[WARNING]" + " " + mess;
			break;
		default:
			message = "[UNKNOWN]" + " " + mess;
			break;
		}

		message = "[" + date + "]" + message + "\n";

		allMessages = allMessages + message;

		if (this.printToConsole)
			System.out.print(message);
		
		bw.write(message);
		bw.flush();
	}
}
