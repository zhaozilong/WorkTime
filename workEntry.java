
public class WorkEntry {
	
	private int timeUnit;
	private String nightStart;
	private String nightEnd;
	private int communteFee;
	private String workName;
	private int workId;
	
	void workEntry(int timeUnit,String nightStart,String nightEnd,int communteFee,String workName,int workId)
	{
		/*
		 * jsonデータがあるかどうか判断する
		 * ないと、作る、
		 * 仕事IDにより、JSONの名前を決める
		 */
		this.setCommunteFee(communteFee);
		this.setNightEnd(nightEnd);
		this.setNightStart(nightStart);
		this.setTimeUnit(timeUnit);
		this.setWorkId(workId);
		this.setWorkName(workName);
		
	}
	public int getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(int timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getNightStart() {
		return nightStart;
	}

	public void setNightStart(String nightStart) {
		this.nightStart = nightStart;
	}

	public String getNightEnd() {
		return nightEnd;
	}

	public void setNightEnd(String nightEnd) {
		this.nightEnd = nightEnd;
	}

	public int getCommunteFee() {
		return communteFee;
	}

	public void setCommunteFee(int communteFee) {
		this.communteFee = communteFee;
	}

	public String getWorkName() {
		return workName;
	}

	public void setWorkName(String workName) {
		this.workName = workName;
	}

	public int getWorkId() {
		return workId;
	}

	public void setWorkId(int workId) {
		this.workId = workId;
	}
  
}
