package telecom.atyrau;

public class Starter {

	public static void main(String[] args) {
		Execute e = null;
		try {

			String mode = "sip";

			switch (mode) {
			case "sip":
				e = new ExecuteSip(mode);
				break;
			case "si2000":
				e = new ExecuteSI2000(mode);
				break;
			case "si3000":
			default:
				throw new Exception("Unknown mode");
			}

			if (e != null)
				e.start();

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}
}
