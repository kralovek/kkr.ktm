package kkr.ktm.components.runner.clean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import kkr.ktm.components.cleanmanager.CleanManager;
import kkr.ktm.components.cleanmanager.CleanManager.Group;
import kkr.ktm.components.runner.Runner;
import kkr.ktm.exception.BaseException;
import kkr.ktm.exception.ConfigurationException;

public class RunnerClean extends RunnerCleanFwk implements Runner {
	private static final Logger LOG = Logger.getLogger(RunnerClean.class);

	private static final String PARAM_INPUT_REMOVE_PATTERNS_PREFIX = "INPUT/REMOVE_PATTERNS/";

	private static class GroupImpl implements CleanManager.Group {
		private String name;
		private List<String> patterns;
		
		public void setName(String name) {
			this.name = name;
		}

		public void setPatterns(List<String> patterns) {
			this.patterns = patterns;
		}

		public String getName() {
			return name;
		}

		public List<String> getPatterns() {
			return patterns;
		}
	}
	
	private static class CleanDetail {
		private CleanManager cleanManager;
		private List<Group> groupes;

		public CleanManager getCleanManager() {
			return cleanManager;
		}

		public void setCleanManager(CleanManager cleanManager) {
			this.cleanManager = cleanManager;
		}

		public List<Group> getGroupes() {
			return groupes;
		}

		public void setGroupes(List<Group> groupes) {
			this.groupes = groupes;
		}
	}

	public Map<String, Object> run(Map<String, Object> parameters)
			throws BaseException {
		LOG.trace("BEGIN");
		try {
			testConfigured();
			Map<String, Object> results = new HashMap<String, Object>();

			if (cleanManagers.isEmpty()) {
				LOG.warn("No clean manager is defined");
				LOG.trace("OK");
				return results;
			}

			List<CleanDetail> cleanCalls = prepareCleanCalls(parameters);

			for (CleanDetail cleanCall : cleanCalls) {
				cleanCall.getCleanManager().clean(cleanCall.getGroupes());
			}

			LOG.trace("OK");
			return results;
		} finally {
			LOG.trace("END");
		}
	}

	private List<CleanDetail> prepareCleanCalls(Map<String, Object> parameters) throws BaseException {
		Map<String, Map<String, List<String>>> cleanNames = new HashMap<String, Map<String,List<String>>>();
		
		for (Map.Entry<String, Object> entry : parameters.entrySet()) {
			String[] cleanGroup = getCleanGroupFromParameter(entry.getKey());
			if (cleanGroup == null) {
				continue;
			}
			if (entry.getValue() == null) {
				continue;
			}
			List<String> patterns = new ArrayList<String>();
			boolean error = false;
			if (entry.getValue() instanceof String) {
				patterns.add((String) entry.getValue());
			} else if (entry.getValue().getClass().isArray()) {
				Object[] arrayPatterns = (Object[]) entry.getValue();
				for (Object arrayPattern : arrayPatterns) {
					if (arrayPattern instanceof String) {
						patterns.add((String) arrayPattern);
					} else {
						error = true;
						break;
					}
				}
			} else {
				error = true;
			}

			if (error) {
				throw new ConfigurationException("The value of the parameter " + entry.getKey() + " must be a string or an array of strings");
			}

			
			Map<String, List<String>> groupsPatterns = cleanNames.get(cleanGroup[0]);
			if (groupsPatterns == null) {
				groupsPatterns = new HashMap<String, List<String>>();
				cleanNames.put(cleanGroup[0], groupsPatterns);
			}
			List<String> groupPatterns = groupsPatterns.get(cleanGroup[1]);
			if (groupPatterns == null) {
				groupPatterns = new ArrayList<String>();
				groupsPatterns.put(cleanGroup[1], groupPatterns);
			}
			groupPatterns.addAll(patterns);
		}
		
		List<CleanDetail> retval = new ArrayList<CleanDetail>();
		for (Map.Entry<String, Map<String, List<String>>> entry : cleanNames.entrySet()) {
			CleanManager cleanManager = chooseCleanManager(entry.getKey());
			if (cleanManager == null) {
				throw new ConfigurationException("The clean manager is not defined: " + entry.getKey());
			}
			List<Group> cleanGroups = new ArrayList<Group>(); 
			for (Map.Entry<String, List<String>> entry2 : entry.getValue().entrySet()) {
				GroupImpl group = new GroupImpl();
				group.setName(entry2.getKey());
				group.setPatterns(entry2.getValue());
				cleanGroups.add(group);
			}
			CleanDetail cleanDetail = new CleanDetail();
			cleanDetail.setCleanManager(cleanManager);
			cleanDetail.setGroupes(cleanGroups);
			retval.add(cleanDetail);
		}
		
		
		return retval;
	}

	private String[] getCleanGroupFromParameter(String parameter) throws BaseException {
		String prefix = sysParamPrefix + PARAM_INPUT_REMOVE_PATTERNS_PREFIX;
		if (parameter == null) {
			return null;
		}
		if (!parameter.startsWith(prefix)) {
			return null;
		}
		String[] cleanGroup;
		if (
				parameter.equals(prefix) //
				|| (cleanGroup = parameter.substring(prefix.length()).split("\\/")).length != 2 //
				|| cleanGroup[0].isEmpty() || cleanGroup[1].isEmpty()) {
			throw new ConfigurationException("The paramters starting by " + prefix + " must follow nameCleanManager/nameGroup: " + parameter);
		}
		return cleanGroup;
	}
	
	
	private CleanManager chooseCleanManager(String name) {
		for (CleanManager cleanManager : cleanManagers) {
			if (cleanManager.getName().equals(name)) {
				return cleanManager;
			}
		}
		return null;
	}
}
