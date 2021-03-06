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
package com.netsteadfast.greenstep.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.netsteadfast.greenstep.base.AppContext;
import com.netsteadfast.greenstep.base.Constants;
import com.netsteadfast.greenstep.base.exception.ServiceException;
import com.netsteadfast.greenstep.base.model.DefaultResult;
import com.netsteadfast.greenstep.base.model.YesNo;
import com.netsteadfast.greenstep.model.UploadTypes;
import com.netsteadfast.greenstep.po.hbm.TbSysUpload;
import com.netsteadfast.greenstep.service.ISysUploadService;
import com.netsteadfast.greenstep.util.SimpleUtils;
import com.netsteadfast.greenstep.vo.SysUploadVO;

@SuppressWarnings("unchecked")
public class UploadSupportUtils {
	protected static Logger logger=Logger.getLogger(UploadSupportUtils.class);
	private static ISysUploadService<SysUploadVO, TbSysUpload, String> sysUploadService;
	
	static {
		sysUploadService = (ISysUploadService<SysUploadVO, TbSysUpload, String>)AppContext.getBean("core.service.SysUploadService");
	}
	
	public static void cleanTempUpload() throws ServiceException, Exception {
		logger.info("clean upload temp begin...");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", UploadTypes.IS_TEMP);		
		List<TbSysUpload> searchList = sysUploadService.findListByParams(params);
		if (searchList==null || searchList.size()<1) {
			return;
		}
		for (TbSysUpload entity : searchList) {
			if (!YesNo.YES.equals(entity.getIsFile())) {
				sysUploadService.delete(entity);
				continue;
			}
			String dir = getUploadFileDir(entity.getSystem(), entity.getSubDir(), entity.getType());
			String fileFullPath = dir + "/" + entity.getFileName();
			File file = new File(fileFullPath);
			if (!file.exists()) {
				file = null;
				continue;
			}
			try {
				logger.warn("delete : " + file.getPath());
				FileUtils.forceDelete(file);
				sysUploadService.delete(entity);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("end...");
	}
	
	public static String getSubDir() {
		return SimpleUtils.getStrYMD(SimpleUtils.IS_YEAR);
	}
	
	public static String getUploadFileDir(String system, String type) {
		return getUploadFileDir(system, getSubDir(), type);
	}
	
	public static String getUploadFileDir(String system, String subDir, String type) {
		if (StringUtils.isBlank(system) || StringUtils.isBlank(subDir) || StringUtils.isBlank(type)) {
			throw new java.lang.IllegalArgumentException("system, sub-dir and type cann't blank!");
		}
		return Constants.getUploadDir() + "/" + system + "/" + type + "/" + subDir + "/";
	}	
	
	public static File mkdirUploadFileDir(String system, String type) throws IOException, Exception {
		return mkdirUploadFileDir(system, getSubDir(), type);
	}
	
	public static File mkdirUploadFileDir(String system, String subDir, String type) throws IOException, Exception {
		String uploadDir = getUploadFileDir(system, subDir, type);
		File dir = new File(uploadDir);
		if (dir.exists() && dir.isDirectory()) {
			return dir;
		}
		FileUtils.forceMkdir(dir);
		return dir;
	}
	
	public static String generateRealFileName(String showName) {
		if (StringUtils.isBlank(showName)) {
			throw new java.lang.IllegalArgumentException("name is blank!");
		}
		String extension = getFileExtensionName(showName);
		if (StringUtils.isBlank(extension)) {
			return SimpleUtils.getUUIDStr();
		}
		if (extension.length()>13) { // uuid 加上 點 "." 後是 37 字元 , TB_SYS_UPLOAD.FILE_NAME 最大為 50 , 50-37 = 13
			extension = extension.substring(0, 13);
		}
		return SimpleUtils.getUUIDStr() + "." + extension;
	}	
	
	public static String generateRealFileName(File file) {
		if (file==null) {
			throw new java.lang.IllegalArgumentException("file is null!");
		}
		return generateRealFileName( file.getName() );
	}
	
	public static File getRealFile(String uploadOid) throws ServiceException, IOException, Exception {
		if (StringUtils.isBlank(uploadOid)) {
			throw new Exception("parameter is blank!");
		}
		SysUploadVO uploadObj = new SysUploadVO();
		uploadObj.setOid(uploadOid);
		DefaultResult<SysUploadVO> result = sysUploadService.findObjectByOid(uploadObj);
		if (result.getValue()==null) {
			throw new ServiceException(result.getSystemMessage().getValue());
		}
		uploadObj = result.getValue();
		File packageFile = null;
		if (!YesNo.YES.equals(uploadObj.getIsFile())) {
			File dir = new File( Constants.getWorkTmpDir() + "/" + UploadSupportUtils.class.getSimpleName() );
			if (!dir.exists() || !dir.isDirectory()) {
				FileUtils.forceMkdir(dir);
			}			
			String tmpFileName = dir.getPath() + "/" + SimpleUtils.getUUIDStr() + "." + getFileExtensionName(uploadObj.getShowName());
			dir = null;
			OutputStream fos = null;
			try {
				packageFile = new File( tmpFileName );
				fos = new FileOutputStream(packageFile);
				IOUtils.write(uploadObj.getContent(), fos);		
				fos.flush();
			} catch (IOException e) {
				throw e;
			} finally {
				if (fos!=null) {
					fos.close();
				}
				fos = null;
			}			
		} else {
			String uploadDir = getUploadFileDir(uploadObj.getSystem(), uploadObj.getSubDir(), uploadObj.getType());
			packageFile = new File( uploadDir + "/" + uploadObj.getFileName() );			
		}		
		if (!packageFile.exists()) {
			throw new Exception("File is missing: " + uploadObj.getFileName() );
		}
		return packageFile;
	}
	
	public static byte[] getDataBytes(String uploadOid) throws ServiceException, IOException, Exception {
		if (StringUtils.isBlank(uploadOid)) {
			throw new Exception("parameter is blank!");
		}
		byte datas[] = null;
		SysUploadVO uploadObj = new SysUploadVO();
		uploadObj.setOid(uploadOid);
		DefaultResult<SysUploadVO> result = sysUploadService.findObjectByOid(uploadObj);
		if (result.getValue()==null) {
			throw new ServiceException(result.getSystemMessage().getValue());
		}
		uploadObj = result.getValue();
		datas = uploadObj.getContent();
		if (YesNo.YES.equals(uploadObj.getIsFile())) {
			String uploadDir = getUploadFileDir(uploadObj.getSystem(), uploadObj.getSubDir(), uploadObj.getType());
			File file = new File( uploadDir + "/" + uploadObj.getFileName() );
			datas = FileUtils.readFileToByteArray(file);
			file = null;
		}
		return datas;
	}
	
	public static String create(String system, String type, boolean isFile, File file, 
			String showName) throws ServiceException, IOException, Exception {
		if (StringUtils.isBlank(type) || null == file || StringUtils.isBlank(showName)) {
			throw new Exception("parameter is blank!");
		}				
		if (!file.exists()) {
			throw new Exception("file no exists!");
		}
		SysUploadVO upload = new SysUploadVO();		
		upload.setIsFile( ( isFile ? YesNo.YES : YesNo.NO ) );
		upload.setShowName(showName);
		upload.setSystem(system);
		upload.setType(type);
		upload.setSubDir( getSubDir() );		
		if (isFile) {
			String uploadDir = getUploadFileDir(system, type);
			String uploadFileName = generateRealFileName(file);
			mkdirUploadFileDir(system, uploadDir);
			FSUtils.cp( file.getPath(), uploadDir + "/" + uploadFileName );
			upload.setFileName( uploadFileName );			
		} else {
			upload.setContent( FileUtils.readFileToByteArray(file) );
			upload.setFileName( " " );			
		}		
		DefaultResult<SysUploadVO> result =  sysUploadService.saveObject(upload);
		if ( result.getValue() == null ) {
			throw new ServiceException( result.getSystemMessage().getValue() );
		}		
		return result.getValue().getOid();
	}
	
	public static String create(String system, String type, boolean isFile, byte[] datas, 
			String showName) throws ServiceException, IOException, Exception {
		if (StringUtils.isBlank(type) || null == datas || StringUtils.isBlank(showName)) {
			throw new Exception("parameter is blank!");
		}				
		SysUploadVO upload = new SysUploadVO();		
		upload.setIsFile( ( isFile ? YesNo.YES : YesNo.NO ) );
		upload.setShowName(showName);
		upload.setSystem(system);
		upload.setType(type);
		upload.setSubDir( getSubDir() );		
		if (isFile) {
			String uploadDir = getUploadFileDir(system, type);
			String uploadFileName = generateRealFileName(showName);
			mkdirUploadFileDir(system, uploadDir);
			File file = null;
			try {
				file = new File( uploadDir + "/" + uploadFileName );
				FileUtils.writeByteArrayToFile(file, datas);
			} catch (Exception e) {
				throw e;
			} finally {
				file = null;
			}
			upload.setFileName( uploadFileName );			
		} else {
			upload.setContent( datas );
			upload.setFileName( " " );			
		}		
		DefaultResult<SysUploadVO> result =  sysUploadService.saveObject(upload);
		if ( result.getValue() == null ) {
			throw new ServiceException( result.getSystemMessage().getValue() );
		}		
		return result.getValue().getOid();
	}	
	
	private static String getFileExtensionName(String fileFullName) {
		if (StringUtils.isBlank(fileFullName)) {
			return "";
		}
		String extension = "";
		String[] tmp = fileFullName.split("[.]");
		for (int i=1; tmp!=null && i<tmp.length; i++) {
			extension = tmp[i];
		}
		return extension;
	}
	
}
