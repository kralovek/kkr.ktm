package kkr.ktm.components.executant.xstream.batch;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import kkr.ktm.components.executant.xstream.data.InputParameters;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.TechnicalException;
import kkr.ktm.utils.UtilsXStream;
import kkr.ktm.utils.errors.TreatErrors;



public class MainInputProperties {
	public static void main(final String[] pArgs) {
		final MainInputProperties mainInputProperties = new MainInputProperties();
		mainInputProperties.work(pArgs);
	}

	private void work(final String[] pArgs) {
		try {
			final ConfigInputProperties config = new ConfigInputProperties(
					pArgs);

			if (!config.getDirOutput().isDirectory()
					&& !config.getDirOutput().mkdirs()) {
				throw new TechnicalException("Cannot create the directory: "
						+ config.getDirOutput().getAbsolutePath());
			}

			final Class<?> clazz = loadClass(config.getClassName());

			final Method[] methods = clazz.getMethods();

			for (final Method method : methods) {
				if (Object.class.equals(method.getDeclaringClass())) {
					continue;
				}
				workMethod(config, clazz, method);
			}
		} catch (final Exception ex) {
			TreatErrors.treatException(ex);
		}
	}

	private Class<?> loadClass(final String pClassName) throws BaseException {
		try {
			return Class.forName(pClassName);
		} catch (final ClassNotFoundException ex) {
			throw new TechnicalException("Cannot create the class: "
					+ pClassName, ex);
		}
	}

	private void workMethod(final ConfigInputProperties pConfig,
			final Class<?> pClazz, final Method pMethod) throws BaseException {
		final InputParameters inputParameters = new InputParameters();
		inputParameters.setInstanceName(pClazz.getName());
		inputParameters.setMethodname(pMethod.getName());

		final List<Object> parameters = new ArrayList<Object>();
		inputParameters.setParameters(parameters);

		for (final Class<?> clazz : pMethod.getParameterTypes()) {
			final Object parameter = instantiateObject(clazz);
			parameters.add(parameter);
		}

		final String filenameRoot = generateFilenameRoot(pClazz, pMethod);
		final File file = new File(pConfig.getDirOutput(), filenameRoot
				+ ".xml");
		UtilsXStream.getInstance().toXMLFile(inputParameters, file);
	}

	private String generateFilenameRoot(final Class<?> pClass,
			final Method pMethod) {
		return pClass.getSimpleName() + "_" + pMethod.getName();
	}

	private Object instantiateObject(final Class<?> pClass) {
		return null;
	}
}
