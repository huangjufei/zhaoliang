//package cn.stylefeng.guns.core.util.resources;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.UUID;
//
//import org.apache.commons.io.FileUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.stereotype.Component;
//
//import cn.stylefeng.guns.core.shiro.ShiroKit;
//import cn.stylefeng.guns.modular.core.entity.Resources;
//import lombok.extern.slf4j.Slf4j;
//
///**
// *	 本地文件存储类资源工具
// * @author huwei
// */
//@Slf4j
//@Component
//@PropertySource(value= {"classpath:/config/properties/pro-resource.properties"})
//public class LocalFileResourceStore implements ResourceStore{
//
//	@Value("${resource.dir}")
//	private String dir;
//	@Value("${system.ip}")
//	private String ip;
//	@Value("${system.port}")
//	private Integer port;
//	@Value("${base.url}")
//	private String baseUrl;
//	
//	@Override
//	public byte[] findResource(Resources resources) {
//		try {
//			return FileUtils.readFileToByteArray(new File(path(resources ,resources.getCreateId() + "")));
//		} catch (IOException e) {
//			log.info("---> find resource file failed ,resource path : " + path(resources) ,e);
//		}
//		return null;
//	}
//
//	@Override
//	public boolean delResource(Resources resources) {
//		File file = new File(path(resources));
//		if(file.isDirectory()) {
//			try {
//				FileUtils.deleteDirectory(file);
//			} catch (IOException e) {
//			}
//		}
//		else {
//			FileUtils.deleteQuietly(file);
//		}
//		//删除空目录
//		try {
//			delEmptyParentDirectory(file.getParent());
//		} catch (Exception e) {
//		}
//		return true;
//	}
//	
//	private void delEmptyParentDirectory(String directoryPath) throws IOException {
//		File file = new File(directoryPath);
//		if(!file.exists() || !file.isDirectory()) {
//			return;
//		}
//		File[] files = file.listFiles();
//		if(files == null || files.length != 0) {
//			return ;
//		}
//		FileUtils.deleteDirectory(file);
//		delEmptyParentDirectory(file.getParent());
//	}
//
//	@Override
//	public boolean saveResource(Resources resources, File file) {
//		try {
//			return saveResource(resources, FileUtils.readFileToByteArray(file));
//		} catch (IOException e) {
//			log.info("---> save resource file failed ,resource path : " + path(resources) + " and resource file : " + (file == null ? "file is null" : file.getAbsolutePath()),e);
//			return false;
//		}
//	}
//
//	@Override
//	public boolean saveResource(Resources resources, byte[] fileContent) {
//		//参数设置
//		resources.setDir(this.dir);
//		resources.setStoreType("本地文件");
//		resources.setSysKey(UUID.randomUUID().toString().replace("-", "").toUpperCase());
//		try {
//			FileUtils.writeByteArrayToFile(generateFile(resources), fileContent);
//		} catch (IOException e) {
//			log.info("---> save resource file failed ,resource path : " + path(resources) + " and resource file : " + fileContent,e);
//			return false;
//		}
//		return true;
//	}
//
//	@Override
//	public boolean saveResource(Resources resources, String fileContent) {
//		return saveResource(resources, fileContent.getBytes());
//	}
//
//	
//	private File generateFile(Resources resources) {
//		return new File(path(resources));
//	}
//
//	@Override
//	public String downloadUrl(Resources resource) {
//		return "http://" + this.ip + ":" + this.port + "/" + this.baseUrl + "/" + resource.getUserKey();
//	}
//	
//	private String path(Resources resources) {
//		return path(resources, userInfo());
//	}
//	
//	private String path(Resources resources ,String userInfo) {
//		return this.dir + "/" + userInfo +"/" + resources.getSign() + "/" + resources.getSysKey() 
//			+ "/" + resources.getFileName();
//	}
//	
//	private String userInfo() {
//		return ShiroKit.getUser() == null ? "tourist" : ShiroKit.getUser().getId() + "";
//	}
//}
