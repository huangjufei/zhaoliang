package cn.stylefeng.guns.core.util.resources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.stylefeng.guns.core.util.AliyunOSSUtil;
import cn.stylefeng.guns.modular.core.entity.Resources;
import lombok.extern.slf4j.Slf4j;

/**
 *	oss文件存储
 * @author huwei
 */
@Slf4j
@Component
public class OSSResourceStore implements ResourceStore{
	
	final private String rootName = "zcshouliangSystem/";

	@Autowired
	private AliyunOSSUtil aliyunOSSUtil;
	
	@Override
	public byte[] findResource(Resources resources) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		aliyunOSSUtil.downOutputStream(baos,ossKey(resources));
		return baos.toByteArray();
	}

	@Override
	public boolean delResource(Resources resources) {
		aliyunOSSUtil.deleteFileFromOSS(ossKey(resources));
		return true;
	}
	
	@Override
	public boolean saveResource(Resources resources, File file) {
		try {
			return saveResource(resources, FileUtils.readFileToByteArray(file));
		} catch (IOException e) {
			log.info("---> save resource file failed " + (file == null ? "file is null" : file.getAbsolutePath()),e);
			return false;
		}
	}

	@Override
	public boolean saveResource(Resources resources, byte[] fileContent) {
		System.err.println(aliyunOSSUtil.toString());
		//参数设置
		resources.setDir("");
		resources.setStoreType("OSS");
		resources.setSysKey(UUID.randomUUID().toString().replace("-", "").toUpperCase());
		String key = aliyunOSSUtil.uploadFileToOSS(fileContent, ossKey(resources));
		return key != null;
	}

	@Override
	public boolean saveResource(Resources resources, String fileContent) {
		return saveResource(resources, fileContent.getBytes());
	}

	
	@Override
	public String downloadUrl(Resources resources) {
		return aliyunOSSUtil.getCdn() + "/" + ossKey(resources);
	}
	
	private String ossKey(Resources resources) {
		return rootName + resources.getSysKey() + "-" + resources.getFileName();
	}
}
