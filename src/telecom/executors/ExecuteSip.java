package telecom.executors;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import telecom.Mailer;

public class ExecuteSip extends Execute {
	
	String pieceName = null; 

	public ExecuteSip(String mode) throws IOException {
		super(mode);
		this.pieceName = "scrn_" + this.yyyyMMddHHmmssS + "_txt_bw.txt";
	
	}
	
	protected void start() throws Exception {		 
		String subj = ini.getString("email","subject","");		
		try {
			File[] files = downloadFiles();
			if (files.length > 0) {

				uploadFilesToArch(files);	
	         	
				List <File> aFiles = new ArrayList<File>();
				aFiles.add(getMergedFile(files, this.pieceName));
								
				uploadFilesToMD(aFiles.toArray(new File[aFiles.size()]));
				
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
	
	private File getMergedFile(File[] files, String outFilename) throws IOException {
		String slash = System.getProperty("file.separator");

		FileOutputStream fout = new FileOutputStream(files[0].getParentFile().getCanonicalPath() + slash + outFilename);
		try {
			for (File file : files) {
				FileInputStream fin = new FileInputStream(file);
				try {
					byte b[] = new byte[fin.available()];
					fin.read(b);
					fout.write(b);
					fin.close();
				} finally {
					if (fin != null) {
						fin.close();						
					}
				}
			}
		} finally {
			if (fout != null) {
				fout.close();
			}
		}
		File outFile = new File(files[0].getParentFile().getCanonicalPath() + slash + outFilename);
		return outFile;

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
				if (!Pattern.compile("\\{.*\\}").matcher(strLine).matches())
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
			
			bw.write("{" + this.pieceName + "}" + "\n");	
			
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

}
