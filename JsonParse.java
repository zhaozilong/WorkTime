import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;


public class JsonParse {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String DateAddress = "src/month.json";
		String month = "201504";
		 // jsonデータの作成
        JSONObject jsonOneData = new JSONObject();
        try {
			jsonOneData.put("workDay", "2015-04-04");
	        jsonOneData.put("workStart", "9:00");
	        jsonOneData.put("workEnd", "18:00");
	        jsonOneData.put("workTime", 8);
	        jsonOneData.put("salaryUp", 0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		try {
			writeJson(DateAddress,month,jsonOneData);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param fileAddress jsonファイルの場所
	 * @param month　入力した月
	 * @return json 入力した月対応するデータ
	 */
	public static JSONObject readJson(String fileAddress,String month)
	{//return the data of month
		InputStream input;
		try {
		    input = new FileInputStream(fileAddress);
		    int size = input.available();
		    byte[] buffer = new byte[size];
		    input.read(buffer);
		    input.close();
		    // Json読み込み
		    String json = new String(buffer);
		    JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(json);
				JSONObject jsonArray = jsonObject.getJSONObject(month);

					// JSONObject jsonOneRecord = jsonArray.getJSONObject(i);
					 System.out.println(jsonArray.getString("totalTime"));
				 
				return jsonArray;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		    // データ追加
		   
		}catch(IOException e)
		{
			System.out.println("");
		}
		return null;
	}
	public static void writeJson(String fileAddress,String month,JSONObject jsonDayData) throws IOException, ParseException
	{
		/*
		 * 1.DATAのファイルに月に対応するデータがあるかどうか判断する
		 * 2．ない場合、JSONデータを追加前、月のデータを組む合わせる。
		 * 3．ある場合、JSONデータを追加して、月のデータを計算する。
		 * ――――＞週末、祝日の判断
		 */
		InputStream input;
		try {
			input = new FileInputStream(fileAddress);
			int size = input.available();
			byte[] buffer = new byte[size];
			input.read(buffer);
			input.close();
			// Json読み込み
			String json = new String(buffer);

			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonMonth = jsonObject.getJSONObject(month);
			//時間の単位と深夜の時間
			int workUnit = jsonObject.getInt("timeUnit");
			String nightStart = jsonObject.getString("nightStart");
			String nightEnd = jsonObject.getString("nightEnd");
			JSONArray jsonDayArray = jsonMonth.getJSONArray("monthlist");
			boolean changed = true;
			for (int i = 0; i < jsonDayArray.length(); i++) {
				JSONObject jsonDay = jsonDayArray.getJSONObject(i);
				System.out.println(jsonDay.getString("workDay") +"前と後ろの区別は："+jsonDayData
						.getString("workDay"));
				if (jsonDay.getString("workDay").equals(jsonDayData
						.getString("workDay"))) {// 同じな日のデータを存在した場合、更新する
					jsonDay = jsonDayData;
					changed = false;
				}
								
			}
			if(changed)
			{
				jsonDayArray.put(jsonDayData);
			}
			/*勤務時間(totalTime):今月の全部勤務時間
			 *　祝日、週末(weekendTime):週末と祝日の勤務時間
			 *深夜時間(extraTime):夜10：００以後の勤務時間
			 *理論労働時間(Nomaltime):８＊平日
			 */
			int totalTime = 0;
			int weekendTime = 0;
			int extraTime = 0;
			int nomalTime = 0;
			for (int i = 0; i < jsonDayArray.length(); i++) {
				JSONObject jsonDay = jsonDayArray.getJSONObject(i);
				totalTime += jsonDay.getInt("workTime");
				extraTime += jsonDay.getInt("salaryUp");
				String workDay = jsonDay.getString("workDay");
				String[] tempDay = workDay.split("-");

				workDay = tempDay[0]+"/"+tempDay[1]+"/"+tempDay[2].toString(); 				
				if(KtHoliday.getHolidayName(workDay)!=""||KtHoliday.getWeekend(jsonDay.getString("workDay")))
				{
					weekendTime +=jsonDay.getInt("workTime");
				}else
				{
					nomalTime += 8;
				}
			}
			jsonMonth.put("totalTime",totalTime);
			jsonMonth.put("weekendTime",weekendTime);
			jsonMonth.put("extraTime",extraTime);
			jsonMonth.put("NomalTime",nomalTime);
			FileWriter filewriter;

			filewriter = new FileWriter(fileAddress);
			BufferedWriter bw = new BufferedWriter(filewriter);
			PrintWriter pw = new PrintWriter(bw);
			pw.write(jsonObject.toString());
			pw.close();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

