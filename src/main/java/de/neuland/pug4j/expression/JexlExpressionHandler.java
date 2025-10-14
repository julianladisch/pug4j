package de.neuland.pug4j.expression;

import de.neuland.pug4j.jexl3.PugJexlArithmetic;
import de.neuland.pug4j.jexl3.PugJexlBuilder;
import org.apache.commons.jexl3.*;

import de.neuland.pug4j.exceptions.ExpressionException;
import de.neuland.pug4j.model.PugModel;
import org.apache.commons.jexl3.internal.introspection.Uberspect;
import org.apache.commons.jexl3.introspection.JexlPermissions;
import org.apache.commons.jexl3.introspection.JexlUberspect;
import org.apache.commons.logging.LogFactory;

import java.util.Map;

public class JexlExpressionHandler extends AbstractExpressionHandler {

    private static final int MAX_ENTRIES = 5000;
    private JexlEngine jexl;
    private final JexlExpressionHandlerOptions options = new JexlExpressionHandlerOptions();
    private final Uberspect pugUberspect = new Uberspect(LogFactory.getLog(JexlExpressionHandler.class),
            (op, obj) -> {
                if (obj instanceof Map) {
                    return JexlUberspect.MAP;
                }
                if (op == JexlOperator.ARRAY_GET) {
                    return JexlUberspect.MAP;
                } else if (op == JexlOperator.ARRAY_SET) {
                    return JexlUberspect.MAP;
                } else {
                    return JexlUberspect.POJO;
                }
            },
            JexlPermissions.UNRESTRICTED
    );

    private final PugJexlArithmetic pugJexlArithmetic = new PugJexlArithmetic(false);


    public JexlExpressionHandler() {
        jexl = getJexlEngine(options);
    }

    public JexlExpressionHandler(JexlExpressionHandlerOptions options) {
        jexl = getJexlEngine(options);
    }

    private JexlEngine getJexlEngine(JexlExpressionHandlerOptions options) {
        return getPugJexlBuilder(options).create();
    }

    private JexlBuilder getPugJexlBuilder(JexlExpressionHandlerOptions options) {
        return new PugJexlBuilder()
                .arithmetic(pugJexlArithmetic)
                .uberspect(pugUberspect)
                .safe(true)
                .silent(false)
                .strict(false)
                .cacheThreshold(options.getCacheThreshold())
                .cache(options.getCache())
                .debug(options.isDebug());
    }

    public Boolean evaluateBooleanExpression(String expression, PugModel model) throws ExpressionException {
        return BooleanUtil.convert(evaluateExpression(expression, model));
    }

    public Object evaluateExpression(String expression, PugModel model) throws ExpressionException {
        try {
            saveLocalVariableName(expression, model);
            expression = removeVar(expression);
            JexlScript e = jexl.createScript(expression);
            MapContext jexlContext = new MapContext(model);
            return e.execute(jexlContext);
        } catch (JexlException e) {
            throw new ExpressionException(expression, e);
        }
    }

    private String removeVar(String expression) {
        expression = expression.replace("var ", ";");
        expression = expression.replace("let ", ";");
        expression = expression.replace("const ", ";");
        return expression;
    }

    public void assertExpression(String expression) throws ExpressionException {
        try {
            jexl.createExpression(expression);
        } catch (JexlException e) {
            throw new ExpressionException(expression, e);
        }
    }

    public String evaluateStringExpression(String expression, PugModel model) throws ExpressionException {
        Object result = evaluateExpression(expression, model);
        return result == null ? "" : result.toString();
    }

    public void setCache(boolean cache) {
        if (cache)
            options.setCache(MAX_ENTRIES);
        else
            options.setCache(0);
        jexl = getJexlEngine(options);
    }

    public void clearCache() {
        jexl.clearCache();
    }
}
