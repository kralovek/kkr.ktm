package kkr.ktm.domains.common.components.diffmanager.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class UtilsDiffManager {
	private static final Logger LOG = Logger.getLogger(UtilsDiffManager.class);

	public static Map<String, Object> toParametersDiffEntities(Collection<DiffEntity> diffEntities) {
		LOG.trace("BEGIN");
		try {
			Map<String, Object> retval = new HashMap<String, Object>();

			for (DiffEntity diffEntity : diffEntities) {
				String keyName = diffEntity.getName() + ".<NAME>";
				String keyIndex = diffEntity.getName() + ".<INDEX>";
				String keyItem = diffEntity.getName() + ".<ITEM>";
				String keyItemName = diffEntity.getName() + ".<ITEM>.<NAME>";
				String keyItemStatus = diffEntity.getName() + ".<ITEM>.<STATUS>";
				String keyItemIndex = diffEntity.getName() + ".<ITEM>.<INDEX>";

				retval.put(keyName, diffEntity.getName());
				retval.put(keyIndex, diffEntity.getLastIndex().toString());

				if (diffEntity.getItems().size() == 1) {
					DiffItem diffItem = diffEntity.getItems().iterator().next();

					retval.put(keyItemName, diffItem.getName());
					retval.put(keyItemIndex, diffItem.getIndex().toString());
					retval.put(keyItemStatus, diffItem.getStatus().name());

					for (Map.Entry<String, Object> entry : diffItem.getParameters().entrySet()) {
						retval.put(keyItem + "." + entry.getKey(), entry.getValue());
					}
				} else if (diffEntity.getItems().size() > 1) {
					String[] arrayName = new String[diffEntity.getItems().size()];
					String[] arrayIndex = new String[diffEntity.getItems().size()];
					String[] arrayStatus = new String[diffEntity.getItems().size()];
					retval.put(keyItemName, arrayName);
					retval.put(keyItemIndex, arrayIndex);
					retval.put(keyItemStatus, arrayStatus);

					Map<String, Object[]> arraysData = new LinkedHashMap<String, Object[]>();

					int i = 0;
					for (DiffItem diffItem : diffEntity.getItems()) {
						arrayName[i] = diffItem.getName();
						arrayIndex[i] = diffItem.getIndex().toString();
						arrayStatus[i] = diffItem.getStatus().name();

						for (Map.Entry<String, Object> entry : diffItem.getParameters().entrySet()) {
							String keyData = keyItem + "." + entry.getKey();
							Object[] arrayData = arraysData.get(keyData);
							if (arrayData == null) {
								arrayData = new Object[diffEntity.getItems().size()];
								arraysData.put(keyData, arrayData);
								retval.put(keyData, arrayData);
							}
							arrayData[i] = entry.getValue();
						}

						i++;
					}
				}
			}
			LOG.trace("OK");
			return retval;
		} finally {
			LOG.trace("END");
		}
	}

}
