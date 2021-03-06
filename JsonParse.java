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
			jsonOneData.put("workDay", "2015-04-03");
	        jsonOneData.put("workStart", "9:00");
	        jsonOneData.put("workEnd", "23:00");
	        jsonOneData.put("workTime", 8);
	        jsonOneData.put("salaryUp", 0);
	        jsonOneData.put("restCount", 0);
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
		/* 時間登録と変更する時
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
				if (jsonDay.getString("workDay").equals(jsonDayData
						.getString("workDay"))) {// 同じな日のデータを存在した場合、更新する
					
					jsonDay = CaluateDayTime(jsonDayData,workUnit,nightStart,nightEnd);
					jsonDayArray.put(i,jsonDay);
					changed = false;
				}
								
			}
			if(changed)
			{
				jsonDayArray.put(CaluateDayTime(jsonDayData,workUnit,nightStart,nightEnd));
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
/**
 * @param dayData
 * @param timeUint
 * @param nightStart
 * @param nightEnd
 * @return
 */

public static JSONObject CaluateDayTime(JSONObject dayData,int timeUint,String nightStart,String nightEnd)
{
		double workTime = 0;
	try {
		String workStart = dayData.getString("workStart");
		String workEnd = dayData.getString("workEnd");
		int restCount = dayData.getInt("restCount");
		if(restCount == 0)
		{
			workTime =KtHoliday.calTime(workStart, workEnd,timeUint);
		}else
		{
			JSONObject array = (JSONObject) dayData.get("restTimeList");
			for(int i = 0;i<restCount;i++)
			{
				String start = "start"+(i+1);
				String end = "end"+(i+1);
				workEnd = array.getString(start);
				workTime += KtHoliday.calTime(workStart, workEnd,timeUint);
				workStart =  array.getString(end);
			}
		}
		dayData.put("workTime", workTime);
		/*
		 * 深夜の計算ーーー＞日跨るはまだですが、徹夜した場合、二日に入力してください
		 * */
		double salaryUp = 0;
		if(KtHoliday.calTime(nightStart,dayData.getString("workEnd"),timeUint)>0)
		{
			salaryUp += KtHoliday.calTime(nightStart,dayData.getString("workEnd"),timeUint);
		}else if(KtHoliday.calTime(dayData.getString("workStart"),nightEnd,timeUint)>0)
		{
			salaryUp += KtHoliday.calTime(dayData.getString("workStart"),nightEnd,timeUint);
		}
		dayData.put("salaryUp", salaryUp);
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return dayData;
	
}
	/**
	 * @param work
	 * @param fileAddress
	 * jsonファイルが無いとき、作る
	 */
	public static void fileCreate(WorkEntry work, String fileAddress) {
		int workId = work.getWorkId();
		int commuteFee = work.getCommunteFee();
		int timeUnit = work.getTimeUnit();
		String workName = work.getWorkName();
		String nightStart = work.getNightStart();
		String nightEnd = work.getNightEnd();
		fileAddress = fileAddress + work.getWorkId() + ".json";
		File file = new File(fileAddress);
		if (!file.exists()) {
			JSONObject jsonOneData = new JSONObject();
			try {
				jsonOneData.put("workId", workId);
				jsonOneData.put("workName", workName);
				jsonOneData.put("commuteFee", commuteFee);
				jsonOneData.put("nightStart", nightStart);
				jsonOneData.put("nightEnd", nightEnd);

				FileWriter filewriter;
				try {
					filewriter = new FileWriter(fileAddress);
					BufferedWriter bw = new BufferedWriter(filewriter);
					PrintWriter pw = new PrintWriter(bw);
					pw.write(jsonOneData.toString());
					pw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}

