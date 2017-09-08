package telecom.executors;

import telecom.executors.kostanay.*;
import telecom.executors.atyrau.*;


public class Starter {

	public static void main(String[] args) {
		Execute e = null;
		try {

			String mode =
					args[0];
					 //"s12"; 
					 //"cc8_lis"; 
					 
			
			switch (mode) {
			case "bclient":
				e = new ExecuteBClient(mode);
				break;			
			case "sip":
				e = new ExecuteSip(mode);
				break;
			case "si2000":
				e = new ExecuteSI2000(mode);
				break;
			case "skyage":
				e = new ExecuteSkyAge(mode);
				break;
			case "skyage_ngn":
				e = new ExecuteSkyAgeNGN(mode);
				break;				
			case "ngn":
				e = new ExecuteNGN(mode);
				break;
			case "dama":
				e = new ExecuteDAMA(mode);
				break;				
			case "ktcl09":
				e = new ExecuteKTCL09(mode);
				break;
			case "rubin":
				e = new ExecuteRubin(mode);
				break;
			case "cdma":
				e = new ExecuteCDMA(mode);
				break;
			case "ssw":
				e = new ExecuteSSW(mode);
				break;
			case "zxj10_amn":
				e = new ExecuteZXJ10AMN(mode);
				break;
			case "zxj10_ats22":
				e = new ExecuteZXJ10ATS22(mode);
				break;
			case "zxj10_ats26":
				e = new ExecuteZXJ10ATS26(mode);
				break;
			case "zxj10_ats28":
				e = new ExecuteZXJ10ATS28(mode);
				break;
			case "zxj10_ats9":
				e = new ExecuteZXJ10ATS9(mode);
				break;
			case "zxj10_ats573":
				e = new ExecuteZXJ10ATS573(mode);
				break;				
			case "s12wr_amts":
				e = new ExecuteS12WRAMTS(mode);
				break;
			case "cc8_aul":
				e = new ExecuteCC8AUL(mode);
				break;
			case "cc8_fed":
				e = new ExecuteCC8FED(mode);
				break;
			case "cc8_krs":
				e = new ExecuteCC8KRS(mode);
				break;
			case "cc8_lis":
				e = new ExecuteCC8LIS(mode);
				break;
			case "cc8_krb":
				e = new ExecuteCC8KRB(mode);
				break;
			case "cc8_srk":
				e = new ExecuteCC8SRK(mode);
				break;				
			case "s12":
				e = new ExecuteS12(mode);
				break;
			case "s12wr_ats50":
				e = new ExecuteS12WRATS50(mode);
				break;
			case "s12wr_ats53":
				e = new ExecuteS12WRATS53(mode);
				break;
			case "s12wr_ats55":
				e = new ExecuteS12WRATS55(mode);
				break;	
			case "s12wr_ats7a":
				e = new ExecuteS12WRATS7ARK(mode);
				break;	
			case "s12wr_ats7r":
				e = new ExecuteS12WRATS7RDN(mode);
				break;					
			default:
				throw new Exception("Unknown mode");
				
			}

			if (e != null)
				e.start();

		} catch (Exception ex) {
			ex.printStackTrace();

		} finally {
			if (e != null){
				try{
					e.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
				
		}
	}
}
