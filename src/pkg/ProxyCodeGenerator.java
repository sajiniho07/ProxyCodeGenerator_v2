package pkg;

import java.util.*;

public class ProxyCodeGenerator {
	public static void main(String[] args) {
		List<String> outputLines = new ArrayList<>();
		List<String> lines = IOCodeGen.input("input.txt");
		createApiBody(outputLines, lines);
	//	populateProxyInterface(outputLines, lines);
	//	populateAddScalar(outputLines, lines);
		IOCodeGen.output(outputLines, "output.txt");
	}

	private static void createApiBody(List<String> outputLines, List<String> lines) {
		outputLines.add("--------- queryString:");
		outputLines.add("\n");
		String methodName = "public SomeOutput methodName(";
		String queryString = "StringBuilder sb = new StringBuilder();\nsb.append(\"exec \" + ProcedureNames.SOME_SP_NAME);\n";
		String querySetParam = "Query query = getSession().createNativeQuery(sb.toString())\n";
		String querySetNullableParam = "";
		for (String line : lines) {
			if (line.contains("ALTER")) {
				continue;
			}
			String[] words = line.split("\\s+");
			queryString = getQueryString(queryString, words);
			querySetParam = getQuerySetParam(querySetParam, words);
			querySetNullableParam = getQuerySetNullableParam(querySetNullableParam, words);
			methodName = populateMethodArgs(methodName, words);
		}
		querySetParam += ".setResultTransformer(Transformers.aliasToBean(SomeOutput.class));\n";

		outputLines.add(methodName +") {\n");
		outputLines.add("***********************\n");
		outputLines.add(queryString +"\n");
		outputLines.add("***********************\n");
		outputLines.add(querySetParam +"\n");
		outputLines.add("***********************\n");
		outputLines.add(querySetNullableParam);
	}

	private static String getQuerySetNullableParam(String querySetNullableParam, String[] words) {
		if (words.length < 2) {
			return querySetNullableParam;
		}
		String paramName = words[0].substring(1);
		String javaValidParamName = getJavaValidParamName(paramName);
		if (Arrays.asList(words).contains("NULL") || Arrays.asList(words).contains("null")) {
			querySetNullableParam += "if (" + javaValidParamName + " != null) {query.setParameter(\"" + javaValidParamName + "\", " + javaValidParamName + ");}\n";
		}
		return querySetNullableParam;
	}

	private static String getQuerySetParam(String querySetParam, String[] words) {
		if (words.length < 2) {
			return querySetParam;
		}
		String paramName = words[0].substring(1);
		String javaValidParamName = getJavaValidParamName(paramName);
		if (Arrays.asList(words).contains("NULL") || Arrays.asList(words).contains("null")) {
			querySetParam += ".setParameter(\"" + javaValidParamName + "\", " + javaValidParamName + ")\n";
		}
		return querySetParam;
	}

	private static String getQueryString(String queryString, String[] words) {
		if (words.length < 2) {
			return queryString;
		}
		String paramName = words[0].substring(1);
		String javaValidParamName = getJavaValidParamName(paramName);
		if (Arrays.asList(words).contains("NULL") || Arrays.asList(words).contains("null")) {
			queryString += "sb.append(" + javaValidParamName + " == null ? \"\" : \"@" + paramName + " = :" + javaValidParamName + ", \");\n";
		} else {
			queryString += "sb.append(\"@" + paramName + " = :" + javaValidParamName + ",\");\n";
		}
		return queryString;
	}

	private static String populateMethodArgs(String methodName, String[] words) {
		if (words.length < 2) {
			return methodName;
		}
		String paramName = words[0].substring(1);
		String javaValidParamName = getJavaValidParamName(paramName);

		String paramType = words[1];
		if (paramType.contains("BIT")) {
			methodName += ", boolean " + javaValidParamName;
		} else if (paramType.contains("DATE")) {
			methodName += ", Date " + javaValidParamName;
		} else if (paramType.contains("BIGINT")) {
			methodName += ", Long " + javaValidParamName;
		} else if (paramType.equals("INT")) {
			methodName += ", Integer " + javaValidParamName;
		} else if (paramType.equals("DECIMAL")) {
			methodName += ", Double " + javaValidParamName;
		} else {
			methodName += ", String " + javaValidParamName;
		}
		return methodName;
	}

	private static String getJavaValidParamName(String paramName) {
		char c[] = paramName.toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		String s = new String(c);
		return s.replace("ID", "Id");
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

	private static void populateAddScalar(List<String> outputLines, List<String> lines) {
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
