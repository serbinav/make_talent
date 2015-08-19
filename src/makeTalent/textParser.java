package makeTalent;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class textParser {

	// константы для формирования html файла
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
	// String inputDict - путь до файла словаря
	// String inputDoc - путь до текстового файла
	// String inputCountString - число строк в выходном html файле не должно
	// превышать данную величину
	// выходной параметр:
	// String содержащая текст ошибки или "ok", если все прошло удачно
	public static String findstring(String inputDict, String inputDoc, String inputCountString)
			throws FileNotFoundException, IOException {

		final FileInputStream FinputStreamDoc = new FileInputStream(inputDoc);
		final InputStreamReader IStreamReaderDoc = new InputStreamReader(FinputStreamDoc, "Windows-1251");
		final BufferedReader BreaderDoc = new BufferedReader(IStreamReaderDoc);

		final FileInputStream FinputStreamDict = new FileInputStream(inputDict);
		final InputStreamReader IStreamReaderDict = new InputStreamReader(FinputStreamDict, "Windows-1251");
		final BufferedReader BreaderDict = new BufferedReader(IStreamReaderDict);

		// переменная используется при формировании названия файла(порядковый
		// номер файла)
		int i = 1;

		List<String> stringDict = new ArrayList<String>();
		String lineDict;
		int countLine = 0;
		// построчное считывание файла словаря
		while ((lineDict = BreaderDict.readLine()) != null) {

			// поиск пробельного символа в строке с помощью RegExp
			String pattern = "\u0020";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(lineDict.trim());
			while (m.find()) {
				return "ошибка: строка в файле словаря содержит больше одного слова";
			}

			stringDict.add(lineDict.trim());
			countLine++;
		}
		if (countLine > 100000) {
			return "ошибка: количество строк в файле словаря больше 100 000";
		}

		int inputInt;
		// попытка преобразования входного параметра inputCountString в число
		try {
			inputInt = new Integer(inputCountString);
		} catch (NumberFormatException e) {
			return "ошибка: " + inputCountString + " не числовой параметр";
		}

		// переменная для подсчета количества считанных строк
		int stringCount = 1;

		// переменная, в которой хранится номер последней найденной строки,
		// содержащей точку
		int memoryStringNum = 0;

		// флаг который меняет свое состояние если нашли слово из словаря в
		// строке
		boolean flag = false;

		// строка в которой будет храниться основное тело html файла
		StringBuilder body = new StringBuilder();

		String line;
		// построчное считывание текстового файла
		while ((line = BreaderDoc.readLine()) != null) {
			// массив для хранения отдельных слов из строки
			String[] stringParts;

			if (line.contains(".")) {
				memoryStringNum = stringCount;
			}

			if (stringCount >= inputInt) {
				stringParts = line.split(" ");
				body.append(MY_CONSTANT_OP);
				for (String word : stringParts) {
					for (int j = 0; j < stringDict.size(); j++) {
						// сравнение по слову со словарем
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

				StringBuilder untilDota = new StringBuilder();
				StringBuilder afterDota = new StringBuilder();
				// если в проанализированном фрагменте встречалась точка
				// то нам нельзя разбивать данное предложение
				if (memoryStringNum != 0) {
					// все что до точки останется в этом html, остальное уйдет в
					// следующий
					untilDota.append(body.substring(0, body.lastIndexOf(".") + 1));
					untilDota.append(MY_CONSTANT_CP);
					afterDota.append(MY_CONSTANT_OP);
					afterDota.append(body.substring(body.lastIndexOf(".") + 1));
				} else
					untilDota.append(body);

				// формирование выходного html файла
				BufferedWriter NewHtml = new BufferedWriter(new FileWriter("part" + i + ".html"));
				NewHtml.write(MY_CONSTANT_DOCTYPE);
				NewHtml.write(MY_CONSTANT_HTTP);
				NewHtml.write(MY_CONSTANT_OHTML);
				NewHtml.write(MY_CONSTANT_OHEAD);
				NewHtml.write(MY_CONSTANT_META);
				NewHtml.write(MY_CONSTANT_CHEAD);
				NewHtml.write(MY_CONSTANT_OBODY);
				NewHtml.write(untilDota.toString());
				NewHtml.write(MY_CONSTANT_CBODY);
				NewHtml.write(MY_CONSTANT_CHTML);
				NewHtml.close();

				body.delete(0, body.length());

				// если в проанализированном фрагменте встречалась точка
				// то количество строк в следующем фрагменте изначально будет не
				// нулевое
				if (memoryStringNum != 0) {
					body.append(afterDota);
					stringCount = inputInt - memoryStringNum;
					memoryStringNum = 0;
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

		// если текстовый файл не заканчивается точкой, то остается фрагмент
		// который запишем в html файла
		if (stringCount > 1) {
			BufferedWriter NewHtml = new BufferedWriter(new FileWriter("part" + i + ".html"));
			NewHtml.write(MY_CONSTANT_DOCTYPE);
			NewHtml.write(MY_CONSTANT_HTTP);
			NewHtml.write(MY_CONSTANT_OHTML);
			NewHtml.write(MY_CONSTANT_OHEAD);
			NewHtml.write(MY_CONSTANT_META);
			NewHtml.write(MY_CONSTANT_CHEAD);
			NewHtml.write(MY_CONSTANT_OBODY);
			NewHtml.write(body.toString());
			NewHtml.write(MY_CONSTANT_CBODY);
			NewHtml.write(MY_CONSTANT_CHTML);
			NewHtml.close();

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

	// метод для проверки входных параметров на корректность
	// входные параметры:
	// String fileWordbook - путь до файла словаря
	// String fileDoc - путь до текстового файла
	// String numberString - число строк в выходном html файле
	// выходной параметр:
	// String содержащая текст ошибки или "ok", если все прошло удачно
	public static String testData(String fileWordbook, String fileDoc, String numberString) {
		int numberLine = 0;
		File fileDict = new File("");
		File fileText = new File("");

		String errorFile = "";
		fileDict = new File(fileWordbook);
		errorFile = test_file(fileDict);
		if (errorFile != "ok") {
			return errorFile;
		}

		fileText = new File(fileDoc);
		errorFile = test_file(fileText);
		if (errorFile != "ok") {
			return errorFile;
		}

		try {
			numberLine = new Integer(numberString);

			if (numberLine < 10) {
				return "ошибка: числовой параметр меньше 10";
			} else if (numberLine > 100000) {
				return "ошибка: числовой параметр больше 100 000";
			}
		} catch (NumberFormatException e) {
			return "ошибка: " + numberString + " не числовой параметр";
		}

		return "ok";
	}

	// метод для проверки файлов на корректность
	// входные параметры:
	// File inputFile - файл
	// выходной параметр:
	// String содержащая текст ошибки или "ok", если все прошло удачно
	public static String test_file(File inputFile) {
		if (!inputFile.exists()) {
			return "ошибка: данный файл: " + inputFile.toString() + " не существует";
		} else if (!inputFile.isFile()) {
			return "ошибка: данный файл: " + inputFile.toString() + " является директорией";
		} else if (!inputFile.canRead()) {
			return "ошибка: данный файл: " + inputFile.toString() + " не доступен для чтения";
		} else if (inputFile.length() > 2097152) {
			return "ошибка: размер данного файла: " + inputFile.toString() + " превышает 2Мб";
		}
		return "ok";
	}

	// метод для информирования пользователя о входных параметрах программы
	public static void returnHelp() {
		System.err.println("Входные параметры:");
		System.err.println("[-n] количество строк в выходных html-файлах в интервале от 10 строк до 100 000 строк");
		System.err.println("[-d] путь к файлу словарю (размер файла не должен превышать 100 000 строк или 2 Мб)");
		System.err.println("[-t] путь к файлу с текстом (размер файла не должен превышать 2 Мб)");
		return;
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("ошибка: не заданы входные параметры");
			returnHelp();
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
					returnHelp();
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
			System.err.println("ошибка: не задано количество строк в выходных html-файлах (параметр [-n])");
			returnHelp();
			return;
		} else if (fileDict == "") {
			System.err.println("ошибка: не задан путь к файлу словаря (параметр [-d])");
			returnHelp();
			return;
		} else if (fileText == "") {
			System.err.println("ошибка: не задан путь к файлу с текстом (параметр [-t])");
			returnHelp();
			return;
		}

		String error = "";

		error = textParser.testData(fileDict, fileText, numberLine);
		if (error != "ok") {
			System.err.println(error);
			returnHelp();
			return;
		}

		try {
			error = textParser.findstring(fileDict, fileText, numberLine);
			if (error != "ok") {
				System.err.println(error);
				returnHelp();
				return;
			}
		} catch (FileNotFoundException e) {
			System.out.println("ошибка: не найден файл!");
		} catch (IOException e) {
			System.out.println("ошибка: ввода вывода!");
		}
	}
}
