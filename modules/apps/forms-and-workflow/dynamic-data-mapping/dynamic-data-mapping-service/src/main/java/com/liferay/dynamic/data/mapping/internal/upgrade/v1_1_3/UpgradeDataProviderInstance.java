/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v1_1_3;

import com.liferay.dynamic.data.mapping.data.provider.DDMDataProvider;
import com.liferay.dynamic.data.mapping.data.provider.DDMDataProviderTracker;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONDeserializer;
import com.liferay.dynamic.data.mapping.io.DDMFormValuesJSONSerializer;
import com.liferay.dynamic.data.mapping.model.UnlocalizedValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.util.DDMFormFactory;
import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.Map;

/**
 * @author Rafael Praxedes
 */
public class UpgradeDataProviderInstance extends UpgradeProcess {

	public UpgradeDataProviderInstance(
		DDMDataProviderTracker ddmDataProviderTracker,
		DDMFormValuesJSONDeserializer ddmFormValuesJSONDeserializer,
		DDMFormValuesJSONSerializer ddmFormValuesJSONSerializer) {

		_ddmDataProviderTracker = ddmDataProviderTracker;
		_ddmFormValuesJSONDeserializer = ddmFormValuesJSONDeserializer;
		_ddmFormValuesJSONSerializer = ddmFormValuesJSONSerializer;
	}

	protected void addDefaultInputParameters(DDMFormValues ddmFormValues) {
		List<DDMFormFieldValue> ddmFormFieldValues =
			ddmFormValues.getDDMFormFieldValues();

		for (DDMFormFieldValue ddmFormFieldValue : ddmFormFieldValues) {
			String ddmFormFieldValueName = ddmFormFieldValue.getName();

			if (ddmFormFieldValueName.equals("inputParameters")) {
				Map<String, List<DDMFormFieldValue>> nestedDDMFormFieldValues =
					ddmFormFieldValue.getNestedDDMFormFieldValuesMap();

				if (nestedDDMFormFieldValues.get("inputParameterLabel") ==
						null) {

					ddmFormFieldValue.addNestedDDMFormFieldValue(
						createDDMFormFieldValue(
							ddmFormValues, "inputParameterLabel",
							StringPool.BLANK));

					return;
				}
			}
		}
	}

	protected DDMFormFieldValue createDDMFormFieldValue(
		DDMFormValues ddmFormValues, String name, String value) {

		DDMFormFieldValue ddmFormFieldValue = new DDMFormFieldValue();

		ddmFormFieldValue.setDDMFormValues(ddmFormValues);
		ddmFormFieldValue.setInstanceId(StringUtil.randomString());
		ddmFormFieldValue.setName(name);

		if (Validator.isNotNull(value)) {
			ddmFormFieldValue.setValue(new UnlocalizedValue(value));
		}

		return ddmFormFieldValue;
	}

	@Override
	protected void doUpgrade() throws Exception {
		try (PreparedStatement ps1 = connection.prepareStatement(
				"select dataProviderInstanceId, definition, type_ from " +
					"DDMDataProviderInstance");
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMDataProviderInstance set definition = ? where " +
						"dataProviderInstanceId = ?");
			ResultSet rs = ps1.executeQuery()) {

			while (rs.next()) {
				long dataProviderInstanceId = rs.getLong(1);
				String dataProviderInstanceDefinition = rs.getString(2);

				String type = rs.getString(3);

				String newDefinition = upgradeDataProviderInstanceDefinition(
					dataProviderInstanceDefinition, type);

				ps2.setString(1, newDefinition);

				ps2.setLong(2, dataProviderInstanceId);

				ps2.addBatch();
			}

			ps2.executeBatch();
		}
	}

	protected String upgradeDataProviderInstanceDefinition(
			String dataProviderInstanceDefinition, String type)
		throws Exception {

		DDMDataProvider ddmDataProvider =
			_ddmDataProviderTracker.getDDMDataProvider(type);

		DDMFormValues ddmFormValues =
			_ddmFormValuesJSONDeserializer.deserialize(
				DDMFormFactory.create(ddmDataProvider.getSettings()),
				dataProviderInstanceDefinition);

		addDefaultInputParameters(ddmFormValues);

		return _ddmFormValuesJSONSerializer.serialize(ddmFormValues);
	}

	private final DDMDataProviderTracker _ddmDataProviderTracker;
	private final DDMFormValuesJSONDeserializer _ddmFormValuesJSONDeserializer;
	private final DDMFormValuesJSONSerializer _ddmFormValuesJSONSerializer;

}