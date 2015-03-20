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
package com.netsteadfast.greenstep.qcharts.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.netsteadfast.greenstep.base.dao.IBaseDAO;
import com.netsteadfast.greenstep.base.exception.ServiceException;
import com.netsteadfast.greenstep.base.service.BaseService;
import com.netsteadfast.greenstep.qcharts.dao.IDataSourceDriverDAO;
import com.netsteadfast.greenstep.po.hbm.QcDataSourceDriver;
import com.netsteadfast.greenstep.qcharts.service.IDataSourceDriverService;
import com.netsteadfast.greenstep.vo.DataSourceDriverVO;

@Service("qcharts.service.DataSourceDriverService")
@Scope("prototype")
@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
public class DataSourceDriverServiceImpl extends BaseService<DataSourceDriverVO, QcDataSourceDriver, String> implements IDataSourceDriverService<DataSourceDriverVO, QcDataSourceDriver, String> {
	protected Logger logger=Logger.getLogger(DataSourceDriverServiceImpl.class);
	private IDataSourceDriverDAO<QcDataSourceDriver, String> dataSourceDriverDAO;
	
	public DataSourceDriverServiceImpl() {
		super();
	}

	public IDataSourceDriverDAO<QcDataSourceDriver, String> getDataSourceDriverDAO() {
		return dataSourceDriverDAO;
	}

	@Autowired
	@Resource(name="qcharts.dao.DataSourceDriverDAO")
	@Required		
	public void setDataSourceDriverDAO(
			IDataSourceDriverDAO<QcDataSourceDriver, String> dataSourceDriverDAO) {
		this.dataSourceDriverDAO = dataSourceDriverDAO;
	}

	@Override
	protected IBaseDAO<QcDataSourceDriver, String> getBaseDataAccessObject() {
		return dataSourceDriverDAO;
	}

	@Override
	public String getMapperIdPo2Vo() {		
		return MAPPER_ID_PO2VO;
	}

	@Override
	public String getMapperIdVo2Po() {
		return MAPPER_ID_VO2PO;
	}

	@Override
	public Map<String, String> findForMap(boolean pleaseSelect) throws ServiceException, Exception {
		Map<String, String> dataMap = this.providedSelectZeroDataMap(pleaseSelect);
		List<QcDataSourceDriver> searchList = this.findListByParams(null);
		for (QcDataSourceDriver entity : searchList) {
			dataMap.put(entity.getOid(), entity.getName());
		}
		return dataMap;
	}

}