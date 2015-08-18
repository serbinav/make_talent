package make_talent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class text_parser {
		
	//java файла частями в память
	public static String findstring (File inputDict, File inputDoc, int inputCountString)  throws FileNotFoundException, IOException{
		String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" ";
		String http = "\"http://www.w3.org/TR/html4/strict.dtd\">";
		String ohtml = "<html>";
		String ohead = "<head>";
		String meta = "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1251\">";
		String chead = "</head>";
		String obody = "<body>";
		
		String op = "<p>";
		String cp = "</p>";
		
		String obi = "<b><i>";
		String cbi = "</i></b>";
		
		String cbody = "</body>";
		String chtml = "</html>";
        
		final InputStream inputStreamDoc = new FileInputStream(inputDoc);
		final BufferedReader readerDoc = new BufferedReader(new InputStreamReader(inputStreamDoc)); 
		final InputStream inputStreamDict = new FileInputStream(inputDict);
		final BufferedReader readerDict = new BufferedReader(new InputStreamReader(inputStreamDict)); 
		
		StringBuilder body = new StringBuilder();
		  
		int i = 1;
		String[] stringParts;
		
        String lineDict;
        List<String> stringDict = new ArrayList<String>();
        
        int countLine = 0;
        while ((lineDict = readerDict.readLine()) != null) {
        	stringDict.add(lineDict.trim());
        	countLine++;
        }
		if ( countLine > 100000 ) {
			return "ошибка: количество строк в файле словаря больше 100 000";
		}	
        
		// использовать ли строку ???
		String line;  
		boolean flag = false;
		
        int stringCount = 1;
        int memoryStringnumber = 0;
        while ((line = readerDoc.readLine()) != null) {
        	
        	if (line.contains(".")) {
        		memoryStringnumber = stringCount;
        	}
        	
        	if (stringCount >= inputCountString) {
//				System.out.println(line); 

					stringParts = line.split(" ");

					body.append(op);
					for (String word : stringParts) {
//						System.out.println(word); 
						for (int j = 0; j < stringDict.size(); j++) {
							if (word.equalsIgnoreCase(stringDict.get(j))) {
								body.append(obi+ word + cbi + " ");
								flag = true;
								break;
							}
						}
						if (flag != true) 
							body.append(word + " ");
						else
							flag = false;			
					}
					body.append(cp);
					
//					System.out.println(body.lastIndexOf(".")); 
//					System.out.println(body.substring(0,body.lastIndexOf(".")+1)); 
					
					StringBuilder untildota = new StringBuilder();
					StringBuilder afterdota = new StringBuilder();
		        	if (memoryStringnumber != 0) {
		        		untildota.append(body.substring(0,body.lastIndexOf(".")+1));
		        		untildota.append(cp);
		        		afterdota.append(op);
		        		afterdota.append(body.substring(body.lastIndexOf(".")+1));
		        	}
		        	else
		        		untildota.append(body);

	    	  		BufferedWriter qwerty = new BufferedWriter(new FileWriter("part"+i+".html"));
		        	qwerty.write(DOCTYPE);
					qwerty.write(http);
					qwerty.write(ohtml);
					qwerty.write(ohead);
					qwerty.write(meta);
					qwerty.write(chead);
					qwerty.write(obody);
					qwerty.write(untildota.toString());
					qwerty.write(cbody);
					qwerty.write(chtml);
					qwerty.close();
		      
					body.delete(0, body.length());
		        	if (memoryStringnumber != 0) {
		        		body.append(afterdota);
		        		stringCount = inputCountString - memoryStringnumber;
		        		memoryStringnumber = 0;
		        	}
		        	else
		        		stringCount = 0;
					i++;
			}
			else{
	        	stringParts = line.split(" ");

				body.append(op);
				for (String word : stringParts) {
					for (int k = 0; k < stringDict.size(); k++) {
						if (word.equalsIgnoreCase(stringDict.get(k))) {
							body.append(obi+ word + cbi + " ");
							flag = true;
							break;
						}
					}
					if (flag != true) 
						body.append(word + " ");
					else
						flag = false;	
				}
				body.append(cp);
			} 
        	stringCount++;
        }
        
    	if (stringCount > 1) {
	  		BufferedWriter qwerty = new BufferedWriter(new FileWriter("part"+i+".html"));
        	qwerty.write(DOCTYPE);
			qwerty.write(http);
			qwerty.write(ohtml);
			qwerty.write(ohead);
			qwerty.write(meta);
			qwerty.write(chead);
			qwerty.write(obody);
			qwerty.write(body.toString());
			qwerty.write(cbody);
			qwerty.write(chtml);
			qwerty.close();
      
			body.delete(0, body.length());
    	}
    	
		readerDict.close();
        inputStreamDict.close();
		readerDoc.close();
        inputStreamDoc.close();
		return "ok";
    }
  
	public static void main(String[] args) {
//************************************************************************
		int number_line = 0;
		File file_dict = new File("");
		File file_text = new File("");

		boolean flag_number = false;
		boolean flag_dict  = false;
		boolean flag_text = false;
		
		if (args.length == 0){
			System.err.println("ошибка: не заданы входные параметры");
		    return_help();  
		    return; 
		}	
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")){
				switch (args[i]) {
				case "-n":
					flag_number = true;
					flag_dict = false;
					flag_text = false;
				break;
				// разбор неправильных слешей/наклонок
				//"C:\Работа\Симбирсофт"
				case "-d":
					flag_number = false;
					flag_dict = true;
					flag_text = false;
				break;
				case "-t":
					flag_number = false;
					flag_dict = false;
					flag_text = true;
				break;
				default:
					System.err.println("ошибка: "+args[i]+" неизвестный аргумент");
					return_help();  
			    	return;
				} 
			}
			else if (flag_number) {
				try { 
					number_line = new Integer(args[i]);
					flag_number = false;
//			  		System.out.println(number_line);
			  		
					if ( number_line < 10 ) { 
						System.err.println("ошибка: числовой параметр меньше 10");
					    return_help();
					    return;
					}
					else if ( number_line > 100000 ) { 
						System.err.println("ошибка: числовой параметр больше 100 000");
					    return_help();
					    return;
					}	
				}catch (NumberFormatException e) {  
					System.err.println("ошибка: " + args[i] +" не числовой параметр");
				    return_help();
				    return;
				}
			}
			else if (flag_dict) {
				file_dict = new File(args[i]);
				flag_dict = false;
//				System.out.println(file_dict);
				
				String error = test_file(file_dict);
				if ( error != "ok") {
					System.err.println(error);
					return_help();
					return;
				}
			}
			else if (flag_text) {
				file_text = new File(args[i]);
				flag_text = false;
//				System.out.println(file_text);
				
				String error = test_file(file_text);
				if ( error != "ok") {
					System.err.println(error);
					return_help();
					return;
				}
			}	
		}
		
		if (number_line == 0){
			System.err.println("ошибка: не задано количество строк в выходных html-файлах");
			return_help();  
			return; 
		}
		else if (file_dict.toString() == ""){
			System.err.println("ошибка: не задан путь к файлу словаря");
			return_help();  
			return; 
		}
		else if (file_text.toString() == ""){
			System.err.println("ошибка: не задан путь к файлу с текстом");
			return_help();  
			return; 
		}
//************************************************************************

		String error = "";
		try {
			error = text_parser.findstring(file_dict,file_text, number_line);
			if ( error != "ok") {
				System.err.println(error);
				return_help();  
			}
		} catch (FileNotFoundException e) {
	        System.out.println("ошибка: не найден файл!"); 
//			e.printStackTrace();
		} catch (IOException e) {
	        System.out.println("ошибка: ввода вывода!"); 
//			e.printStackTrace();
		}

//	    text_parser.readFile("файл.txt"); 
//	    text_parser.readFile("словарь.txt");   
	} 

	public static String test_file(File input_file) {
		if (!input_file.exists()) {
			return "ошибка: данный файл: "+input_file.toString()+" не существует"; 
		}  
		else if (!input_file.isFile()) {
			return "ошибка: данный файл: "+input_file.toString()+" является директорией";  
		}
		else if (!input_file.canRead()) {
			return "ошибка: данный файл: "+input_file.toString()+" не доступен для чтения";  
		}
		else if (input_file.length() > 2097152 ) {
			return "ошибка: размер данного файла: "+input_file.toString()+" превышает 2Мб";  
		}
		return "ok";
	}
	
	public static void return_help() {
	    //!выдавать помощь на разных языках
	    System.err.println("Входные параметры:");
	    System.err.println("[-n] количество строк в выходных html-файлах в интервале от 10 строк до 100 000 строк");
	    System.err.println("[-d] путь к файлу словарю (размер файла не должен превышать 100 000 строк или 2 Мб)");
	    System.err.println("[-t] путь к файлу с текстом (размер файла не должен превышать 2 Мб)"); 
	    return;   
	}
}

/*public static String getFileContents(final File file) throws IOException {
    final InputStream inputStream = new FileInputStream(file);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream),2097152);
    	        
    final StringBuilder stringBuilder = new StringBuilder();
    
    boolean done = false;
    
    while (!done) {
        final String line = reader.readLine();
        done = (line == null);
        	            
        if (line != null) {
            stringBuilder.append(line);
            if (line.contains(".")) {
    	        System.out.println(line); 
            }
        }
    }
    
    reader.close();
    inputStream.close();
    
    return stringBuilder.toString();
}*/

/*private static void readFile(String fileName) { 
	try { 
      
       FileReader reader = new FileReader(fileName); 
       BufferedReader in = new BufferedReader(reader); 
       String string; 
       while ((string = in.readLine()) != null) { 
         System.out.println(string); 
       } 
       in.close(); 
     } catch (IOException e) { 
       e.printStackTrace(); 
     } 
}*/

//разбивание каждой подстроки
//stringParts = line.split(" ");
//System.out.println(line.lastIndexOf(".") ); 
	
//String[] words = line.split(" ");
//int j = 1;
//for (String word : words) {
// if (word.equalsIgnoreCase(inputFind)) {
//     System.out.println("Найдено в " + i + "-й строке, " + j + "-е слово");
// }
// j++;
//}