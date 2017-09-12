package telecom.executors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import telecom.IniFile;
import telecom.Logger;
import telecom.Mailer;
import telecom.MyFTP;

public class Execute {

	protected String mode;
	protected Logger log;
	protected IniFile ini;
	protected MyFTP f;
	protected String fileMask;
	private String yyyyMMdd;
	protected String yyyyMMddHHmmssS;
	private int daysBack;
	private int daysInc;
	private String localDir;
	//protected ArrayList<String> files; 
	//protected File[] files;
	protected String src_remoteDir; 
	

	public Execute(String mode) throws IOException {
		this.mode = mode;
		this.log = new Logger();
		this.log.add("info", "Starting mode: " + this.mode);
		this.ini = new IniFile("md_operator.conf");
		this.f = null;
		this.fileMask = ini.getString(this.mode, "filemask", "-1");
		//this.files = null;
		this.daysBack = Integer.parseInt(ini.getString("settings", "days_back", "0"));
		this.daysInc = Integer.parseInt(ini.getString(this.mode, "days_inc", "0"));
		this.yyyyMMddHHmmssS = getWorkDate(true, 0);
		this.yyyyMMdd = this.yyyyMMddHHmmssS.substring(0, 8);			
		
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
		String subj = ini.getString("email","subject","");
		try {
			File[] files = downloadFiles();
			if (files.length > 0) {
	         	uploadFilesToArch(files);
				uploadFilesToMD(files);
				setFilesDB(files);
			}			  		 			
		} catch (Exception ex) {
			this.log.add("error", ex.toString());
			throw ex;
		} finally {
			subj = subj + "(" + (this.mode) + ")";
			if (this.log.errCount > 0) { 
				  subj = subj + "[Error]";
				} else if (this.log.wrnCount > 0) {
					subj = subj + "[Warning]";
				}			
			(new Mailer(ini.getString("email","to",""),
					 ini.getString("email","from",""),
					 ini.getString("email","host",""),
					 subj,
					 this.log.getAllMessages())).send();			
		}
	}

	protected void prepareLocalDir(String localDir) {
		File dir = new File(localDir);				
		if (!dir.exists())		
		  dir.mkdirs();
	}

	protected ArrayList<String> getFilesDB() throws IOException {
		FileReader fr = null;
		BufferedReader br = null;
		String slash = System.getProperty("file.separator");
		try {
			fr = new FileReader("db" + slash + this.mode + ".db");
			br = new BufferedReader(fr);
			String strLine;
			ArrayList<String> ar = new ArrayList<String>();
			while ((strLine = br.readLine()) != null) {
				ar.add(strLine);
			}
			return ar;
		} finally {
			if (br != null) {
				br.close();
			}			
			if (fr != null) {
				fr.close();
			}
		}
	}

	protected void setFilesDB(File[] files) throws IOException {
		File file = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		String slash = System.getProperty("file.separator");
		try {
			file = new File("db" + slash + this.mode + ".db");
			fw = new FileWriter(file.getPath(), true);
			
			bw = new BufferedWriter(fw);
			
			for(File f : files) {
				bw.write(f.getName() + "\n");				
			}		
			
			bw.flush();
			
		} finally {
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
	}

	protected File[] downloadFiles() throws IOException {
		try {
			String hostname = ini.getString(mode, "hostname", "qqq");
			String username = ini.getString(mode, "username", "qqq");
			String password = ini.getString(mode, "password", "qqq");
			String remoteDir = ini.getString(mode, "remote_dir", "qqq");
			
			remoteDir = getDatifiedPath(remoteDir);

			prepareLocalDir(localDir);

			f = new MyFTP(hostname, username, password);
			f.connect();
			log.add("info", "Downloading files. Local directory: "
					+ this.localDir + "; Remote host parameters:" + username
					+ "@" + hostname + ":" + remoteDir);
			
			File[] files =
		    	f.downloadFiles(this.localDir, remoteDir, this.fileMask, this.daysBack, this.getFilesDB());
						
			log.add("info", "Downloaded files count: " + files.length);
			
			log.add("info", "Downloaded files: ");
			for (File file: files)				
				log.add("info", file.getName());	
			return files;
			
		} finally {
			f.disconnect();
		}
		
	}

	protected void uploadFilesToArch(File[] files) throws IOException {
		try {
			String hostname = ini.getString("arch", "hostname", "qqq");
			String username = ini.getString("arch", "username", "qqq");
			String password = ini.getString("arch", "password", "qqq");
			String remoteDir = ini.getString("arch", "remote_dir", "qqq");
			f = new MyFTP(hostname, username, password);
			f.connect();
	
			if (!remoteDir.endsWith("/"))
				remoteDir = remoteDir + "/";
			
			remoteDir = remoteDir + this.mode + "/" +
					this.yyyyMMdd.substring(0, 4) + "/" +
					this.yyyyMMdd.substring(4, 6);

			log.add("info", "Uploading files to ARCH. Remote host parameters:"
					+ username + "@" + hostname + ":" + remoteDir);

			f.uploadFiles(files, remoteDir);
		} finally {
			f.disconnect();
		}
	}

	protected void uploadFilesToMD(File[] files) throws IOException {
		try {
			String hostname = ini.getString("md", "hostname", "qqq");
			String username = ini.getString("md", "username", "qqq");
			String password = ini.getString("md", "password", "qqq");
			String remoteDir = ini.getString(mode, "md_dir", "qqq");
			f = new MyFTP(hostname, username, password);
			f.connect();

			if (!remoteDir.endsWith("/"))
				remoteDir = remoteDir + "/";
					
			log.add("info", "Uploading files to MD. Remote host parameters:"
					+ username + "@" + hostname + ":" + remoteDir + files);
			f.uploadFiles(files, remoteDir);
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
	
	private String getDatifiedPath(String path){
		
		String strYYYYMMDD = getWorkDate(false, this.daysInc);
		
		String strYYYY = strYYYYMMDD.substring(0,4);
		String strYY = strYYYYMMDD.substring(2,4);
		String strMM = strYYYYMMDD.substring(4,6);
		String strDD = strYYYYMMDD.substring(6,8);				
		 
		String rd = path;
		
		boolean m = Pattern.compile(".*%YYYY%.*").matcher(rd).matches();

		if (m) {
			rd = rd.replaceAll("%YYYY%", strYYYY);
		}		
		
		m = Pattern.compile(".*%YY%.*").matcher(rd).matches();
		
		if (m) {
			rd = rd.replaceAll("%YY%", strYY);
		}
		
		m = Pattern.compile(".*%MM%.*").matcher(rd).matches();

		if (m) {
			rd = rd.replaceAll("%MM%", strMM);
		}

		m = Pattern.compile(".*%DD%.*").matcher(rd).matches();

		if (m) {
			rd = rd.replaceAll("%DD%", strDD);
		}		
		
		if (!rd.endsWith("/"))
			rd = rd + "/";
					
		return rd;		
	}

}
