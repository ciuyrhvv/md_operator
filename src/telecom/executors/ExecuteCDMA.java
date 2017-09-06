package telecom.executors;

import java.io.IOException;

import telecom.Execute;

public class ExecuteCDMA extends Execute {

	public ExecuteCDMA(String mode) throws IOException {
		super(mode);
		//this.fileMask = "(scrn_)(.*)(\\.txt)";
		//scrn_20170729_111500-txt_CDMAv5_REC_712_ATYRAU.txt		
	}
}
