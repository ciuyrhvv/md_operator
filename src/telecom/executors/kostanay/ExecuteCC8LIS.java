package telecom.executors.kostanay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.impl.FileVolumeManager;
import com.github.junrar.rarfile.FileHeader;

import telecom.Mailer;
import telecom.executors.Execute;

public class ExecuteCC8LIS extends Execute{

	public ExecuteCC8LIS(String mode) throws IOException {
		super(mode);
	}
	
	@SuppressWarnings("resource")
	private File[] unRarFile(File file) throws RarException, IOException{
		String slash = System.getProperty("file.separator");
		
		ArrayList<File> fileList = new ArrayList<>();
		
		Archive a = new Archive(new FileVolumeManager(file));

	    String extractedFile = null;
		if (a != null) {
			//a.getMainHeader().print();
			FileHeader fh = a.nextFileHeader();
			while (fh != null) {
				extractedFile = fh.getFileNameString().trim();
				String strOutFile = file.getCanonicalFile().getParent() + slash + extractedFile;
				File out = new File(strOutFile);
				if (out.exists()) 
				  throw new IOException(out.getCanonicalPath() + " allready exists. JUNRAR error.");
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
			File[] files = downloadFiles();
			
			if (files.length > 0) {
				
	         	uploadFilesToArch(files);
	         	
	         	List <File> aFiles = new ArrayList<File>();	         		         	
							
				for(File file : files ) {
										
					File[] unrFiles = unRarFile(file);
														
					aFiles.addAll(Arrays.asList(unrFiles));					

				}
												
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

}
