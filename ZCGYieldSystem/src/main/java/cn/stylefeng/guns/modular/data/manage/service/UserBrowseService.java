package cn.stylefeng.guns.modular.data.manage.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.stylefeng.guns.modular.core.entity.AppInfo;
import cn.stylefeng.guns.modular.core.entity.BaseChannel;
import cn.stylefeng.guns.modular.core.entity.CmsProduct;
import cn.stylefeng.guns.modular.core.entity.Template;
import cn.stylefeng.guns.modular.core.entity.UserBrowse;
import cn.stylefeng.guns.modular.core.repository.JpaAppInfoRepository;
import cn.stylefeng.guns.modular.core.repository.JpaBaseChannelRepository;
import cn.stylefeng.guns.modular.core.repository.JpaCmsProductRepository;
import cn.stylefeng.guns.modular.core.repository.JpaTemplateRepository;
import cn.stylefeng.guns.modular.core.repository.JpaUserBrowseRepository;
import cn.stylefeng.guns.modular.core.vo.AppIdAndNameMapperVO;
import cn.stylefeng.guns.modular.core.vo.UserBrowseRecordVo;




@Service
public class UserBrowseService {
	

	@Autowired
	private JpaCmsProductRepository cmsProductDatamanager;
	
	@Autowired 
	private JpaAppInfoRepository appInfoDataManager;
	
	@Autowired
	private JpaUserBrowseRepository jpaUserBrowse;
	
	@Autowired
	private JpaTemplateRepository jpaTemplate;
	
	@Autowired
	private JpaBaseChannelRepository jpaBaseChannelRepository;

	public List<AppIdAndNameMapperVO> findAllAppInfo() {
		List<AppInfo> appInfoList = appInfoDataManager.findAll();
		return appInfoListToAppIdAndNameList(appInfoList);
	}
	
	private List<AppIdAndNameMapperVO> appInfoListToAppIdAndNameList(List<AppInfo> appInfoList){
		List<AppIdAndNameMapperVO> result = new ArrayList<AppIdAndNameMapperVO>();
		for (AppInfo appInfo : appInfoList) {
			result.add(appInfoToAppIdAndName(appInfo));
		}
		return result;
	}
	
	private AppIdAndNameMapperVO appInfoToAppIdAndName(AppInfo appinfo){
		return new AppIdAndNameMapperVO(Long.valueOf(appinfo.getId()), appinfo.getName());
	}

	public Map<String, Integer> appBrowseRecord(long id) {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		List<CmsProduct> cmsProductList = cmsProductDatamanager.findAll();
		if(Objects.isNull(cmsProductList) || cmsProductList.isEmpty()) {
			return result;
		}
		
		List<UserBrowse> userBrowseList = jpaUserBrowse.findByAppId(id);
		if(Objects.isNull(cmsProductList) || cmsProductList.isEmpty()) {
			return result;
		}
		
		for(int i = 0 ,len = cmsProductList.size() ;i < len ;++i) {
			CmsProduct cmsProduct = cmsProductList.get(i);
			int productId = cmsProduct.getId();
			
			//TODO 耗时操作
			int userBrowseCount = 0;
			for(int j = 0 ,jLen = userBrowseList.size() ;j < jLen ;++j) {
				UserBrowse ub = userBrowseList.get(j);
				if(id == ub.getAppId() && productId == ub.getProductId()) {
					++userBrowseCount;
				}
			}
			
			result.put(cmsProduct.getName(), userBrowseCount);
		}
		
		return result;
	}
	
	public List<UserBrowseRecordVo> appBrowseRecordForList(long appId){
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		List<CmsProduct> cmsProductList = cmsProductDatamanager.findAll();
		if(Objects.isNull(cmsProductList) || cmsProductList.isEmpty()) {
			return result;
		}
		
		List<UserBrowse> userBrowseList = jpaUserBrowse.findByAppId(appId);
		if(Objects.isNull(userBrowseList) || userBrowseList.isEmpty()) {
			return result;
		}
		
		Map<Long,Integer> allProductBrowseCountMap = statisticsProductBrowseCount(userBrowseList);
		
		for(int i = 0 ,len = cmsProductList.size() ;i < len ;++i) {
			CmsProduct cmsProduct = cmsProductList.get(i);
			result.add(new UserBrowseRecordVo(cmsProduct.getName(), allProductBrowseCountMap.get(Long.valueOf(cmsProduct.getId()))));
		}
		return result;
	}

	public int findUserBrowseCountNumberByAppId(long appId) {
		return jpaUserBrowse.findUserBrowseCountNumberByAppId(appId);
	}
	
	public int findUserBrowseCountNumberByAppIdAndChannel(long appId ,String channel) {
		return jpaUserBrowse.findUserBrowseCountNumberByAppIdAndChannel(appId ,channel);
	}

	public int findAllUserBrowseCountNumber() {
		return jpaUserBrowse.findAllUserBrowseCountNumber();
	}

	public List<Template> findAllTemplate() {
		return jpaTemplate.findAll();
	}

	public List<AppInfo> getAppVersionByTemplateId(int templateId) {
		return appInfoDataManager.findAppVersionByTemplateId(templateId);
	}

	public List<UserBrowseRecordVo> searchBrowse(long appId, Date startDate, Date endDate) {
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		List<CmsProduct> cmsProductList = cmsProductDatamanager.findAll();
		if(Objects.isNull(cmsProductList) || cmsProductList.isEmpty()) {
			return result;
		}
		
		List<UserBrowse> userBrowseList = null; 
		if(Objects.isNull(startDate) && Objects.isNull(endDate)) {
			userBrowseList = jpaUserBrowse.findByAppId(appId);
		}
		else if(Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
			userBrowseList = jpaUserBrowse.findAllByAppIdAndBrowseTimeBetween(appId,startDate ,endDate);
		}
		else if(Objects.nonNull(startDate) && Objects.isNull(endDate)) {
			userBrowseList = jpaUserBrowse.findAllByAppIdAndBrowseTimeGreaterThanEqual(appId,startDate);
		}
		else if(Objects.isNull(startDate) && Objects.nonNull(endDate)) {
			userBrowseList = jpaUserBrowse.findAllByAppIdAndBrowseTimeLessThanEqual(appId,endDate);
		}
		
		Map<Long,Integer> allProductBrowseCountMap = statisticsProductBrowseCount(userBrowseList);
		
		//合并计数
		for(int i = 0 ,len = cmsProductList.size() ;i < len ;++i) {
			CmsProduct cmsProduct = cmsProductList.get(i);
			Integer count = allProductBrowseCountMap.get(Long.valueOf(cmsProduct.getId()));
			result.add(new UserBrowseRecordVo(cmsProduct.getName(), count == null ? 0 : count));
		}
		
		return result;
	}
	
	public List<UserBrowseRecordVo> searchBrowse(long appId,Date startDate, Date endDate ,String channel) {
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		List<CmsProduct> cmsProductList = cmsProductDatamanager.findAll();
		if(Objects.isNull(cmsProductList) || cmsProductList.isEmpty()) {
			return result;
		}
		
		List<UserBrowse> userBrowseList = null; 
		if(Objects.isNull(startDate) && Objects.isNull(endDate)) {
			userBrowseList = jpaUserBrowse.findByAppId(appId);
		}
		else if(Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
			userBrowseList = jpaUserBrowse.findAllByAppIdAndBrowseTimeBetween(appId,startDate ,endDate);
		}
		else if(Objects.nonNull(startDate) && Objects.isNull(endDate)) {
			userBrowseList = jpaUserBrowse.findAllByAppIdAndBrowseTimeGreaterThanEqual(appId,startDate);
		}
		else if(Objects.isNull(startDate) && Objects.nonNull(endDate)) {
			userBrowseList = jpaUserBrowse.findAllByAppIdAndBrowseTimeLessThanEqual(appId,endDate);
		}
		
		// channel过滤
		userBrowseList = filterByChannel(userBrowseList ,channel);
		
		Map<Long,Integer> allProductBrowseCountMap = statisticsProductBrowseCount(userBrowseList);
		
		//合并计数
		for(int i = 0 ,len = cmsProductList.size() ;i < len ;++i) {
			CmsProduct cmsProduct = cmsProductList.get(i);
			Integer count = allProductBrowseCountMap.get(Long.valueOf(cmsProduct.getId()));
			result.add(new UserBrowseRecordVo(cmsProduct.getName(), count == null ? 0 : count));
		}
		
		return result;
	}
	
	private List<UserBrowse> filterByChannel(List<UserBrowse> userBrowseList ,String channel) {
		List<UserBrowse> result = new ArrayList<UserBrowse>();
		for(int i = 0 ,len = userBrowseList.size() ;i < len ;++i) {
			UserBrowse ub = userBrowseList.get(i);
			if(ub.getChannel() != null && ub.getChannel().equals(channel)) {
				result.add(ub);
			}
		}
		return result;
	}

	private Map<Long,Integer> statisticsProductBrowseCount(List<UserBrowse> userBrowseList){
		Map<Long,Integer> productBrowseCountMap = new HashMap<Long, Integer>();
		for(int i = 0 ,len = userBrowseList.size() ;i < len ;++i) {
			UserBrowse ub = userBrowseList.get(i);
			Integer count = productBrowseCountMap.get(ub.getProductId());
			productBrowseCountMap.put(ub.getProductId(), count == null ? 1 : ++count);
		}
		return productBrowseCountMap;
	}

	public int calculateBrowseCountNumber(List<UserBrowseRecordVo> userBrowse) {
		if(Objects.isNull(userBrowse) || userBrowse.isEmpty()) {
			return 0;
		}
		
		int countNumber = 0;
		for(int i = userBrowse.size() - 1 ;i >= 0 ;--i) {
			countNumber += userBrowse.get(i).getCount();
		}
		
		return countNumber;
	}
	
	public List<UserBrowseRecordVo> searchBrowseByTemplateIds(int[] templateIds, Date sDate, Date eDate){
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		for(int i = 0 ;i < templateIds.length ;++i) {
			List<UserBrowseRecordVo> templateList = searchBrowseByTemplateId(templateIds[i], sDate, eDate);
			if(result.isEmpty()) {
				result.addAll(templateList);
			}
			else {
				result = mergeBrowseCountNumber(result ,templateList);
			}
		}
		
		return result;
	}
	
	private List<UserBrowseRecordVo> mergeBrowseCountNumber(List<UserBrowseRecordVo> main ,List<UserBrowseRecordVo> target){
		List<UserBrowseRecordVo> result = main;
		List<UserBrowseRecordVo> temp = target;
		
		Map<String ,Integer> tempBrowseCountMap = new HashMap<String, Integer>();
		for(int j = result.size() -1 ;j >= 0 ;--j) {
			tempBrowseCountMap.put(result.get(j).getName(), result.get(j).getCount());
		}
		
		//合并浏览计数
		for(int k = temp.size() -1 ;k >= 0 ;--k) {
			UserBrowseRecordVo temp0 = temp.get(k);
			Integer count = tempBrowseCountMap.get(temp0.getName());
			if(count != null) {
				tempBrowseCountMap.put(temp0.getName(), count+temp0.getCount());
			}
		}
		
		//组装结果
		for(int j = result.size() -1 ;j >= 0 ;--j) {
			result.get(j).setCount(tempBrowseCountMap.get(result.get(j).getName()));
		}
		
		return result;
	}

	public List<UserBrowseRecordVo> searchBrowseByTemplateId(int templateId, Date sDate, Date eDate) {
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		List<AppInfo> appInfoList = appInfoDataManager.findAppVersionByTemplateId(templateId);
		for(int i = appInfoList.size() -1 ;i >= 0 ;--i) {
			List<UserBrowseRecordVo> appVersions = searchBrowse(appInfoList.get(i).getId(), sDate, eDate);
			if(result.isEmpty()) {
				result.addAll(appVersions);
			}
			else {
				result = mergeBrowseCountNumber(result ,appVersions);
			}
		}
		return result;
	}
	
	public List<UserBrowseRecordVo> searchBrowseByTemplateId(int templateId, Date sDate, Date eDate ,String channel) {
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		List<AppInfo> appInfoList = appInfoDataManager.findAppVersionByTemplateId(templateId);
		for(int i = appInfoList.size() -1 ;i >= 0 ;--i) {
			List<UserBrowseRecordVo> appVersions = searchBrowse(appInfoList.get(i).getId(), sDate, eDate ,channel);
			if(result.isEmpty()) {
				result.addAll(appVersions);
			}
			else {
				result = mergeBrowseCountNumber(result ,appVersions);
			}
		}
		return result;
	}

	public List<UserBrowseRecordVo> searchBrowseByTemplateIds(int[] templateIds, Date sDate, Date eDate,
			String channel) {
		List<UserBrowseRecordVo> result = new ArrayList<UserBrowseRecordVo>();
		
		for(int i = 0 ;i < templateIds.length ;++i) {
			List<UserBrowseRecordVo> templateList = searchBrowseByTemplateId(templateIds[i], sDate, eDate ,channel);
			if(result.isEmpty()) {
				result.addAll(templateList);
			}
			else {
				result = mergeBrowseCountNumber(result ,templateList);
			}
		}
		
		return result;
	}
	
	
	public List<BaseChannel> findAllBaseChannel(){
		return jpaBaseChannelRepository.findAll();
	}
}
