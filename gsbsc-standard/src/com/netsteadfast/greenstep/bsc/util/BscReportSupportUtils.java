/* 
 * Copyright 2012-2016 bambooCORE, greenstep of copyright Chen Xin Nien
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * -----------------------------------------------------------------------
 * 
 * author: 	Chen Xin Nien
 * contact: chen.xin.nien@gmail.com
 * 
 */
package com.netsteadfast.greenstep.bsc.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.netsteadfast.greenstep.base.AppContext;
import com.netsteadfast.greenstep.base.exception.ServiceException;
import com.netsteadfast.greenstep.base.model.DefaultResult;
import com.netsteadfast.greenstep.po.hbm.TbSysExpression;
import com.netsteadfast.greenstep.service.ISysExpressionService;
import com.netsteadfast.greenstep.util.ScriptExpressionUtils;
import com.netsteadfast.greenstep.vo.KpiVO;
import com.netsteadfast.greenstep.vo.SysExpressionVO;

@SuppressWarnings("unchecked")
public class BscReportSupportUtils {	
	private static final String REPORT_UP_DOWN_HTML_ICON_STATUS_EXPR_ID = "BSC_RPT_EXPR0003";
	private static final String REPORT_UP_DOWN_BYTE_ICON_STATUS_EXPR_ID = "BSC_RPT_EXPR0004";
	private static ISysExpressionService<SysExpressionVO, TbSysExpression, String> sysExpressionService; 
	private static ThreadLocal<SysExpressionVO> exprThreadLocal01 = new ThreadLocal<SysExpressionVO>(); // for BSC_RPT_EXPR0003
	private static ThreadLocal<SysExpressionVO> exprThreadLocal02 = new ThreadLocal<SysExpressionVO>(); // for BSC_RPT_EXPR0004
	private static NumberFormat numberFormat = null;
	
	static {
		numberFormat = new DecimalFormat("0.00");
		numberFormat.setCurrency( Currency.getInstance(Locale.US) );
		sysExpressionService = (ISysExpressionService<SysExpressionVO, TbSysExpression, String>)
				AppContext.getBean("core.service.SysExpressionService");		
	}
	
	public static void loadExpression() throws ServiceException, Exception {
		if ( exprThreadLocal01.get() == null ) { // 2015-04-10 add if block
			SysExpressionVO sysExpression01 = new SysExpressionVO();
			sysExpression01.setExprId( REPORT_UP_DOWN_HTML_ICON_STATUS_EXPR_ID );
			//DefaultResult<SysExpressionVO> result01 = sysExpressionService.findByUK(sysExpression01); // 2015-04-10 rem
			DefaultResult<SysExpressionVO> result01 = sysExpressionService.findByUkCacheable(sysExpression01); // 2015-04-10 add
			if (result01.getValue()!=null) {
				sysExpression01 = result01.getValue();
				exprThreadLocal01.set(sysExpression01);
			}			
		}
		if ( exprThreadLocal02.get() == null ) { // 2015-04-10 add if block
			SysExpressionVO sysExpression02 = new SysExpressionVO();
			sysExpression02.setExprId( REPORT_UP_DOWN_BYTE_ICON_STATUS_EXPR_ID );
			//DefaultResult<SysExpressionVO> result02 = sysExpressionService.findByUK(sysExpression02); // 2015-04-10 rem
			DefaultResult<SysExpressionVO> result02 = sysExpressionService.findByUkCacheable(sysExpression02); // 2015-04-10 add
			if (result02.getValue()!=null) {
				sysExpression02 = result02.getValue();
				exprThreadLocal02.set(sysExpression02);
			}			
		}
		
	}
	
	public static String getUrlIcon(KpiVO kpi, float score) throws Exception {
		String icon = "";
		SysExpressionVO sysExpression = exprThreadLocal01.get();
		if (null == sysExpression) {
			return icon;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> results = new HashMap<String, Object>();
		parameters.put("kpi", kpi);
		parameters.put("score", score);
		results.put("icon", " ");
		ScriptExpressionUtils.execute(
				sysExpression.getType(), 
				sysExpression.getContent(), 
				results, 
				parameters);
		icon = (String)results.get("icon");
		return StringUtils.defaultString( icon );
	}
	
	public static String getHtmlIcon(KpiVO kpi, float score) throws Exception {
		String icon = getUrlIcon(kpi, score);
		if ( StringUtils.isBlank(icon) ) {
			return "";
		}
		return "<img src='./images/" + icon + "' border='0' >";
	}
	
	public static byte[] getByteIcon(KpiVO kpi, float score) throws Exception {
		byte[] datas = null;
		SysExpressionVO sysExpression = exprThreadLocal02.get();
		if (null == sysExpression) {
			return datas;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> results = new HashMap<String, Object>();
		parameters.put("kpi", kpi);
		parameters.put("score", score);
		results.put("icon", " ");
		ScriptExpressionUtils.execute(
				sysExpression.getType(), 
				sysExpression.getContent(), 
				results, 
				parameters);
		String iconResource = (String)results.get("icon");		
		ClassLoader classLoader = BscReportSupportUtils.class.getClassLoader();
		datas = IOUtils.toByteArray( classLoader.getResource(iconResource).openStream() );
		return datas;
	}
	
	public static String parse(float score) {			
		return numberFormat.format(score);
	}
	
	public static String parse2(float score) {			
		String str = numberFormat.format(score);
		if (str.endsWith(".00")) {
			str = str.substring(0, str.length()-3);
		}
		return str;
	}	
	
}
