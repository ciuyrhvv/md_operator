package telecom.executors;

import java.io.IOException;

public class ExecuteBClient extends Execute {

	public ExecuteBClient(String mode) throws IOException {
		super(mode);
		//this.fileMask = "(scrn_)(.*)(\\.txt)";
		//scrn_2017-05-17_00`00`00-2017-05-17_23`59`59_BCLIENT-CDR_CSR_14_Record_BCL_ATR.txt
	}

}
