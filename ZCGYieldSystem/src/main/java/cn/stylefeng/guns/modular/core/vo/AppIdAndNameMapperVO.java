package cn.stylefeng.guns.modular.core.vo;

public class AppIdAndNameMapperVO {

	private Long id;
	
	private String name;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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

	public AppIdAndNameMapperVO(Long id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public AppIdAndNameMapperVO() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AppIdAndNameMapperVO [id=" + id + ", name=" + name + "]";
	}
	
}
