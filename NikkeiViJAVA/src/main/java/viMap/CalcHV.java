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
import java.util.List;

public class CalcHV {
	
	private static List<NikkeiData> allDataSet = new ArrayList<NikkeiData>(); // 全データを格納するのリスト
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");  // 文字列から日付データへ変換するフォーマット
	private static SimpleDateFormat strDateFormat = new SimpleDateFormat("yyyy/MM/dd"); // 日付データから文字列へ変換するフォーマット
	
	public static void main() {
		calcHv();
		//writeCSV();
	}
	
	public static void calcHv() {
		readFile();
		
	}
	private static void readFile() {
		String line = "";
		List<String> lines;
		Path path = Paths.get(ViDataMake.getPath() + "alldata.csv");
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
				NikkeiData onedayDataSet = new NikkeiData(dateFormat.parse(data[0]));
				// それ以外のデータはdouble型でインスタンス変数に格納
				onedayDataSet.indexOpen = Double.parseDouble(data[1]);
				onedayDataSet.indexClose = Double.parseDouble(data[2]);
				onedayDataSet.viOpen = Double.parseDouble(data[3]);
				onedayDataSet.viClose = Double.parseDouble(data[4]);
				// 全データを格納するリストに１日ごとのデータを集計したインスタンスを格納
				allDataSet.add(onedayDataSet);
			}
		} catch (IOException | ParseException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();	
			LogMaker.logMake("alldata.csv file set failed");
		} 
		LogMaker.logMake("read file succeed");
	}
	
	private static void writeCSV() {
		// 全ての日データのCSVファイルへの出力
				FileWriter fw;
				try {
					LogMaker.logMake("start write file alldata.csv");
					fw = new FileWriter(ViDataMake.getPath() + "alldata.csv", false);
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					pw.print("date");
					pw.print(",");
					pw.print("indexOpen");
					pw.print(",");
					pw.print("indexClose");
					pw.print(",");
					pw.print("viOpen");
					pw.print(",");
					pw.print("viClose");
					pw.print(",");
					pw.print("hv");
					pw.println();
					for (int i = 0; i < allDataSet.size(); i++) {
						pw.print(strDateFormat.format(allDataSet.get(i).date));
						pw.print(",");
						pw.print(allDataSet.get(i).indexOpen);
						pw.print(",");
						pw.print(allDataSet.get(i).indexClose);
						pw.print(",");
						pw.print(allDataSet.get(i).viOpen);
						pw.print(",");
						pw.print(allDataSet.get(i).viClose);
						pw.print(",");
						pw.print(allDataSet.get(i).hv);
						pw.print(",");
						pw.println();
					}
					pw.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					LogMaker.logMake("write file failed alldata.csv");
				}
				LogMaker.logMake("write file succeed alldata.csv");
	}
}
