package de.neuland.jade4j.compiler;

import de.neuland.jade4j.Jade4J;
import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.TestFileHelper;
import de.neuland.jade4j.exceptions.JadeCompilerException;
import de.neuland.jade4j.expression.ExpressionHandler;
import de.neuland.jade4j.filter.CDATAFilter;
import de.neuland.jade4j.filter.PlainFilter;
import de.neuland.jade4j.lexer.Lexer;
import de.neuland.jade4j.lexer.token.Token;
import de.neuland.jade4j.template.FileTemplateLoader;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;


public class OriginalJade20150515SingleTest {
	String templateName = "doctype.default";

	@Test
	public void testCase() throws IOException, JadeCompilerException {
		JadeConfiguration jade = getJadeConfiguration();
		Lexer lexer = initLexer(templateName,jade);
		LinkedList<Token> tokens = lexer.getTokens();
		testJade(templateName, jade);
	}

	protected Lexer initLexer(String fileName, JadeConfiguration jade) {
     try {
         TemplateLoader loader = jade.getTemplateLoader();
		 ExpressionHandler expressionHandler = jade.getExpressionHandler();
		 return new Lexer(fileName+".jade", loader, expressionHandler);
     } catch (Exception e) {
         e.printStackTrace();
         return null;
     }
 }

	private void testJade(String path, JadeConfiguration jade) throws IOException {
		File file = new File(TestFileHelper.getOriginal20150515ResourcePath("cases/"+path + ".jade"));
		JadeTemplate template = jade.getTemplate(path+".jade");
		Writer writer = new StringWriter();
		HashMap<String, Object> model = getModel();
		jade.renderTemplate(template, model, writer);
		String html = writer.toString();

		String expected = readFile(file.getPath().replace(".jade", ".html")).trim().replaceAll("\r", "");
		assertEquals(file.getName(), expected, html.trim());
	}

	private HashMap<String, Object> getModel() {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("title","Jade");
		return model;
	}

	private JadeConfiguration getJadeConfiguration() {
		JadeConfiguration jade = new JadeConfiguration();
		jade.setTemplateLoader(new FileTemplateLoader(TestFileHelper.getOriginal20150927ResourcePath("cases/"), "UTF-8"));
		//		jade.setExpressionHandler(new JsExpressionHandler());
		jade.setMode(Jade4J.Mode.XHTML); // original jade uses xhtml by default
		jade.setFilter("plain", new PlainFilter());
		jade.setFilter("cdata", new CDATAFilter());
		jade.setPrettyPrint(true);
		return jade;
	}


	private String readFile(String fileName) {
		try {
			return FileUtils.readFileToString(new File(fileName));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return "";
	}
}
