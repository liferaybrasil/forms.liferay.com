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

package com.liferay.dynamic.data.lists.internal.upgrade.v1_0_3;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.dynamic.data.lists.model.DDLRecordSetConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.upgrade.AutoBatchPreparedStatementUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author Pedro Queiroz
 */
public class UpgradeDDLRecordSetVersion extends UpgradeProcess {

	public UpgradeDDLRecordSetVersion(CounterLocalService counterLocalService,
									  DDMStructureLocalService ddmStructureLocalService,
									  UserLocalService userLocalService) {
		_counterLocalService = counterLocalService;
		_ddmStructureLocalService = ddmStructureLocalService;
		_userLocalService = userLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		String sql =
			"select recordSetId, groupId, companyId, userId, userName, createDate," +
			"modifiedDate, DDMStructureId, name, description, settings_ from DDLRecordSet";

		try (PreparedStatement ps1 = connection.prepareStatement(sql);
			 PreparedStatement ps2 =
				 AutoBatchPreparedStatementUtil.concurrentAutoBatch(
					 connection,
					 "insert into DDLRecordSetVersion (recordSetVersionId, " +
					 "groupId, companyId, userId, userName, createDate," +
					 "recordSetId, DDMStructureVersionId, name, description, " +
					 "settings_, version,  status, statusByUserId, statusByUserName, " +
					 "statusDate) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
					 "?, ?, ?, ?, ?)")) {

			try (ResultSet rs = ps1.executeQuery()) {
				while (rs.next()) {
					long recordSetId = rs.getLong("recordSetId");

					long userId = rs.getLong("userId");

					User user = _userLocalService.getUser(userId);

					ps2.setLong(1, _counterLocalService.increment());
					ps2.setLong(2, rs.getLong("groupId"));
					ps2.setLong(3, rs.getLong("companyId"));
					ps2.setLong(4, userId);
					ps2.setString(5, rs.getString("userName"));
					ps2.setDate(6, rs.getDate("createDate"));
					ps2.setLong(7, recordSetId);
					ps2.setString(8, getDDMStructureVersionId(rs.getLong("DDMStructureId")));
					ps2.setString(9, rs.getString("name"));
					ps2.setString(10, rs.getString("description"));
					ps2.setString(11, rs.getString("settings_"));
					ps2.setString(12, DDLRecordSetConstants.VERSION_DEFAULT);
					ps2.setInt(13, WorkflowConstants.STATUS_APPROVED);
					ps2.setLong(14, user.getUserId());
					ps2.setString(15, user.getFullName());
					ps2.setDate(16, rs.getDate("modifiedDate"));

					ps2.addBatch();
				}

				ps2.executeBatch();
			}
		}
	}

	protected String getDDMStructureVersionId(long ddmStructureId)
		throws PortalException {

		DDMStructure ddmStructure = _ddmStructureLocalService.getStructure(
			ddmStructureId);

		return ddmStructure.getVersion();
	}

	private final CounterLocalService _counterLocalService;

	private final DDMStructureLocalService _ddmStructureLocalService;

	private final UserLocalService _userLocalService;

}
