package pkg;

import java.util.*;

public class ProxyCodeGenerator {
	public static void main(String[] args) {
		List<String> outputLines = new ArrayList<>();
		List<String> lines = IOCodeGen.input("input.txt");
		populateProxyInterface(outputLines, lines);
		poulateAddScalar(outputLines, lines);
		IOCodeGen.output(outputLines, "output.txt");
	}

	private static void populateProxyInterface(List<String> outputLines, List<String> lines) {
		outputLines.add("---------proxy interface:");
		outputLines.add("\n");
		for (String line : lines) {
			if (line.contains("{")) {
				outputLines.add(line.replace(" {", ";"));
				outputLines.add("\n");
			}
		}
	}

	private static void poulateAddScalar(List<String> outputLines, List<String> lines) {
		outputLines.add("---------addScalar:");
		outputLines.add("\n");
		for (String line : lines) {
			if (line.contains(" set")) {
				line = line.substring(line.indexOf("(") + 1);
				String[] split = line.substring(0, line.indexOf(")")).split(" ");

				String type = split[0];
				String temp = ".addScalar(\"" + split[1] + "\", " + type +"Type.INSTANCE)";
				if (type.equals("Date")) {
					temp = ".addScalar(\"" + split[1] + "\")";
				}
				outputLines.add(temp);
			}
		}
	}
}
