package cn.stylefeng.guns.modular.core.vo;

import lombok.Data;

@Data
public class VisitCountVo {
	private String visitTime;
	
	private String appName;
	
	private String channelName;
	
	private String company;
	
	private String userName;
	
	private String os;
	
	private Long count;

	public VisitCountVo(String visitTime, String appName, String channelName, String company, String userName,
			Long count) {
		super();
		this.visitTime = visitTime;
		this.appName = appName;
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.count = count;
	}
	
	public VisitCountVo() {
		super();
	}

	public VisitCountVo(String visitTime, String appName, String channelName, String company, String userName,
			String os, Long count) {
		super();
		this.visitTime = visitTime;
		this.appName = appName;
		this.channelName = channelName;
		this.company = company;
		this.userName = userName;
		this.os = os;
		this.count = count;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VisitCountVo [visitTime=" + visitTime + ", appName=" + appName + ", channelName=" + channelName
				+ ", company=" + company + ", userName=" + userName + ", os=" + os + ", count=" + count + "]";
	}

}
