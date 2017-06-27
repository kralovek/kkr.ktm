package kkr.ktm.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class UtilsBean {

	private static UtilsBean instance = new UtilsBean();

	public static BeanFactory createBeanFactory(String config, List<String> propertiesList, Map<String, String> parameters) {
		File fileConfig = new File(config);
		ConfigurableApplicationContext configurableApplicationContext;
		if (fileConfig.exists()) {
			try {
				fileConfig = fileConfig.getCanonicalFile();
			} catch (IOException ex) {
				throw new BeanCreationException("Cannot create canonical file: " + fileConfig.toString(), ex);
			}

			FileSystemXmlApplicationContext fileSystemXmlApplicationContext = new FileSystemXmlApplicationContext();
			fileSystemXmlApplicationContext.setConfigLocation(config);
			configurableApplicationContext = fileSystemXmlApplicationContext;
		} else {
			ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext();
			classPathXmlApplicationContext.setConfigLocation(config);
			configurableApplicationContext = classPathXmlApplicationContext;
		}

		if (propertiesList != null && !propertiesList.isEmpty()) {
			Resource[] resources = new Resource[propertiesList.size()];
			for (int i = 0; i < propertiesList.size(); i++) {
				String properties = propertiesList.get(i);
				File fileProperties = new File(properties);

				Resource resource = fileProperties.isFile() ? new FileSystemResource(fileProperties) : new ClassPathResource(properties);
				resources[i] = resource;
			}
			Properties propertiesAll = toProperties(resources);
			if (parameters != null) {
				Properties propertiesParameters = toProperties(parameters);
				propertiesAll.putAll(propertiesParameters);
			}
			PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
			propertyPlaceholderConfigurer.setProperties(propertiesAll);
			// propertyPlaceholderConfigurer.setLocations(resources);

			propertyPlaceholderConfigurer.setBeanFactory(configurableApplicationContext);

			configurableApplicationContext.addBeanFactoryPostProcessor(propertyPlaceholderConfigurer);
		}
		configurableApplicationContext.refresh();

		return configurableApplicationContext;
	}

	private static Properties toProperties(Map<String, String> parameters) {
		Properties properties = new Properties();
		properties.putAll(parameters);
		return properties;
	}

	private static Properties toProperties(Resource[] resources) {
		Properties properties = new Properties();
		for (Resource resource : resources) {
			InputStream inputStream = null;
			try {
				inputStream = resource.getInputStream();
				Properties propertiesResource = new Properties();
				propertiesResource.load(inputStream);
				inputStream.close();
				inputStream = null;
				properties.putAll(propertiesResource);
			} catch (IOException ex) {
				throw new BeanCreationException("Cannot load the properties from: " + resource.toString(), ex);
			} finally {
				UtilsFile.getInstance().close(inputStream);
			}
		}
		return properties;
	}
}
