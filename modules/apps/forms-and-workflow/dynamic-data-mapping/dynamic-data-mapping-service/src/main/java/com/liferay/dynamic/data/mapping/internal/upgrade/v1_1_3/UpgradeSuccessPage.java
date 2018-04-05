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

import com.liferay.portal.kernel.dao.jdbc.AutoBatchPreparedStatementUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.StringBundler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Pedro Queiroz
 */
public class UpgradeSuccessPage extends UpgradeProcess {

	public UpgradeSuccessPage(JSONFactory jsonFactory) {
		_jsonFactory = jsonFactory;
	}

	@Override
	protected void doUpgrade() throws Exception {
		upgradeDDMStructureVersionSuccessPage();
	}

	protected void updateSuccessPage(JSONObject definitionJSONObject) {
		JSONObject successPageJSONObject = definitionJSONObject.getJSONObject(
			"successPage");

		if (successPageJSONObject == null) {
			return;
		}

		JSONObject title = _jsonFactory.createJSONObject();
		JSONObject body = _jsonFactory.createJSONObject();

		title.put("en_US", successPageJSONObject.getString("title", ""));
		body.put("en_US", successPageJSONObject.getString("body", ""));

		successPageJSONObject.put("title", title);
		successPageJSONObject.put("body", body);
	}

	protected void upgradeDDMStructureVersionSuccessPage() throws Exception {
		StringBundler sb = new StringBundler(2);

		sb.append("select structureVersionId, definition from ");
		sb.append("DDMStructureVersion");

		try (PreparedStatement ps1 = connection.prepareStatement(sb.toString());
			PreparedStatement ps2 =
				AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					connection,
					"update DDMStructureVersion set definition = ? where " +
						"structureVersionId = ?");
			ResultSet rs = ps1.executeQuery()) {

			while (rs.next()) {
				String definition = rs.getString("definition");

				JSONObject jsonObject = _jsonFactory.createJSONObject(
					definition);

				updateSuccessPage(jsonObject);

				long structureVersionId = rs.getLong("structureVersionId");

				ps2.setString(1, jsonObject.toString());

				ps2.setLong(2, structureVersionId);

				ps2.addBatch();
			}

			ps2.executeBatch();
		}
	}

	private final JSONFactory _jsonFactory;

}