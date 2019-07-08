package cn.stylefeng.guns.core.common.constant;

import lombok.Data;

/**
 * 分页工具
 * @author huwei
 *
 */
public class PageConstant {

	//默认页数
	static final private Integer DEFAULT_PAGE = 0;
	//默认显示数
	static final private Integer DEFAULT_LIMIT = 20;
	
	static public PageInfo pageInfo(Integer page ,Integer limit) {
		return pageInfo(new PageInfo(page ,limit));
	} 
	
	static private PageInfo pageInfo(PageInfo pageInfo) {
		if(pageInfo == null || pageInfo.getPage() == null || pageInfo.getPage() < 0 
				|| pageInfo.getLimit() == null || pageInfo.getLimit() < 0) {
			return pageInfo();
		}
		pageInfo.setPage(pageInfo.getPage() - 1);
		return pageInfo;
	}
	
	static private  PageInfo pageInfo() {
		return new PageInfo(DEFAULT_PAGE, DEFAULT_LIMIT);
	}

	@Data
	static public class PageInfo{
		private Integer page;
		private Integer limit;
		PageInfo(Integer page2, Integer limit2) {
			this.page = page2;
			this.limit = limit2;
		}
	}
}
