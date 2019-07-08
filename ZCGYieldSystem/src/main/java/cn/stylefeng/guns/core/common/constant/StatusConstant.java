package cn.stylefeng.guns.core.common.constant;

public interface StatusConstant {
	/**
	 * 数据提交，提交用户与管理员可见
	 */
	final static public String COMMIT = "提交";
	/**
	 * 数据审核中，提交用户与管理员可见
	 */
	final static public String AUDIT = "审核";
	/**
	 * 数据审核失败，提交用户与管理员可见
	 */
	final static public String FAILED = "失败";
	/**
	 * 数据审核通过，且上架到平台之中，对数据可见用户都可见；
	 */
	final static public String SHELVES = "上架";
	/**
	 * 数据审核通过，用户自定义的下架数据，则只有提交用户与管理员可见
	 */
	final static public String SOLD_OUT = "下架";
	/**
	 * 数据被标记为删除状态，在删除规则之前有可恢复的时机，反之，则数据将被真的删除；
	 */
	final static public String MARK_DELETE = "标记删除";
	
	static public boolean isLegal(String status) {
		if(COMMIT.equals(status) || AUDIT.equals(status) || FAILED.equals(status) || SHELVES.equals(status)
				|| SOLD_OUT.equals(status) || MARK_DELETE.equals(status)) {
			return true;
		}
		return false;
	}
	
	static public String constant() {
		return "状态可选参数，及其说明：[" + COMMIT + "]：用户提交；[" + AUDIT + "]：审核中 ；[" + FAILED + "]：审核失败；"
				+ "[" + SHELVES + "]：用户可见；[" + SOLD_OUT + "]：用户不可见 ；[" + MARK_DELETE + "]：在系统删除规则时间内可恢复；";
	}
}
