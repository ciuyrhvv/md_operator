package telecom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class TextReader {
	private FileReader fr;
	private BufferedReader br;
	private String[] items;
	private int formatLength;

	public TextReader(String path) throws FileNotFoundException {
		fr = new FileReader(path);
		br = new BufferedReader(fr);
	}

	public void close() {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (fr != null) {
			try {
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	protected String getItem(int index) {
		String str;
		if ((index >= 0) && (index < items.length)) {
			str = items[index];
		} else {
			str = "";
		}
		return str;
	}

	protected int getItemCount() {
		return items.length;
	}

	public boolean nextLine() throws IOException {
		boolean success = false;

		String thisLine = br.readLine();

		if (thisLine != null) {
			parseLine(thisLine);
			success = true;
		}

		return success;

	}

	protected abstract int parseLine(String line);

	protected void clearItems() {
		this.items = null;
	}

	protected void putItem(int index, String item) {
		if (this.items == null) {
			this.items = new String[this.formatLength];
			this.items[index] = item;
		}
	}
}
