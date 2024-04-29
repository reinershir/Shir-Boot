package io.github.reinershir.ai.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.github.reinershir.boot.common.ValidateGroups;
import io.github.reinershir.boot.common.ValidateGroups.AddGroup;
import io.github.reinershir.boot.common.ValidateGroups.UpdateGroup;
import io.github.reinershir.boot.core.query.annotation.QueryRule;
import io.github.reinershir.boot.core.query.annotation.QueryRuleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
/**
 * ChatHistory Model, generated by Shir Boot 
 * @author Shir-Boot
 * @Date 2024年4月17日 上午10:07:25
 * @version 1.0
 *
 */
 @Data
 @TableName("CHAT_HISTORY")
public class ChatHistory {
	/**
	 * 
	 */
	@TableId(type=IdType.AUTO)
	@Schema(description = "ChatHistory ID")
	@NotNull(message = "id不能为空",groups = ValidateGroups.UpdateGroup.class)
	private Long id;
	
	/**
	 * 
	 */
	@TableField("QUESTION")
	@QueryRule(QueryRuleEnum.LIKE)
	@Schema(description = "question")
	@NotBlank(message = "question不能为空",groups = {ValidateGroups.AddGroup.class,ValidateGroups.UpdateGroup.class})
	private String question;
	
	/**
	 * 
	 */
	@TableField("ANSWER")
	@Schema(description = "answer")
	@NotBlank(message = "answer不能为空",groups = {ValidateGroups.AddGroup.class,ValidateGroups.UpdateGroup.class})
	private String answer;
	
	/**
	 * 
	 */
	@TableField("MASK")
	@Schema(description = "mask")
	private String mask;
	
	/**
	 * 
	 */
	@TableField("USER_ID")
	@QueryRule(QueryRuleEnum.EQ)
	@Schema(description = "userId")
	@NotNull(message = "userId不能为空",groups = {ValidateGroups.AddGroup.class,ValidateGroups.UpdateGroup.class})
	private Long userId;
	
	/**
	 * 
	 */
	@TableField("CONSUME_TOKEN")
	@Schema(description = "consumeToken",nullable = true,defaultValue="0")
	private Float consumeToken;
	
	/**
	 * 
	 */
	@TableField("CREATE_TIME")
	@Schema(description = "createTime")
	private Date createTime;
	
	@TableField("SESSION_ID")
	@QueryRule(QueryRuleEnum.EQ)
	@Schema(description = "session id")
	private String sessionId;

	public ChatHistory(
			@NotBlank(message = "question不能为空", groups = { AddGroup.class, UpdateGroup.class }) String question,
			@NotBlank(message = "answer不能为空", groups = { AddGroup.class, UpdateGroup.class }) String answer,
			String mask, @NotNull(message = "userId不能为空", groups = { AddGroup.class, UpdateGroup.class }) Long userId,
			Float consumeToken,String sessionId,Date createTime) {
		super();
		this.question = question;
		this.answer = answer;
		this.mask = mask;
		this.userId = userId;
		this.consumeToken = consumeToken;
		this.createTime = createTime;
		this.sessionId = sessionId;
	}
	
	public ChatHistory() {}
	
}