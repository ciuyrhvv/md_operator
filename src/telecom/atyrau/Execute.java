package telecom.atyrau;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Execute {

	private String mode;
	private Logger log;
	private IniFile ini;
	private MyFTP f;
	protected String fileMask;
	protected String yyyyMMdd;
	protected String yyyyMMddHHmmssS;
	protected int daysBack;
	protected String localDir;

	public Execute(String mode) throws IOException {
		this.log = new Logger();
		this.log.add("info", "Starting..");
		this.ini = new IniFile("md_operator.conf");
		this.f = null;
		this.fileMask = null;
		this.daysBack = Integer.parseInt(ini.getString("settings", "days_back",
				"-1"));
		this.yyyyMMddHHmmssS = getWorkDate(true, 0);
		this.yyyyMMdd = this.yyyyMMddHHmmssS.substring(0, 8);
		this.mode = mode;

		String slash = System.getProperty("file.separator");
		this.localDir = ini.getString("settings", "local_dir", "qqq");
		if (!localDir.endsWith(slash))
			localDir += slash + this.mode + slash + this.yyyyMMddHHmmssS
					+ slash;
	}

	public void close() {
		this.log.close();
	}

	protected void start() throws Exception {
		try {
			downloadFiles();
			uploadFilesToArch();
			uploadFilesToMD();
			// (new Mailer(ini.getString("email","to",""),
			// ini.getString("email","from",""),
			// ini.getString("email","host",""),
			// ini.getString("email","subject",""),
			// this.log.getAllMessages())).send();
		} catch (Exception ex) {
			this.log.add("error", "Error!");
			throw ex;
		}
	}

	private void clearFolder(String localDir) {
		File dir = new File(localDir);
		if (!dir.exists())
			dir.mkdir();
		File[] files = dir.listFiles();
		for (File file : files)
			file.delete();
	}

	private ArrayList<String> getFilesDB() throws IOException {
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(this.mode + ".txt");
			br = new BufferedReader(fr);
			String strLine;
			ArrayList<String> ar = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				ar.add(strLine);
			}
			return ar;
		} finally {
			if (fr != null) {
				fr.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}

	private void setFilesDB(String files) throws IOException {
		File file = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			file = new File(this.mode + ".txt");
			fw = new FileWriter(file.getName(), true);
			bw = new BufferedWriter(fw);
			bw.flush();
			bw.write(files);
		} finally {
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	protected void downloadFiles() throws IOException {
		try {
			String hostname = ini.getString(mode, "hostname", "qqq");
			String username = ini.getString(mode, "username", "qqq");
			String password = ini.getString(mode, "password", "qqq");
			String remoteDir = ini.getString(mode, "remote_dir", "qqq");
			// String localDir = ini.getString("settings","local_dir","qqq");

			clearFolder(localDir);

			f = new MyFTP(hostname, username, password);
			f.connect();
			log.add("info", "Downloading files. Local directory "
					+ this.localDir + "; Remote host parameters:" + username
					+ "@" + hostname + ":" + remoteDir);

			f.downloadFiles(this.localDir, remoteDir, this.fileMask,
					this.daysBack, getFilesDB());
		} finally {
			f.disconnect();
		}
	}

	protected void uploadFilesToArch() throws IOException {
		try {
			String hostname = ini.getString("arch", "hostname", "qqq");
			String username = ini.getString("arch", "username", "qqq");
			String password = ini.getString("arch", "password", "qqq");
			String remoteDir = ini.getString("arch", "remote_dir", "qqq");
			// String localDir = ini.getString("settings", "local_dir", "qqq");
			f = new MyFTP(hostname, username, password);
			f.connect();

			if (!remoteDir.endsWith("/"))
				remoteDir = remoteDir + "/";

			log.add("info", "Uploading files to ARCH. Remote host parameters:"
					+ username + "@" + hostname + ":" + remoteDir);

			f.uploadFiles(this.localDir,
					remoteDir + this.yyyyMMdd.substring(0, 4) + "/"
							+ this.yyyyMMdd.substring(4, 6), fileMask);
		} finally {
			f.disconnect();
		}
	}

	protected void uploadFilesToMD() throws IOException {
		try {
			String hostname = ini.getString("md", "hostname", "qqq");
			String username = ini.getString("md", "username", "qqq");
			String password = ini.getString("md", "password", "qqq");
			String remoteDir = ini.getString("md", "remote_dir", "qqq");
			// String localDir = ini.getString("settings", "local_dir", "qqq");
			f = new MyFTP(hostname, username, password);
			f.connect();
			log.add("info", "Uploading files to MD. Remote host parameters:"
					+ username + "@" + hostname + ":" + remoteDir);
			f.uploadFiles(localDir, remoteDir, fileMask);
		} finally {
			f.disconnect();
		}
	}

	private String getWorkDate(boolean full, int daysBack) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, daysBack);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		String strFull = sdf.format(cal.getTime());
		String strShort = strFull.substring(0, 8);

		String res = "";

		if (full) {
			res = strFull;
		} else {
			res = strShort;
		}

		return res;
	}

}
