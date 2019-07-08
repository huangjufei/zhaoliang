package cn.stylefeng.guns.modular.core.vo;

public class UserBrowseRecordVo {

	private String name ;
	
	private Integer count;

	public UserBrowseRecordVo(String name, Integer count) {
		super();
		this.name = name;
		this.count = count;
	}

	public UserBrowseRecordVo() {
		super();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
}
