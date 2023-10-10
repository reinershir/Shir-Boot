package io.github.reinershir.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.github.reinershir.auth.annotation.OptionType;
import io.github.reinershir.auth.annotation.Permission;
import io.github.reinershir.auth.annotation.PermissionMapping;
import io.github.reinershir.boot.common.Result;
import io.github.reinershir.boot.dto.req.SystemLogDTO;
import io.github.reinershir.boot.mapper.SystemLogMapper;
import io.github.reinershir.boot.model.SystemLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/systemLogs")
@Tag(name = "系统日志")
@PermissionMapping("LOG")
public class SystemLogController {
	@Autowired
	SystemLogMapper systemLogMapper;

	
	@GetMapping
	@Permission(value=OptionType.LIST,name="查询系统日志")
	@Operation(summary = "系统日志查询接口",description = "统计日志")
	public Result<IPage<SystemLog>> list(@RequestBody @Validated SystemLogDTO systemLogDTO) throws Exception{
		QueryWrapper<SystemLog> queryWrapper = new QueryWrapper<>();
		if(null!=systemLogDTO.getUserId()) {
			queryWrapper.eq("REQUEST_USER", systemLogDTO.getUserId());
		}
		queryWrapper.ge(systemLogDTO.getStartTime()!=null, "CREATE_TIME", systemLogDTO.getStartTime());
		queryWrapper.le(systemLogDTO.getEndTime()!=null, "CREATE_TIME", systemLogDTO.getEndTime());
		IPage<SystemLog> page = systemLogMapper.selectPage(new Page<SystemLog>(systemLogDTO.getPage(),systemLogDTO.getPageSize()), queryWrapper);
		return Result.ok(page);
	}
	

}
