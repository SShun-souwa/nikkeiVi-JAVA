package viMap;

import java.util.Date;

//日経平均のデータを格納するクラス
class NikkeiData {

	public static String indexName = "NI225";
	public Date date;
	public double indexClose;
	public double indexOpen;
	public double viClose;
	public double viOpen;
	public double hv;

	//　インスタンス変数は終値、始値、Viの終値、Viの始値
	NikkeiData(Date date) {
		this.date = date;
		this.indexClose = 0.0;
		this.indexOpen = 0.0;
		this.viClose = 0.0;
		this.viOpen = 0.0;
		this.hv = 0.0;
	}
	
	// インスタンス変数をコンソール表示するクラスメソッド
	public void allPrint(){
		System.out.print(this.date + ", ");
		System.out.print(this.indexClose + ", ");
		System.out.print(this.indexOpen + ", ");
		System.out.print(this.viClose + ", ");
		System.out.print(this.viOpen + ",");
		System.out.print(this.hv + "\n");
	}

}