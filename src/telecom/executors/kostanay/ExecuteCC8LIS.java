package telecom.executors.kostanay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
	private File[] unRarFile(File file) throws RarException, IOException{
		String slash = System.getProperty("file.separator");
		
		ArrayList<File> fileList = new ArrayList<>();
		
		//File f = new File(file.getCanonicalPath());
		Archive a = null;

	    a = new Archive(new FileVolumeManager(file));

	    String extractedFile = null;
		if (a != null) {
			//a.getMainHeader().print();
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				extractedFile = fh.getFileNameString().trim();
				String strOutFile = file.getCanonicalFile().getParent() + slash + extractedFile;
				File out = new File(strOutFile);
				fileList.add(out);
				FileOutputStream os = new FileOutputStream(out);
				a.extractFile(fh, os);
				os.close();

				fh = a.nextFileHeader();
			}
		}

		return fileList.toArray(new File[fileList.size()]);
	}	
	
	
	protected void start() throws Exception {		 
		String subj = ini.getString("email","subject","");
		try {
			downloadFiles();
			
			if (this.files.size() > 0) {
				
	         	uploadFilesToArch();
							
				for(String fileName : files ) {
					
					File rarFile = new File(localDir + fileName);
					
					File[] unrFiles = unRarFile(rarFile);
					
					for(File unrFile : unrFiles) {	
						
						uploadFilesToMD(unrFile.getName());
						
					}	
				}
								
				setFilesDB(this.files);
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
