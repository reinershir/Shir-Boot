package io.github.reinershir.boot.core.easygenerator.generator;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import io.github.reinershir.boot.core.international.InternationalizationMessager;

public class InternationalizationDirectiveModel implements TemplateDirectiveModel {
	
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		Writer write = env.getOut();
		write.write(InternationalizationMessager.getInstance().getMessage(params.get("code").toString()).toString());
	}

}
