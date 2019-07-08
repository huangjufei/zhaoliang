package cn.stylefeng.guns.core.util.resources;

import java.io.File;

import cn.stylefeng.guns.modular.core.entity.Resources;

/**
 * 	资源工具接口
 * @author huwei
 *
 */
public interface ResourceStore {
	
	byte[] findResource(Resources resources);
	
	boolean delResource(Resources resources);
	
	boolean saveResource(Resources resources,File file);
	
	boolean saveResource(Resources resources,byte[] fileContent);
	
	boolean saveResource(Resources resources,String fileContent);
	
	String downloadUrl(Resources resource);
}
