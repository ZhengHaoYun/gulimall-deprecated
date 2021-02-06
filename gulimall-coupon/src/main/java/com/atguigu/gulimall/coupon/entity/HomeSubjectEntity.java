package com.atguigu.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * 
 * @author zhenghaoyun
 * @email zhenghaoyun@foxmail.com
 * @date 2021-01-12 16:32:03
 */
@Data
@TableName("sms_home_subject")
public class HomeSubjectEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * ר
	 */
	private String name;
	/**
	 * ר
	 */
	private String title;
	/**
	 * ר
	 */
	private String subTitle;
	/**
	 * 
	 */
	private Integer status;
	/**
	 * 
	 */
	private String url;
	/**
	 * 
	 */
	private Integer sort;
	/**
	 * ר
	 */
	private String img;

}
