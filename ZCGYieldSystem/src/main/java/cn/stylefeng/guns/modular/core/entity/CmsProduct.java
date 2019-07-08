package cn.stylefeng.guns.modular.core.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="t_cms_product")
public class CmsProduct{
	@Id
	@GeneratedValue
    private Integer id;
    private String name;
    @Column(name="rate_type")
    private Integer rateType;
    private Double rate;
    @Column(name="term_min")
    private Integer termMin;
    @Column(name="term_max")
    private Integer termMax;
    @Column(name="term_increasing")
    private Integer termIncreasing;
    @Column(name="money_min")
    private Double moneyMin;
    @Column(name="money_max")
    private Double moneyMax;
    @Column(name="money_increasing")
    private Double moneyIncreasing;
    private String adver;
    @Column(name="apply_condition")
    private String applyCondition;
    @Column(name="new_guidance")
    private String newGuidance;
    private String remarks;
    @Column(name="img_url")
    private String imgUrl;
    private Integer state=-1;
    private Integer modifier;
    @Column(name="update_time")
    private Date updateTime;
    private Integer creator;
    @Column(name="creater_time")
    private String createrTime;
    @Column(name="app_process")
    private String appProcess;
    @Column(name="recommend_whether")
    private String recommendWhether;
    private Double jianjun;
    @Column(name="jianjun_weight")
    private Double jianjunWeight;
    @Column(name="real_pass_tate")
    private Double realPassRate;
    @Column(name="real_pass_rate_weight")
    private Double realPassRateWeight;
    @Column(name="conversion_rate")
    private Double conversionRate;
    @Column(name="conversion_rate_weight")
    private Double conversionRateWeight;
    @Column(name="uv_income")
    private Double uvIncome;
    @Column(name="uv_income_weight")
    private Double uvIncomeWeight;
    @Column(name="market_time")
    private Date marketTime;
    private Double factor;
    @Column(name="a_value")
    private Integer aValue;
    @Column(name="app_id")
    private Long appId;
    @Column(name="template_id")
    private Long templateId;
    private Long channel;
    @Column(name="apply_url")
    private String applyUrl;
    @Column(name="success_rate")
    private String successRate;
    @Column(name="loan_time")
    private String loanTime;
    @Column(name="apply_people")
    private String applyPeople;
    @Column(name="new_whether")
    private String newWhether;
    @Column(name="new_sort")
    private Long newSort;
    @Column(name="hot_whether")
    private String hotWhether;
    @Column(name="hot_recommend_whether")
    private String hotRecommendWhether;
    @Column(name="hot_sort")
    private Long hotSort;
    @Column(name="list_sort")
    private Long listSort;
	public CmsProduct(Integer id, String name, Integer rateType, Double rate, Integer termMin, Integer termMax,
			Integer termIncreasing, Double moneyMin, Double moneyMax, Double moneyIncreasing, String adver,
			String applyCondition, String newGuidance, String remarks, String imgUrl, Integer state, Integer modifier,
			Date updateTime, Integer creator, String createrTime, String appProcess, String recommendWhether,
			Double jianjun, Double jianjunWeight, Double realPassRate, Double realPassRateWeight,
			Double conversionRate, Double conversionRateWeight, Double uvIncome, Double uvIncomeWeight, Date marketTime,
			Double factor, Integer aValue, Long appId, Long templateId, Long channel, String applyUrl,
			String successRate, String loanTime, String applyPeople, String newWhether, Long newSort, String hotWhether,
			String hotRecommendWhether, Long hotSort, Long listSort) {
		super();
		this.id = id;
		this.name = name;
		this.rateType = rateType;
		this.rate = rate;
		this.termMin = termMin;
		this.termMax = termMax;
		this.termIncreasing = termIncreasing;
		this.moneyMin = moneyMin;
		this.moneyMax = moneyMax;
		this.moneyIncreasing = moneyIncreasing;
		this.adver = adver;
		this.applyCondition = applyCondition;
		this.newGuidance = newGuidance;
		this.remarks = remarks;
		this.imgUrl = imgUrl;
		this.state = state;
		this.modifier = modifier;
		this.updateTime = updateTime;
		this.creator = creator;
		this.createrTime = createrTime;
		this.appProcess = appProcess;
		this.recommendWhether = recommendWhether;
		this.jianjun = jianjun;
		this.jianjunWeight = jianjunWeight;
		this.realPassRate = realPassRate;
		this.realPassRateWeight = realPassRateWeight;
		this.conversionRate = conversionRate;
		this.conversionRateWeight = conversionRateWeight;
		this.uvIncome = uvIncome;
		this.uvIncomeWeight = uvIncomeWeight;
		this.marketTime = marketTime;
		this.factor = factor;
		this.aValue = aValue;
		this.appId = appId;
		this.templateId = templateId;
		this.channel = channel;
		this.applyUrl = applyUrl;
		this.successRate = successRate;
		this.loanTime = loanTime;
		this.applyPeople = applyPeople;
		this.newWhether = newWhether;
		this.newSort = newSort;
		this.hotWhether = hotWhether;
		this.hotRecommendWhether = hotRecommendWhether;
		this.hotSort = hotSort;
		this.listSort = listSort;
	}
	public CmsProduct() {
		super();
	}
	
}
