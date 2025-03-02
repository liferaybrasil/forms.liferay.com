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

package com.liferay.mobile.device.rules.service;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Provides the local service utility for MDRRuleGroupInstance. This utility wraps
 * <code>com.liferay.mobile.device.rules.service.impl.MDRRuleGroupInstanceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Edward C. Han
 * @see MDRRuleGroupInstanceLocalService
 * @generated
 */
public class MDRRuleGroupInstanceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.mobile.device.rules.service.impl.MDRRuleGroupInstanceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the mdr rule group instance to the database. Also notifies the appropriate model listeners.
	 *
	 * @param mdrRuleGroupInstance the mdr rule group instance
	 * @return the mdr rule group instance that was added
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		addMDRRuleGroupInstance(
			com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
				mdrRuleGroupInstance) {

		return getService().addMDRRuleGroupInstance(mdrRuleGroupInstance);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			addRuleGroupInstance(
				long groupId, String className, long classPK, long ruleGroupId,
				int priority,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addRuleGroupInstance(
			groupId, className, classPK, ruleGroupId, priority, serviceContext);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			addRuleGroupInstance(
				long groupId, String className, long classPK, long ruleGroupId,
				com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addRuleGroupInstance(
			groupId, className, classPK, ruleGroupId, serviceContext);
	}

	/**
	 * Creates a new mdr rule group instance with the primary key. Does not add the mdr rule group instance to the database.
	 *
	 * @param ruleGroupInstanceId the primary key for the new mdr rule group instance
	 * @return the new mdr rule group instance
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		createMDRRuleGroupInstance(long ruleGroupInstanceId) {

		return getService().createMDRRuleGroupInstance(ruleGroupInstanceId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			createPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	public static void deleteGroupRuleGroupInstances(long groupId) {
		getService().deleteGroupRuleGroupInstances(groupId);
	}

	/**
	 * Deletes the mdr rule group instance with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param ruleGroupInstanceId the primary key of the mdr rule group instance
	 * @return the mdr rule group instance that was removed
	 * @throws PortalException if a mdr rule group instance with the primary key could not be found
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			deleteMDRRuleGroupInstance(long ruleGroupInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteMDRRuleGroupInstance(ruleGroupInstanceId);
	}

	/**
	 * Deletes the mdr rule group instance from the database. Also notifies the appropriate model listeners.
	 *
	 * @param mdrRuleGroupInstance the mdr rule group instance
	 * @return the mdr rule group instance that was removed
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		deleteMDRRuleGroupInstance(
			com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
				mdrRuleGroupInstance) {

		return getService().deleteMDRRuleGroupInstance(mdrRuleGroupInstance);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			deletePersistedModel(
				com.liferay.portal.kernel.model.PersistedModel persistedModel)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	public static void deleteRuleGroupInstance(long ruleGroupInstanceId) {
		getService().deleteRuleGroupInstance(ruleGroupInstanceId);
	}

	public static void deleteRuleGroupInstance(
		com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			ruleGroupInstance) {

		getService().deleteRuleGroupInstance(ruleGroupInstance);
	}

	public static void deleteRuleGroupInstances(long ruleGroupId) {
		getService().deleteRuleGroupInstances(ruleGroupId);
	}

	public static com.liferay.portal.kernel.dao.orm.DynamicQuery
		dynamicQuery() {

		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQuery(dynamicQuery);
	}

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.mobile.device.rules.model.impl.MDRRuleGroupInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.mobile.device.rules.model.impl.MDRRuleGroupInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	public static <T> java.util.List<T> dynamicQuery(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery) {

		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		com.liferay.portal.kernel.dao.orm.DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		fetchMDRRuleGroupInstance(long ruleGroupInstanceId) {

		return getService().fetchMDRRuleGroupInstance(ruleGroupInstanceId);
	}

	/**
	 * Returns the mdr rule group instance matching the UUID and group.
	 *
	 * @param uuid the mdr rule group instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching mdr rule group instance, or <code>null</code> if a matching mdr rule group instance could not be found
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		fetchMDRRuleGroupInstanceByUuidAndGroupId(String uuid, long groupId) {

		return getService().fetchMDRRuleGroupInstanceByUuidAndGroupId(
			uuid, groupId);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		fetchRuleGroupInstance(long ruleGroupInstanceId) {

		return getService().fetchRuleGroupInstance(ruleGroupInstanceId);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		fetchRuleGroupInstance(
			String className, long classPK, long ruleGroupId) {

		return getService().fetchRuleGroupInstance(
			className, classPK, ruleGroupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the mdr rule group instance with the primary key.
	 *
	 * @param ruleGroupInstanceId the primary key of the mdr rule group instance
	 * @return the mdr rule group instance
	 * @throws PortalException if a mdr rule group instance with the primary key could not be found
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			getMDRRuleGroupInstance(long ruleGroupInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMDRRuleGroupInstance(ruleGroupInstanceId);
	}

	/**
	 * Returns the mdr rule group instance matching the UUID and group.
	 *
	 * @param uuid the mdr rule group instance's UUID
	 * @param groupId the primary key of the group
	 * @return the matching mdr rule group instance
	 * @throws PortalException if a matching mdr rule group instance could not be found
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			getMDRRuleGroupInstanceByUuidAndGroupId(String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMDRRuleGroupInstanceByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns a range of all the mdr rule group instances.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.mobile.device.rules.model.impl.MDRRuleGroupInstanceModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of mdr rule group instances
	 * @param end the upper bound of the range of mdr rule group instances (not inclusive)
	 * @return the range of mdr rule group instances
	 */
	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getMDRRuleGroupInstances(int start, int end) {

		return getService().getMDRRuleGroupInstances(start, end);
	}

	/**
	 * Returns all the mdr rule group instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the mdr rule group instances
	 * @param companyId the primary key of the company
	 * @return the matching mdr rule group instances, or an empty list if no matches were found
	 */
	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getMDRRuleGroupInstancesByUuidAndCompanyId(
				String uuid, long companyId) {

		return getService().getMDRRuleGroupInstancesByUuidAndCompanyId(
			uuid, companyId);
	}

	/**
	 * Returns a range of mdr rule group instances matching the UUID and company.
	 *
	 * @param uuid the UUID of the mdr rule group instances
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of mdr rule group instances
	 * @param end the upper bound of the range of mdr rule group instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching mdr rule group instances, or an empty list if no matches were found
	 */
	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getMDRRuleGroupInstancesByUuidAndCompanyId(
				String uuid, long companyId, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
						orderByComparator) {

		return getService().getMDRRuleGroupInstancesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of mdr rule group instances.
	 *
	 * @return the number of mdr rule group instances
	 */
	public static int getMDRRuleGroupInstancesCount() {
		return getService().getMDRRuleGroupInstancesCount();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			getRuleGroupInstance(long ruleGroupInstanceId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getRuleGroupInstance(ruleGroupInstanceId);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			getRuleGroupInstance(
				String className, long classPK, long ruleGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getRuleGroupInstance(
			className, classPK, ruleGroupId);
	}

	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getRuleGroupInstances(long ruleGroupId) {

		return getService().getRuleGroupInstances(ruleGroupId);
	}

	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getRuleGroupInstances(long ruleGroupId, int start, int end) {

		return getService().getRuleGroupInstances(ruleGroupId, start, end);
	}

	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getRuleGroupInstances(String className, long classPK) {

		return getService().getRuleGroupInstances(className, classPK);
	}

	public static java.util.List
		<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
			getRuleGroupInstances(
				String className, long classPK, int start, int end,
				com.liferay.portal.kernel.util.OrderByComparator
					<com.liferay.mobile.device.rules.model.MDRRuleGroupInstance>
						orderByComparator) {

		return getService().getRuleGroupInstances(
			className, classPK, start, end, orderByComparator);
	}

	public static int getRuleGroupInstancesCount(long ruleGroupId) {
		return getService().getRuleGroupInstancesCount(ruleGroupId);
	}

	public static int getRuleGroupInstancesCount(
		String className, long classPK) {

		return getService().getRuleGroupInstancesCount(className, classPK);
	}

	/**
	 * Updates the mdr rule group instance in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param mdrRuleGroupInstance the mdr rule group instance
	 * @return the mdr rule group instance that was updated
	 */
	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
		updateMDRRuleGroupInstance(
			com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
				mdrRuleGroupInstance) {

		return getService().updateMDRRuleGroupInstance(mdrRuleGroupInstance);
	}

	public static com.liferay.mobile.device.rules.model.MDRRuleGroupInstance
			updateRuleGroupInstance(long ruleGroupInstanceId, int priority)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateRuleGroupInstance(
			ruleGroupInstanceId, priority);
	}

	public static MDRRuleGroupInstanceLocalService getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker
		<MDRRuleGroupInstanceLocalService, MDRRuleGroupInstanceLocalService>
			_serviceTracker;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			MDRRuleGroupInstanceLocalService.class);

		ServiceTracker
			<MDRRuleGroupInstanceLocalService, MDRRuleGroupInstanceLocalService>
				serviceTracker =
					new ServiceTracker
						<MDRRuleGroupInstanceLocalService,
						 MDRRuleGroupInstanceLocalService>(
							 bundle.getBundleContext(),
							 MDRRuleGroupInstanceLocalService.class, null);

		serviceTracker.open();

		_serviceTracker = serviceTracker;
	}

}