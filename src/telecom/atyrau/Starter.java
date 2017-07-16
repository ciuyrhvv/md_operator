package telecom.atyrau;
public static class Starter {


public static void main  

try {
  Execute e = null;

  String mode = this.args[0];

  switch (mode) {
    case "sip":      e = new ExecuteSip(mode);
             break;
    case "si2000":   e = new ExecuteSI2000(mode);
             break;
    case "si3000":   e = new ExecuteSI3000(mode);
             break;
    case "cc":       e = new ExecuteCC(mode);
             break;
    case "bclient":  e = new ExecuteBClient(mode);
             break;
    case "cdma":     e = new ExecuteCDMA(mode);
             break;                                                    
    default: throw new Exception("Unknown mode");
             break;
  }

  if (e != null)
    e.start();

} catch (Exception ex) {

}