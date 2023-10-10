<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/content/common/taglib.jsp" %> 
<script type="text/javascript">
var ${ClassName}={};
$(function(){
	//DataGrid Init
	var ${ClassName?uncap_first}Datagrid = UI.initDatagrid("#${ClassName?uncap_first}Datagrid",{
		url: '${ClassName}/getList',
	    <#if Oid??>idField : "${Oid}",</#if>
	    onDblClickRow:function(index,row){
	    	User.editUser();
	    },
	    toolbar:"#${ClassName?uncap_first}Toolbar",
	    <#if contextMenu>menuSelector:'#${ClassName?uncap_first}ContextMenu',</#if>
	    columns:[[    
	    	{field:'ck',checkbox:true,title:'<s:message code="label.chkbox"/>'},
	    	<#if Autos??>
				<#list Autos as item>
				<#assign info=item?split("#")>
					<#if info[4]=="true">
					{field:'${info[0]}',title:'${info[2]}',width:80,align:'center'}<#if item_has_next>,</#if>
					</#if>
				</#list>
			</#if>
        ]]
	});

	//Add
	${ClassName}.add${ClassName} = function(){
		UI.addByWindow({
			title:'${ClsLabel}<s:message code="label.add"/>',
			windowUrl:"${ClassName}/toAdd",
			width:600,
			height:${Autos?size*30},
			formId:'#form${ClassName}Add',
			url:'${ClassName}/add',
			datagrid:${ClassName?uncap_first}Datagrid
		});
	}

	//Update
	${ClassName}.edit${ClassName} = function (){
		UI.updateByWindow({
			title:'${ClsLabel}<s:message code="label.edit"/>',
			windowUrl:"${ClassName}/toEdit", //编辑页面的跳转URL
			width:600,
			height:${Autos?size*30},
			formId:'#form${ClassName}Edit',	//表单ID
			url:'${ClassName}/update',		//提交表单时的URL
			datagrid:${ClassName?uncap_first}Datagrid,
			<#if Oid??>idField : "${Oid}",</#if>			//ID列名,使用getUrl获得数据时才需要
			getUrl:'${ClassName}/getById'//获得用户信息详情的请求URL
		});
	}
	
	${ClassName}.delete${ClassName} = function (){
		UI.deleteByDatagrid(${ClassName?uncap_first}Datagrid, '${ClassName}/delete', "${Oid}");
	}

	//Search
	${ClassName}.query${ClassName}List = function (){
		
		var parament={};
		<#if Propertys??>
		<#list Propertys as item>
		<#assign info=item?split("#")>
		var ${info[1]} = $("#${ClassName?uncap_first}${info[1]?cap_first}").val();
		if(${info[1]}!="")
			parament["${info[1]}"]=${info[1]};
		</#list>
		</#if>
				
		${ClassName?uncap_first}Datagrid.datagrid({
			queryParams:parament
		});
	}
});

</script>
<div  id="${ClassName?uncap_first}Toolbar" class="toolbar">
	<form id="search${ClassName}Form" style="padding:10px 10px;" >
		<table>
			<tr>
				
				<#if Propertys??>
				<#list Propertys as item>
				<#assign info=item?split("#")>
					<td>${info[4]}：</td>
					<td width="200"><input name="${info[1]}" id="${ClassName?uncap_first}${info[1]?cap_first}" class="easyui-textbox" /></td>
				</#list>
				</#if>
				<#if search>
				<td>
				<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="${ClassName}.query${ClassName}List()"><s:message code="label.search"></s:message></a>
				<a class="easyui-linkbutton" iconCls="icon-clear" plain="true" onclick="$('#search${ClassName}Form').form('clear')"><s:message code="label.clear"></s:message></a>
				</td>
				</#if>
			</tr>
		</table>
	</form>
	<#if save>
	<shiro:hasPermission name="${ClassName}/add">
		<a href="javascript:void(0)" onclick="${ClassName}.add${ClassName}()" class="easyui-linkbutton" iconCls="icon-add" plain="true"><s:message code="label.add"/></a>
	</shiro:hasPermission>
	</#if>
	<#if update>
	<shiro:hasPermission name="${ClassName}/update">
		<a href="javascript:void(0)" onclick="${ClassName}.edit${ClassName}()" class="easyui-linkbutton" iconCls="icon-edit" plain="true"><s:message code="label.edit"/></a>
	</shiro:hasPermission>	
	</#if>
	<#if remove>
    <shiro:hasPermission name="${ClassName}/delete">
    	<a href="javascript:void(0)" onclick="${ClassName}.delete${ClassName}()" class="easyui-linkbutton" iconCls="icon-remove" plain="true" ><s:message code="label.delete"/></a>
    </shiro:hasPermission>  	
    </#if>
</div>
<table id="${ClassName?uncap_first}Datagrid"></table>
<%-- grid右键菜单 --%>
<div id="${ClassName?uncap_first}ContextMenu" class="easyui-menu" style="width:120px;">
<#if save>
<shiro:hasPermission name="${ClassName}/add">
	<div onclick="${ClassName}.add${ClassName}()" data-options="iconCls:'icon-add'"><s:message code="label.add"/></div>
</shiro:hasPermission>
</#if>
<#if update>
<shiro:hasPermission name="${ClassName}/update">
	<div onclick="${ClassName}.edit${ClassName}()" data-options="iconCls:'icon-edit'"><s:message code="label.edit"/></div>
</shiro:hasPermission>
</#if>
<#if remove>
<shiro:hasPermission name="${ClassName}/delete">
	<div onclick="${ClassName}.delete${ClassName}()" data-options="iconCls:'icon-remove'"><s:message code="label.delete"/></div>
</shiro:hasPermission>
</#if>
</div>    