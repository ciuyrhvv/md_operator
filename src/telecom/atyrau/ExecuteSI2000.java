package telecom.atyrau;

import java.io.IOException;

public class ExecuteSI2000 extends Execute {

	public ExecuteSI2000(String mode) throws IOException {
		super(mode);
	    String fileMmask = "\\w\\d{4}" + StringYYYYMMDD + "\\d{6}.ama"; //i117020160901135064.ama
	}

}
