package telecom.executors.kostanay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

import telecom.Mailer;
import telecom.executors.Execute;

public class ExecuteCC8LIS extends Execute{

	public ExecuteCC8LIS(String mode) throws IOException {
		super(mode);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressWarnings("resource")
	private String unRarFile(String dir, String fileName) throws RarException, IOException{
		
		File f = new File(dir + fileName);
		Archive a = null;

	    a = new Archive(new FileVolumeManager(f));

	    String extractedFile = null;
		if (a != null) {
			//a.getMainHeader().print();
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				extractedFile = fh.getFileNameString().trim();
				File out = new File(dir + extractedFile);
				FileOutputStream os = new FileOutputStream(out);
				a.extractFile(fh, os);
				os.close();

				fh = a.nextFileHeader();
			}
		}
		return extractedFile;				
	}
	
	
	protected void start() throws Exception {		 
		String subj = ini.getString("email","subject","");
		try {
			downloadFiles();
			if (this.files.size() > 0) {
	         	uploadFilesToArch();
							
				String extractedFileName ="";
				for(String fileName : files ) {
					extractedFileName = unRarFile(localDir, fileName);
					uploadFilesToMD(extractedFileName);
				}
								
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

}
