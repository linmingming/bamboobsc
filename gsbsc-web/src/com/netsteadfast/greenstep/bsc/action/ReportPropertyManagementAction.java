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
package com.netsteadfast.greenstep.bsc.action;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.netsteadfast.greenstep.base.action.BaseSupportAction;
import com.netsteadfast.greenstep.base.action.IBaseAdditionalSupportAction;
import com.netsteadfast.greenstep.base.exception.ControllerException;
import com.netsteadfast.greenstep.base.exception.ServiceException;
import com.netsteadfast.greenstep.base.model.ControllerAuthority;
import com.netsteadfast.greenstep.base.model.ControllerMethodAuthority;
import com.netsteadfast.greenstep.bsc.util.BscReportPropertyUtils;
import com.netsteadfast.greenstep.util.MenuSupportUtils;

@ControllerAuthority(check=true)
@Controller("bsc.web.controller.ReportPropertyManagementAction")
@Scope
public class ReportPropertyManagementAction extends BaseSupportAction implements IBaseAdditionalSupportAction {
	private static final long serialVersionUID = 8238927774459840277L;
	
	public ReportPropertyManagementAction() {
		super();
	}
	
	private void initData() throws ServiceException, Exception {
		BscReportPropertyUtils.loadData();
		this.getFields().put("backgroundColor", BscReportPropertyUtils.getBackgroundColor() );
		this.getFields().put("fontColor", BscReportPropertyUtils.getFontColor() );
		this.getFields().put("perspectiveTitle", BscReportPropertyUtils.getPerspectiveTitle() );
		this.getFields().put("objectiveTitle", BscReportPropertyUtils.getObjectiveTitle() );
		this.getFields().put("kpiTitle", BscReportPropertyUtils.getKpiTitle() );
		this.getFields().put("classNote", BscReportPropertyUtils.getPersonalReportClassLevel() );
	}
	
	/**
	 *  bsc.reportPropertyManagementAction.action
	 */
	@ControllerMethodAuthority(programId="BSC_PROG004D0001Q")
	public String execute() throws Exception {
		try {
			this.initData();
		} catch (ControllerException e) {
			this.setPageMessage(e.getMessage().toString());
		} catch (ServiceException e) {
			this.setPageMessage(e.getMessage().toString());
		} catch (Exception e) {
			e.printStackTrace();
			this.setPageMessage(e.getMessage().toString());
		}
		return SUCCESS;		
	}		

	@Override
	public String getProgramName() {
		try {
			return MenuSupportUtils.getProgramName(this.getProgramId());
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public String getProgramId() {
		return super.getActionMethodProgramId();
	}

}
