package kkr.ktm.domains.common.components.parametersformater.template2;

import java.util.Formatter;
import java.util.Locale;

public class KkrHelp {

	public KkrHelp() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Formatter formatter = new Formatter(Locale.US);
		formatter.format("%08.03f", 12345.6);
		String retval = formatter.toString();
		System.out.println(retval);
		formatter.close();

	}

}
