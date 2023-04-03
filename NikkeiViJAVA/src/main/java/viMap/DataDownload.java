package viMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

// URLクラスを用いて日経平均に関するデータをダウンロードするクラス
public class DataDownload {
	private static String urlN225 = "https://indexes.nikkei.co.jp/nkave/historical/nikkei_stock_average_daily_jp.csv";
	private static String urlNVI = "https://indexes.nikkei.co.jp/nkave/historical/nikkei_stock_average_vi_daily_jp.csv";

	public static void dataDownload() {
		String dataPath = ViDataMake.getPath(); //データ保存先のパスを取得
		// 日経平均データ取得先からURLインスタンスを生成
		try {
			URL url1 = new URL(urlN225);
			LogMaker.logMake("URL connection start -> " + urlN225);
			URLConnection conect1 = url1.openConnection();
			InputStream input1 = conect1.getInputStream();
			// CSVファイルへ保存
			File file1 = new File(dataPath + "nikkei_stock_average_daily_jp.csv");
			LogMaker.logMake("file output start -> nikkei_stock_average_daily_jp.csv");
			FileOutputStream output1 = new FileOutputStream(file1, false);
			int i;
			while ((i = input1.read()) != -1) {
				output1.write(i);
			}
			output1.close();
			input1.close();
			LogMaker.logMake("data download finish -> " + urlN225);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			LogMaker.logMake("data download error -> " + urlN225);
		}

		// 次のファイル取得前のディレイ
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		// 日経平均Viデータ取得先からURLインスタンスを生成	
		try {
			URL url2 = new URL(urlNVI);
			LogMaker.logMake("URL connection start -> " + urlNVI);
			URLConnection conect2 = url2.openConnection();
			InputStream input2 = conect2.getInputStream();

			// CSVファイルへ保存
			File file2 = new File(dataPath + "nikkei_stock_average_vi_daily_jp.csv");
			LogMaker.logMake("file output start -> nikkei_stock_average_vi_daily_jp.csv");
			FileOutputStream output2 = new FileOutputStream(file2, false);
			int i;
			while ((i = input2.read()) != -1) {
				output2.write(i);
			}
			output2.close();
			input2.close();
			LogMaker.logMake("data download finish -> " + urlNVI);
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
			LogMaker.logMake("data download error -> " + urlNVI);
		}
	}
}
