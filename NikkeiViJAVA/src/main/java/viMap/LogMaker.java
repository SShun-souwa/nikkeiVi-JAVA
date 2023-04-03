package viMap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

// ログを出力するクラス
public class LogMaker {
	private static String logPath;

	// ログの書き出しを行うメソッド
	public static void logMake(String str) {
		logPath =ViDataMake.getPath() + "applog.log";
		File file = new File(logPath);
		try {
			if(file.exists() == false) {
				FileWriter filewriter = new FileWriter(file, true);
				filewriter.close();
			}
			if(checkBeforeWritefile(file)) {
				FileWriter filewriter = new FileWriter(file,true);
				Date date = new Date();
				filewriter.write(date + " , " + str + "\r\n");
				filewriter.close();
				// ログファイルサイズが1MBを超えた場合リセットする
				if(Files.size(Paths.get(logPath)) > 1000000) {
					logReset();
				}
			} else {
				System.out.println("ログファイルが存在しないか使用中のため、書き込みできません");
			}
		} catch (IOException e){
			System.out.println(e);
		}
	}

	// ログファイルの書き込み可否をチェックするメソッド
	private static boolean checkBeforeWritefile(File file) {
		if(file.exists()) {
			if(file.isFile() && file.canWrite()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// ログファイルをリセットするメソッド
	public static void logReset(){
		try {
			Date date = new Date();
			File file = new File(logPath);
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(date + " , log Reset"  + "\r\n");
			filewriter.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
