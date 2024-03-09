package io.github.reinershir.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema
public class AIChatDTO {

	private String prompt;
	
	private String model;
	
}
