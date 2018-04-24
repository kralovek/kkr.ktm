package kkr.ktm.domains.orchestrator.components.batchdev;

import org.apache.log4j.Logger;

import kkr.common.errors.BaseException;
import kkr.ktm.domains.common.components.context.level.ContextLevel;
import kkr.ktm.domains.common.components.expressionparser.Expression;

public class BatchDev extends BatchDevFwk {
	private static final Logger LOG = Logger.getLogger(BatchDev.class);

	public void run() throws BaseException {
		LOG.trace("BEGIN");
		try {
			String text = "TEXT.CONCAT('abcd', 'xyz', 123)";

			Expression expression = expressionParser.parseExpression(text);

			ContextLevel context = new ContextLevel();

			Object result = expression.evaluate(context);

			LOG.debug("Result: " + result);

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}
	}
}
