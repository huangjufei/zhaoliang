
/**
 * 示例说明
 * 
 * HelloOSS是OSS Java SDK的示例程序，您可以修改endpoint、accessKeyId、accessKeySecret、bucketName后直接运行。
 * 运行方法请参考README。
 * 
 * 本示例中的并不包括OSS Java SDK的所有功能，详细功能及使用方法，请参看“SDK手册 > Java-SDK”，
 * 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/preface.html?spm=5176.docoss/sdk/java-sdk/。
 * 
 * 调用OSS Java SDK的方法时，抛出异常表示有错误发生；没有抛出异常表示成功执行。
 * 当错误发生时，OSS Java SDK的方法会抛出异常，异常中包括错误码、错误信息，详细请参看“SDK手册 > Java-SDK > 异常处理”，
 * 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/exception.html?spm=5176.docoss/api-reference/error-response。
 * 
 * OSS控制台可以直观的看到您调用OSS Java SDK的结果，OSS控制台地址是：https://oss.console.aliyun.com/index#/。
 * OSS控制台使用方法请参看文档中心的“控制台用户指南”， 指南的来链接地址是：https://help.aliyun.com/document_detail/oss/getting-started/get-started.html?spm=5176.docoss/user_guide。
 * 
 * OSS的文档中心地址是：https://help.aliyun.com/document_detail/oss/user_guide/overview.html。
 * OSS Java SDK的文档地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/install.html?spm=5176.docoss/sdk/java-sdk。
 * 
 */
package cn.stylefeng.guns.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Data
@Slf4j
@Component
@ConfigurationProperties(prefix="oss")
@PropertySource(value="classpath:config/properties/pro-aliyun-oss.properties" ,encoding="utf-8")
public class AliyunOSSUtil {
	private String endpoint = "oss-cn-beijing.aliyuncs.com";
	private String accessKeyId = "LTAIuBno1A0g70Wd";
	private String accessKeySecret = "xVa8mKeurQ1UX8KxSSwFVUvnrM29eU";
	private String bucketName = "plutusdog-apkpackage";
	private String cdn = "https://plutusdog-apkpackage.oss-cn-beijing.aliyuncs.com";

	/**
	 * @Title: getOSSClient
	 * @Description: TODO 获取OSS客户端
	 * @return
	 * @return: OSSClient
	 */
	public synchronized OSSClient getOSSClient() {
		OSSClient ossClient = null;
		try {
			ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		} catch (Exception e) {
			// TODO: handle exception
			log.error("获取OSS客户端异常",e);
		}
		return ossClient;
	}

	/**
	 * 线程等待时间
	 * 
	 * @param seconds 等待时间（秒）
	 */
	public void sleep(int seconds) {
		if (seconds <= 0) {
			return;
		}
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {

		}
	}
	
	public String uploadFileToOSS(File srcFile, String saveKey) {
		if(srcFile == null || !srcFile.exists() || srcFile.isDirectory()) {
			log.error("上传文件错误，为空或不是正文件");
			return null;
		}
		try {
			return uploadFileToOSS(new FileInputStream(srcFile), saveKey);
		} catch (FileNotFoundException e) {
			log.error("上传文件错误，文件不存在 ,file :" + srcFile.getAbsolutePath());
			return null;
		}
	}
	
	public String uploadFileToOSS(byte[] buf, String saveKey) {
		return uploadFileToOSS(new ByteArrayInputStream(buf), saveKey);
	}
	
	public String uploadFileToOSS(InputStream is ,String saveKey) {
		if (saveKey.indexOf("/") == 0) {
			saveKey = saveKey.substring(1, saveKey.length());
		}
		// 获取客户端
		OSSClient ossClient = getOSSClient();
		try {
			if (StringUtils.isBlank(bucketName)) {
				log.error("阿里云OSS配置错误，找不到Bucket配置！");
				return null;
			}
			if (ossClient.doesBucketExist(bucketName)) {
				log.error("您已经创建Bucket：" + bucketName + "。");
			} else {
				log.error("您的Bucket不存在，创建Bucket：" + bucketName + "。");
				ossClient.createBucket(bucketName);
			}
			// 上传文件
			ossClient.putObject(bucketName, saveKey, is);
			return saveKey;
		} catch (Exception e) {
			log.error("OSS上传文件出错,停止上传该文件!",e);
		} finally {
			ossClient.shutdown();
		}
		return saveKey;
	}

	/**
	 * 验证OSS上是否存在名称为bucketName的Bucket
	 * 
	 * @param OSS
	 * @param bucketName
	 * @return
	 */
	public boolean checkBucketExists(OSSClient oss, String bucketName) {
		List<Bucket> buckets = oss.listBuckets();
		for (Bucket bucket : buckets) {
			if (Objects.equals(bucket.getName(), bucketName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @Title: downOutputStream
	 * @Description: TODO 输出文件流，用于下载
	 * @param out 文件输出流
	 * @param key 文件地址
	 * @return: void
	 */
	public void downOutputStream(OutputStream out, String key) {
		// 获取客户端
		OSSClient ossClient = getOSSClient();
		try {
			OSSObject ossObject = null;
			try {
				ossObject = ossClient.getObject(bucketName, key);
			} catch (Exception e) {
				log.error("文件不存在，key：" + key);
				return;
			}
			byte[] buffer = new byte[8192];

			InputStream objectData = ossObject.getObjectContent();
			int bytesRead = 0;
			while ((bytesRead = objectData.read(buffer, 0, 8192)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			objectData.close();
		} catch (Exception ase) {
			log.error("下载异常！",ase);
		} finally {
			try {
				ossClient.shutdown();
				out.flush();
				out.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * @Title: deleteFileFromOSS
	 * @Description: TODO 从OSS删除文件
	 * @param saveKey 文件地址（不包含桶名）
	 * @return: void
	 */
	public void deleteFileFromOSS(String saveKey) {
		// 获取客户端
		OSSClient ossClient = getOSSClient();
		try {
			if (saveKey.indexOf("/") == 0) {
				saveKey = saveKey.substring(1, saveKey.length());
			}
			ossClient.deleteObject(bucketName, saveKey);
		} catch (Exception e) {
			log.error("OSS删除文件异常" ,e);
		} finally {
			ossClient.shutdown();
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AliyunOSSUtil [endpoint=" + endpoint + ", accessKeyId=" + accessKeyId + ", accessKeySecret="
				+ accessKeySecret + ", bucketName=" + bucketName + ", cdn=" + cdn + "]";
	}
	
//	static public void main(String[] args) throws FileNotFoundException {
//		AliyunOSSUtil ossutil = new AliyunOSSUtil();
//		String s = ossutil.uploadFileToOSS("hello".getBytes(), "zcshouliangSystem/290DACDB3264487DB6F96F02DFD5CA0F-来钱快.apk");
//		log.error(s);
//		FileOutputStream fos = new FileOutputStream("b.a");
//		ossutil.downOutputStream(fos, "zcshouliangSystem/290DACDB3264487DB6F96F02DFD5CA0F-来钱快.apk");
//	}
}
