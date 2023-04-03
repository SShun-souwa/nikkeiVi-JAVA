package viMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViDataMake {
	private static List<NikkeiData> allDataSet = new ArrayList<NikkeiData>(); // 全データを格納するのリスト
	private static List<NikkeiData> sqDataSet = new ArrayList<NikkeiData>(); // sq日のデータのみを格納するリスト
	private static List<NikkeiData> msqDataSet = new ArrayList<NikkeiData>(); // msq日のデータのみを格納するリスト
	private static List<String> preDateList = new ArrayList<String>(); //既存データの日付を格納するリスト
	private static String dataDirPath = "";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");  // 文字列から日付データへ変換するフォーマット
	private static SimpleDateFormat strDateFormat = new SimpleDateFormat("yyyy/MM/dd"); // 日付データから文字列へ変換するフォーマット

	public static void main(String[] args) throws ParseException, IOException {
		// CSVファイルのパスの指定
		Path p = Paths.get("").toAbsolutePath();
		dataDirPath = p.toString() + "\\DataSet\\";
		//System.out.println(appPath);
		//System.out.println(preDateList);
		//System.out.println(allDataSet);
		
		try {
			Files.createDirectory(Paths.get(dataDirPath));
			LogMaker.logMake("DataSet dir create");
		} catch (IOException e){
			LogMaker.logMake("DataSet dir exist");
		}
		
		oldDataCreate(); // 保持している過去データを処理
		allDataSet = readAlldataFile(allDataSet); // alldata.csvから既にデータを保持している日経平均データを読み込み
		newDataAppend(); // 新しいデータをダウンロードし、追加
		outCSV(); //CSVファイルへ書き出し
		LogMaker.logMake("complete");
	}

	//最新データの処理メソッド
	private static void newDataAppend() {
		sqDataSet.clear();
		msqDataSet.clear();
		String line = "";
		DataDownload.dataDownload(); 
		Path pathNk = Paths.get(dataDirPath + "nikkei_stock_average_daily_jp.csv");
		Path pathVi = Paths.get(dataDirPath + "nikkei_stock_average_vi_daily_jp.csv");

		// 日経平均CSVファイルの読み込み
		List<String> linesNk;
		try {
			LogMaker.logMake("file read start ->" + "nikkei_stock_average_daily_jp.csv");
			linesNk = Files.readAllLines(pathNk,Charset.forName("MS932"));
			LogMaker.logMake("file read ok");
			for (int i = 1; i < linesNk.size() - 1; i++) {
				// 読み込んだ行（文字列）中の"を削除
				line = linesNk.get(i).replaceAll("\"","");
				// 読み込んだ行の要素を,で分割してリストに格納
				String[] data = line.split(",");
				// 既存データにない日付の場合のみ、allDataSetに追加する
				if (preDateList.contains(data[0]) == false) {
					/*	１日毎のデータを一時格納するインスタンスを生成
						日付データをString型→Date型に変更し、日経平均データインスタンス生成 */
					NikkeiData onedayDataSet = new NikkeiData(dateFormat.parse(data[0]));
					// それ以外のデータはdouble型でインスタンス変数に格納
					onedayDataSet.indexClose = Double.parseDouble(data[1]);
					onedayDataSet.indexOpen = Double.parseDouble(data[2]);
					// 全データを格納するリストに１日ごとのデータを集計したインスタンスを格納
					allDataSet.add(onedayDataSet);
				}
			}
			LogMaker.logMake("create ni225 dataset complete");
		} catch (IOException | ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			LogMaker.logMake("create data from nikkei_stock_average_daily_jp.csv ERROR");
		}

		// 日経平均ViのCSVファイルの読み込み
		List<String> linesVi;
		try {
			LogMaker.logMake("file read start ->" + "nikkei_stock_average_vi_daily_jp.csv");
			linesVi = Files.readAllLines(pathVi,Charset.forName("MS932"));
			LogMaker.logMake("file read ok");
			for (int i = 1; i < linesVi.size() - 1; i++) {
				// 読み込んだ行（文字列）中の"を削除
				line = linesVi.get(i).replaceAll("\"","");
				// 読み込んだ行の要素を,で分割してリストに格納
				String[] data = line.split(",");
				for (int j = 0; j < allDataSet.size(); j++) {
					// 日付が合致する要素にViのCSVから読み込んだデータを追加格納
					if (allDataSet.get(j).date.equals(dateFormat.parse(data[0]))) {
						allDataSet.get(j).viClose = Double.parseDouble(data[1]);
						allDataSet.get(j).viOpen = Double.parseDouble(data[2]);
						break;
					}
				}
			}
			LogMaker.logMake("create vi dataset complete");
		} catch (IOException | NumberFormatException | ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			LogMaker.logMake("create data from nikkei_stock_average_daily_jp.csv ERROR");
		}
		selectSQData();
	}

	// 過去データ(2001年1月初-2022年12月末)のデータ処理メソッド
	private static void oldDataCreate() {
		String line = "";
		String[] pathNks = {"ni01.csv","ni02.csv","ni03.csv"}; //過去ファイル名
		String[] pathVis = {"vi01.csv","vi02.csv","vi03.csv"};

		for (int k = 0; k < pathNks.length; k++) {
			LogMaker.logMake("file read start ->" + dataDirPath + pathNks[k]);
			Path pathNk = Paths.get(dataDirPath + pathNks[k]);
			// 日経平均CSVファイルの読み込み
			List<String> linesNk;
			try {
				linesNk = Files.readAllLines(pathNk,Charset.forName("MS932"));
				LogMaker.logMake("file read ok");
				for (int i = 1; i < linesNk.size() - 1; i++) {
					// 読み込んだ行（文字列）中の"を削除
					line = linesNk.get(i).replaceAll("\"","");
					//System.out.println(line);
					// 読み込んだ行の要素を,で分割してリストに格納
					String[] data = line.split(",");
					/* １日毎のデータを一時格納するインスタンスを生成
						日付データをString型→Date型に変更し、日経平均データインスタンス生成 */
					NikkeiData onedayDataSet = new NikkeiData(dateFormat.parse(data[0]));
					// それ以外のデータは文字列のまま全てインスタンス変数に格納
					onedayDataSet.indexClose = Double.parseDouble(data[1]);
					onedayDataSet.indexOpen = Double.parseDouble(data[2]);
					// 全データを格納するリストに１日ごとのデータを集計したインスタンスを格納
					allDataSet.add(onedayDataSet);
				}
			} catch (IOException | ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				LogMaker.logMake("create data from" + dataDirPath + pathNks[k] + "ERROR");
			}
		}
		LogMaker.logMake("create ni225 dataset complete");

		for (int k = 0; k < pathVis.length; k++) {
			LogMaker.logMake("file read start ->" + dataDirPath + pathVis[k]);
			Path pathVi = Paths.get(dataDirPath + pathVis[k]);
			// 日経平均ViのCSVファイルの読み込み
			List<String> linesVi;
			try {
				linesVi = Files.readAllLines(pathVi,Charset.forName("MS932"));
				LogMaker.logMake("file read ok");
				for (int i = 1; i < linesVi.size() - 1; i++) {
					// 読み込んだ行（文字列）中の"を削除
					line = linesVi.get(i).replaceAll("\"","");
					// 読み込んだ行の要素を,で分割してリストに格納
					String[] data = line.split(",");
					for (int j = 0; j < allDataSet.size(); j++) {
						// 日付が合致する要素にViのCSVから読み込んだデータを追加格納
						if (allDataSet.get(j).date.equals(dateFormat.parse(data[0]))) {
							allDataSet.get(j).viClose = Double.parseDouble(data[1]);
							allDataSet.get(j).viOpen = Double.parseDouble(data[2]);
							break;
						}
					}
				}
			} catch (IOException | NumberFormatException | ParseException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				LogMaker.logMake("create data from" + dataDirPath + pathVis[k] + "ERROR");
			}
		} 
		LogMaker.logMake("create vi dataset complete");
		selectSQData();
		outCSV();
	}

	// SQ日部分を抽出するメソッド
	private static void selectSQData(){
		LogMaker.logMake("select SQ Data start");
		// 日付の比較を行うためのCalendarクラスインスタンスを生成
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		Calendar c3 = Calendar.getInstance();
		// 月を跨いだか判定するboolean型
		boolean nextmonth = true;
		// 何週目かをカウントする変数
		int weekcount = 2;
		for (int i = 2; i < allDataSet.size(); i++) {
			// 日付比較用のインスタンスへ日付を入力
			c1.setTime(allDataSet.get(i-2).date);
			c2.setTime(allDataSet.get(i-1).date);
			c3.setTime(allDataSet.get(i).date);
			// 月を跨いだ判定の場合処理を実行
			if (nextmonth) {
				// 第３営業週の初日にif以下を実行
				if (weekcount == 3) {
					// SQ日の日経平均データインスタンスを生成し、始値と前営業日のVi終値を格納
					NikkeiData onedayDataSet = new NikkeiData(allDataSet.get(i-2).date);
					onedayDataSet.viClose = allDataSet.get(i-3).viClose;
					onedayDataSet.indexOpen = allDataSet.get(i-2).indexOpen;
					// 月を跨がない第３週以降の処理はスルーするため、nextmonthをfalseに
					nextmonth = false;
					// SQ日用のArrayListへ格納
					sqDataSet.add(onedayDataSet);
					// MSQの場合は別途で追加格納　JUN=1 なので3の倍数月は3で除算した余りが2となる
					if (c3.get(Calendar. MONTH) % 3 == 2) {
						msqDataSet.add(onedayDataSet);
					}
				} else {
					// 曜日から週を跨いだかを判定し、weekcountを進める
					if (c2.get(Calendar. DAY_OF_WEEK) > c3.get(Calendar. DAY_OF_WEEK)) {
						weekcount += 1;
					}
				}
			}
			// 月を跨いだ場合にnextmonthとweekcountを変更
			if (c2.get(Calendar. MONTH) < c3.get(Calendar. MONTH)) {
				nextmonth = true;
				weekcount = 1;
			}
			// 年を跨いだ場合にnextmonthとweekcountを変更
			if (c2.get(Calendar. YEAR) < c3.get(Calendar. YEAR)) {
				nextmonth = true;
				weekcount = 1;
			}
		}
		LogMaker.logMake("select SQ Data finish");
	}

	// 既存のalldata.csvファイルを読み込むメソッド
	public static List<NikkeiData> readAlldataFile(List<NikkeiData> allDataSet) {
		String line = "";
		List<String> lines;
		Path path = Paths.get(dataDirPath + "alldata.csv");
		try {
			LogMaker.logMake("read file ->" + "alldata.csv");
			lines = Files.readAllLines(path,Charset.forName("MS932"));
			LogMaker.logMake("read file succeed");
			for (int i = 1; i < lines.size() - 1; i++) {
				// 読み込んだ行（文字列）中の"を削除
				line = lines.get(i).replaceAll("\"","");
				// 読み込んだ行の要素を,で分割してリストに格納
				String[] data = line.split(",");
				/*	１日毎のデータを一時格納するインスタンスを生成
					日付データをString型→Date型に変更し、日経平均データクラスのコンストラクタを実行 */
				preDateList.add(data[0]);
				NikkeiData onedayDataSet = new NikkeiData(dateFormat.parse(data[0]));
				// それ以外のデータはdouble型でインスタンス変数に格納
				onedayDataSet.indexOpen = Double.parseDouble(data[1]);
				onedayDataSet.indexClose = Double.parseDouble(data[2]);
				onedayDataSet.viOpen = Double.parseDouble(data[3]);
				onedayDataSet.viClose = Double.parseDouble(data[4]);
				// 全データを格納するリストに１日ごとのデータを集計したインスタンスを格納
				allDataSet.add(onedayDataSet);
			}
			LogMaker.logMake("read file succeed");
			return allDataSet;
		} catch (IOException | ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();	
			LogMaker.logMake("alldata.csv file set failed");
			return null;
		} 
	}

	// CSVファイルへの出力メソッド
	private static void outCSV() {

		//全sq日データのCSVファイルへの出力
		FileWriter fw1;
		try {
			LogMaker.logMake("start write file sqdata.csv");
			fw1 = new FileWriter(dataDirPath + "sqdata.csv", false);
			PrintWriter pw1 = new PrintWriter(new BufferedWriter(fw1));
			pw1.print("date");
			pw1.print(",");
			pw1.print("indexOpen");
			pw1.print(",");
			pw1.print("viClose");
			pw1.println();
			for (int i = 0; i < sqDataSet.size(); i++) {
				pw1.print(strDateFormat.format(sqDataSet.get(i).date));
				pw1.print(",");
				pw1.print(sqDataSet.get(i).indexOpen);
				pw1.print(",");
				pw1.print(sqDataSet.get(i).viClose);
				pw1.print(",");
				pw1.println();
			}
			pw1.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			LogMaker.logMake("write file failed sqdata.csv");
		}
		LogMaker.logMake("write file succeed sqdata.csv");

		// msq日データのCSVファイルへの出力
		FileWriter fw2;
		try {
			LogMaker.logMake("start write file msqdata.csv");
			fw2 = new FileWriter(dataDirPath + "msqdata.csv", false);
			PrintWriter pw2 = new PrintWriter(new BufferedWriter(fw2));
			pw2.print("date");
			pw2.print(",");
			pw2.print("indexOpen");
			pw2.print(",");
			pw2.print("viClose");
			pw2.println();
			for (int i = 0; i < msqDataSet.size(); i++) {
				pw2.print(strDateFormat.format(msqDataSet.get(i).date));
				pw2.print(",");
				pw2.print(msqDataSet.get(i).indexOpen);
				pw2.print(",");
				pw2.print(msqDataSet.get(i).viClose);
				pw2.print(",");
				pw2.println();
			}
			pw2.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			LogMaker.logMake("write file failed sqdata.csv");
		}
		LogMaker.logMake("write file succeed msqdata.csv");
		
		// 全ての日データのCSVファイルへの出力
		FileWriter fw3;
		try {
			LogMaker.logMake("start write file alldata.csv");
			fw3 = new FileWriter(dataDirPath + "alldata.csv", false);
			PrintWriter pw3 = new PrintWriter(new BufferedWriter(fw3));
			pw3.print("date");
			pw3.print(",");
			pw3.print("indexOpen");
			pw3.print(",");
			pw3.print("indexClose");
			pw3.print(",");
			pw3.print("viOpen");
			pw3.print(",");
			pw3.print("viClose");
			pw3.println();
			for (int i = 0; i < allDataSet.size(); i++) {
				pw3.print(strDateFormat.format(allDataSet.get(i).date));
				pw3.print(",");
				pw3.print(allDataSet.get(i).indexOpen);
				pw3.print(",");
				pw3.print(allDataSet.get(i).indexClose);
				pw3.print(",");
				pw3.print(allDataSet.get(i).viOpen);
				pw3.print(",");
				pw3.print(allDataSet.get(i).viClose);
				pw3.print(",");
				pw3.println();
			}
			pw3.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			LogMaker.logMake("write file failed alldata.csv");
		}
		LogMaker.logMake("write file succeed alldata.csv");
	}
	
	// pathのゲッターメソッド
	public static String getPath() {
		return dataDirPath;
	}

}
