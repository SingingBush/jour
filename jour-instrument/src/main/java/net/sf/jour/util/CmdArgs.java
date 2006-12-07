package net.sf.jour.util;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

public abstract class CmdArgs {

	/** 
	 * This is usage examples:
	 * @param args
	 */
	public static void main(String[] args) {
		Properties argsp = CmdArgs.load(args);
		if (argsp.getProperty("help") != null) {
			//help();
			System.exit(0);
		}
	}

	public static int isArgsName(String name) {
		if (name.startsWith("--")) {
			name = name.substring(2);
			return 2;
		} else if (name.startsWith("-")) {
			name = name.substring(1);
			return 1;
		}
		return 0;
	}

	public static Properties load(String[] args) {
		Properties properties = new Properties();
		for (int i = 0; i < args.length; i++) {
			String name = args[i];
			String value = "true";
			int prefix;
			if ((prefix = isArgsName(name)) > 0) {
				name = name.substring(prefix);
				if ((i + 1) < args.length) {
					if (isArgsName(args[i + 1]) == 0) {
						value = args[i + 1];
						i++;
					}
				}
				Object has = properties.get(name);
				if (has == null) {
					properties.put(name, value);
				} else if (has instanceof List) {
					((List) has).add(value);
				} else {
					List valueList = new Vector();
					valueList.add((String) has);
					valueList.add(value);
					properties.put(name, valueList);
				}
			}
		}
		return properties;
	}

}