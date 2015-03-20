<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

//String isGoogleWebConnect = (String)request.getParameter("isGoogleWebConnect");

%>
<div dojoType="dijit.layout.ContentPane" region="top" id="topBar" style=" overflow:hidden; ">
	<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#C5DDF6" > <!-- #CFECEC -->
		<tr>
			<td width="70%" align="left">
			
				<div id="comboButtonMenu" data-dojo-type="dijit.form.ComboButton">
					<span>Application</span>				
					<div dojoType="dijit.Menu" >	
							
							${comboButtonMenuData}	
					
							<div dojoType="dijit.MenuSeparator"></div>
							<div dojoType="dijit.MenuItem" jsId="backHome" data-dojo-props='onClick:function(){ window.location="<%=basePath%>/pages/way.jsp"; }' ><img src="./icons/view-refresh.png" border="0">&nbsp;Refresh</div>
							
					</div>					
				</div>			
				&nbsp;|&nbsp;
				<div id="comboButtonHelp" data-dojo-type="dijit.form.ComboButton">
					<span>Help</span>
					<div dojoType="dijit.Menu" id="helpMenu">
						<div dojoType="dijit.MenuItem" data-dojo-props='onClick:function(){ CORE_PROGCOMM0001Q_DlgShow(); }' >About</div>										
					</div>
				</div>								
									
				&nbsp;|&nbsp;
				<a href="#" title="logout, end session." alt="logout, end session." onClick='confirmDialog("logoutDialogId000", "Logout", "logout , Are you sure ?", logoutEvent, (window.event ? window.event : null) ); return false;'><img src="./images/logout.png" alt="logoOut" border="0" /></a>
				
				&nbsp;|&nbsp;										
				<font size='2'>${verMsg}</font>			
				
			</td>

			<td width="30%" align="right">
					<!--  
					<img src="./images/original.jpg" alt="logo" />
					-->
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									
			</td>
		</tr>
	</table>				
</div>	
			
<!-- dlg -->			
${dialogData}

<div id="pleaseWaitDlg" dojoType="dojox.widget.DialogSimple" style="width: 420px; height: 100px" title="Please wait">
	<table border="0">
		<tr>
			<td>
				<font size='3'>Please wait.</font>
				<br>
				<div id="indeterminateBar" data-dojo-type="dijit/ProgressBar" data-dojo-props='style:"width:380px" ' indeterminate="true" ></div>
				<br>
				<br>
				<br>
				<br>
				<br>
			</td>
		</tr>
	</table>
</div>

		