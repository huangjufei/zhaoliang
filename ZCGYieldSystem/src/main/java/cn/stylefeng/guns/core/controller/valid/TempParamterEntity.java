package cn.stylefeng.guns.core.controller.valid;


import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


import lombok.Data;

/**
 * 	临时参数实体类
 * 	描述：
 * 	该类用于声明在spring mvc中，控制层需要接受并验证的参数，但是该参数又不属于任何实体或vo对象中，将其定义在本类中；
 * 	原因：
 * 	对于单个的临时的参数，可以使用方法级别的参数校验，但会出现一个问题是：给定的message信息无法从i18n中获取（或许有作者不知道的方法，请指教；），所以这是一个变相的校验方法（在作者预想中，应该会有更好的办法，暂时放下）；
 * @author huwei
 *
 */
@Data
public class TempParamterEntity {

	private Integer page = 0;
	private Integer pageSize = 20;
	
	@NotNull(message="{temp.paramter.name.notnull}" ,groups= {ValidPart2.class})
	@Length(max=20,message="{temp.paramter.name.length}" ,groups= {ValidPart2.class})
	private String name;
	@NotNull(message="{temp.paramter.ids.notnull}" ,groups= {ValidPart3.class})
	private Long[] ids;
	
	@NotNull(message="{temp.paramter.uuids.notnull}", groups = {ValidPart4.class})
	private String[] uuids;
}
