package make_talent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class text_parser {
		
	private static final String MY_CONSTANT_DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" ";
	private static final String MY_CONSTANT_HTTP = "\"http://www.w3.org/TR/html4/strict.dtd\">";
	private static final String MY_CONSTANT_OHTML = "<html>";
	private static final String MY_CONSTANT_OHEAD = "<head>";
	private static final String MY_CONSTANT_META = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
	private static final String MY_CONSTANT_CHEAD = "</head>";
	private static final String MY_CONSTANT_OBODY = "<body>";

	private static final String MY_CONSTANT_OP = "<p>";
	private static final String MY_CONSTANT_CP = "</p>";

	private static final String MY_CONSTANT_OBI = "<b><i>";
	private static final String MY_CONSTANT_CBI = "</i></b>";

	private static final String MY_CONSTANT_CBODY = "</body>";
	private static final String MY_CONSTANT_CHTML = "</html>";
	
	// основной метод для поиска слов из файла словаря в текстовом файле и
	// формирования выходных html файлов
	// входные параметры:
	// File inputDict - файл словаря
	// File inputDoc - текстовый файл
	// int inputCountString - количество строк в выходном файле не должно
	// превышать эту величину
	public static String findstring(String inputDict, String inputDoc, String inputCountString)
			throws FileNotFoundException, IOException {

		final FileInputStream FinputStreamDoc = new FileInputStream(inputDoc);
		final InputStreamReader IStreamReaderDoc = new InputStreamReader(FinputStreamDoc, "Windows-1251");
		final BufferedReader BreaderDoc = new BufferedReader(IStreamReaderDoc);

		final FileInputStream FinputStreamDict = new FileInputStream(inputDict);
		final InputStreamReader IStreamReaderDict = new InputStreamReader(FinputStreamDict, "Windows-1251");
		final BufferedReader BreaderDict = new BufferedReader(IStreamReaderDict);

// ,,
		StringBuilder body = new StringBuilder();

		// переменная используется при формировании названия файла(порядковый
		// номер файла)
		int i = 1;
// ,,
		String[] stringParts;

		//
		String lineDict;
		List<String> stringDict = new ArrayList<String>();
		int countLine = 0;
		while ((lineDict = BreaderDict.readLine()) != null) {
			stringDict.add(lineDict.trim());
			countLine++;
		}
		if (countLine > 100000) {
			return "ошибка: количество строк в файле словаря больше 100 000";
		}

		String line;
		boolean flag = false;

		int inputInt;
		try {
			inputInt = new Integer(inputCountString);
		} catch (NumberFormatException e) {
			return "ошибка: " + inputCountString + " не числовой параметр";
		}

		int stringCount = 1;
		int memoryStringnumber = 0;
		while ((line = BreaderDoc.readLine()) != null) {

			if (line.contains(".")) {
				memoryStringnumber = stringCount;
			}

			if (stringCount >= inputInt) {

				stringParts = line.split(" ");

				body.append(MY_CONSTANT_OP);
				for (String word : stringParts) {
					for (int j = 0; j < stringDict.size(); j++) {
						if (word.equalsIgnoreCase(stringDict.get(j))) {
							body.append(MY_CONSTANT_OBI + word + MY_CONSTANT_CBI + " ");
							flag = true;
							break;
						}
					}
					if (flag != true)
						body.append(word + " ");
					else
						flag = false;
				}
				body.append(MY_CONSTANT_CP);

				StringBuilder untildota = new StringBuilder();
				StringBuilder afterdota = new StringBuilder();
				if (memoryStringnumber != 0) {
					untildota.append(body.substring(0, body.lastIndexOf(".") + 1));
					untildota.append(MY_CONSTANT_CP);
					afterdota.append(MY_CONSTANT_OP);
					afterdota.append(body.substring(body.lastIndexOf(".") + 1));
				} else
					untildota.append(body);

				BufferedWriter qwerty = new BufferedWriter(new FileWriter("part" + i + ".html"));
				qwerty.write(MY_CONSTANT_DOCTYPE);
				qwerty.write(MY_CONSTANT_HTTP);
				qwerty.write(MY_CONSTANT_OHTML);
				qwerty.write(MY_CONSTANT_OHEAD);
				qwerty.write(MY_CONSTANT_META);
				qwerty.write(MY_CONSTANT_CHEAD);
				qwerty.write(MY_CONSTANT_OBODY);
				qwerty.write(untildota.toString());
				qwerty.write(MY_CONSTANT_CBODY);
				qwerty.write(MY_CONSTANT_CHTML);
				qwerty.close();

				body.delete(0, body.length());
				if (memoryStringnumber != 0) {
					body.append(afterdota);
					stringCount = inputInt - memoryStringnumber;
					memoryStringnumber = 0;
				} else
					stringCount = 0;
				i++;
			} else {
				stringParts = line.split(" ");

				body.append(MY_CONSTANT_OP);
				for (String word : stringParts) {
					for (int k = 0; k < stringDict.size(); k++) {
						if (word.equalsIgnoreCase(stringDict.get(k))) {
							body.append(MY_CONSTANT_OBI + word + MY_CONSTANT_CBI + " ");
							flag = true;
							break;
						}
					}
					if (flag != true)
						body.append(word + " ");
					else
						flag = false;
				}
				body.append(MY_CONSTANT_CP);
			}
			stringCount++;
		}

		if (stringCount > 1) {
			BufferedWriter qwerty = new BufferedWriter(new FileWriter("part" + i + ".html"));
			qwerty.write(MY_CONSTANT_DOCTYPE);
			qwerty.write(MY_CONSTANT_HTTP);
			qwerty.write(MY_CONSTANT_OHTML);
			qwerty.write(MY_CONSTANT_OHEAD);
			qwerty.write(MY_CONSTANT_META);
			qwerty.write(MY_CONSTANT_CHEAD);
			qwerty.write(MY_CONSTANT_OBODY);
			qwerty.write(body.toString());
			qwerty.write(MY_CONSTANT_CBODY);
			qwerty.write(MY_CONSTANT_CHTML);
			qwerty.close();

			body.delete(0, body.length());
		}

		FinputStreamDict.close();
		IStreamReaderDict.close();
		BreaderDict.close();
		FinputStreamDoc.close();
		IStreamReaderDoc.close();
		BreaderDoc.close();
		return "ok";
	}
	
	public static String testData(String fileWordbook, String fileDoc, String numberString) {
		int number_line = 0;
		File file_dict = new File("");
		File file_text = new File("");

		String errorFile = "";
		file_dict = new File(fileWordbook);
		errorFile = test_file(file_dict);
		if (errorFile != "ok") {
			return errorFile;
		}

		file_text = new File(fileDoc);
		errorFile = test_file(file_text);
		if (errorFile != "ok") {
			return errorFile;
		}
		
		try {
			number_line = new Integer(numberString);

			if (number_line < 10) {
				return "ошибка: числовой параметр меньше 10";
			} else if (number_line > 100000) {
				return "ошибка: числовой параметр больше 100 000";
			}
		} catch (NumberFormatException e) {
			return "ошибка: " + numberString + " не числовой параметр";
		}
		
		return "ok";
	}

	public static String test_file(File input_file) {
		if (!input_file.exists()) {
			return "ошибка: данный файл: " + input_file.toString() + " не существует";
		} else if (!input_file.isFile()) {
			return "ошибка: данный файл: " + input_file.toString() + " является директорией";
		} else if (!input_file.canRead()) {
			return "ошибка: данный файл: " + input_file.toString() + " не доступен для чтения";
		} else if (input_file.length() > 2097152) {
			return "ошибка: размер данного файла: " + input_file.toString() + " превышает 2Мб";
		}
		return "ok";
	}

	public static void return_help() {
		System.err.println("Входные параметры:");
		System.err.println("[-n] количество строк в выходных html-файлах в интервале от 10 строк до 100 000 строк");
		System.err.println("[-d] путь к файлу словарю (размер файла не должен превышать 100 000 строк или 2 Мб)");
		System.err.println("[-t] путь к файлу с текстом (размер файла не должен превышать 2 Мб)");
		return;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("ошибка: не заданы входные параметры");
			return_help();
			return;
		}

		boolean flagNumber = false;
		boolean flagDict = false;
		boolean flagText = false;

		String numberLine = "";
		String fileDict = "";
		String fileText = "";

		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				switch (args[i]) {
				case "-n":
					flagNumber = true;
					flagDict = false;
					flagText = false;
					break;
				case "-d":
					flagNumber = false;
					flagDict = true;
					flagText = false;
					break;
				case "-t":
					flagNumber = false;
					flagDict = false;
					flagText = true;
					break;
				default:
					System.err.println("ошибка: " + args[i] + " неизвестный аргумент");
					return_help();
					return;
				}
			}
			if (flagNumber) {
				numberLine = args[i];
			} else if (flagDict) {
				fileDict = args[i];
			} else if (flagText) {
				fileText = args[i];
			}
		}

		if (numberLine == "") {
			System.err.println("ошибка: не задано количество строк в выходных html-файлах");
			return_help();
			return;
		} else if (fileDict == "") {
			System.err.println("ошиfileDocбка: не задан путь к файлу словаря");
			return_help();
			return;
		} else if (fileText == "") {
			System.err.println("ошибка: не задан путь к файлу с текстом");
			return_help();
			return;
		}

		String error = "";

		error = text_parser.testData(fileDict, fileText, numberLine);
		if (error != "ok") {
			System.err.println(error);
			return_help();
			return;
		}

		try {
			error = text_parser.findstring(fileDict, fileText, numberLine);
			if (error != "ok") {
				System.err.println(error);
				return_help();
				return;
			}
		} catch (FileNotFoundException e) {
			System.out.println("ошибка: не найден файл!");
		} catch (IOException e) {
			System.out.println("ошибка: ввода вывода!");
		}
	}
}
