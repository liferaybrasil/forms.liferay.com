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

package com.liferay.portal.kernel.service;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;

/**
 * Provides the local service utility for LayoutBranch. This utility wraps
 * <code>com.liferay.portal.service.impl.LayoutBranchLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutBranchLocalService
 * @generated
 */
public class LayoutBranchLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.LayoutBranchLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the layout branch to the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranch the layout branch
	 * @return the layout branch that was added
	 */
	public static com.liferay.portal.kernel.model.LayoutBranch addLayoutBranch(
		com.liferay.portal.kernel.model.LayoutBranch layoutBranch) {

		return getService().addLayoutBranch(layoutBranch);
	}

	public static com.liferay.portal.kernel.model.LayoutBranch addLayoutBranch(
			long layoutSetBranchId, long plid, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addLayoutBranch(
			layoutSetBranchId, plid, name, description, master, serviceContext);
	}

	public static com.liferay.portal.kernel.model.LayoutBranch addLayoutBranch(
			long layoutRevisionId, String name, String description,
			boolean master, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addLayoutBranch(
			layoutRevisionId, name, description, master, serviceContext);
	}

	/**
	 * Creates a new layout branch with the primary key. Does not add the layout branch to the database.
	 *
	 * @param layoutBranchId the primary key for the new layout branch
	 * @return the new layout branch
	 */
	public static com.liferay.portal.kernel.model.LayoutBranch
		createLayoutBranch(long layoutBranchId) {

		return getService().createLayoutBranch(layoutBranchId);
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			createPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Deletes the layout branch from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranch the layout branch
	 * @return the layout branch that was removed
	 */
	public static com.liferay.portal.kernel.model.LayoutBranch
		deleteLayoutBranch(
			com.liferay.portal.kernel.model.LayoutBranch layoutBranch) {

		return getService().deleteLayoutBranch(layoutBranch);
	}

	/**
	 * Deletes the layout branch with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch that was removed
	 * @throws PortalException if a layout branch with the primary key could not be found
	 */
	public static com.liferay.portal.kernel.model.LayoutBranch
			deleteLayoutBranch(long layoutBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deleteLayoutBranch(layoutBranchId);
	}

	public static void deleteLayoutSetBranchLayoutBranches(
			long layoutSetBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteLayoutSetBranchLayoutBranches(layoutSetBranchId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
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

	public static com.liferay.portal.kernel.model.LayoutBranch
		fetchLayoutBranch(long layoutBranchId) {

		return getService().fetchLayoutBranch(layoutBranchId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the layout branch with the primary key.
	 *
	 * @param layoutBranchId the primary key of the layout branch
	 * @return the layout branch
	 * @throws PortalException if a layout branch with the primary key could not be found
	 */
	public static com.liferay.portal.kernel.model.LayoutBranch getLayoutBranch(
			long layoutBranchId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getLayoutBranch(layoutBranchId);
	}

	public static java.util.List<com.liferay.portal.kernel.model.LayoutBranch>
		getLayoutBranches(
			long layoutSetBranchId, long plid, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<com.liferay.portal.kernel.model.LayoutBranch>
					orderByComparator) {

		return getService().getLayoutBranches(
			layoutSetBranchId, plid, start, end, orderByComparator);
	}

	/**
	 * Returns a range of all the layout branches.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.LayoutBranchModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of layout branches
	 * @param end the upper bound of the range of layout branches (not inclusive)
	 * @return the range of layout branches
	 */
	public static java.util.List<com.liferay.portal.kernel.model.LayoutBranch>
		getLayoutBranchs(int start, int end) {

		return getService().getLayoutBranchs(start, end);
	}

	/**
	 * Returns the number of layout branches.
	 *
	 * @return the number of layout branches
	 */
	public static int getLayoutBranchsCount() {
		return getService().getLayoutBranchsCount();
	}

	public static java.util.List<com.liferay.portal.kernel.model.LayoutBranch>
		getLayoutSetBranchLayoutBranches(long layoutSetBranchId) {

		return getService().getLayoutSetBranchLayoutBranches(layoutSetBranchId);
	}

	public static com.liferay.portal.kernel.model.LayoutBranch
			getMasterLayoutBranch(long layoutSetBranchId, long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMasterLayoutBranch(layoutSetBranchId, plid);
	}

	public static com.liferay.portal.kernel.model.LayoutBranch
			getMasterLayoutBranch(
				long layoutSetBranchId, long plid,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getMasterLayoutBranch(
			layoutSetBranchId, plid, serviceContext);
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

	/**
	 * Updates the layout branch in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param layoutBranch the layout branch
	 * @return the layout branch that was updated
	 */
	public static com.liferay.portal.kernel.model.LayoutBranch
		updateLayoutBranch(
			com.liferay.portal.kernel.model.LayoutBranch layoutBranch) {

		return getService().updateLayoutBranch(layoutBranch);
	}

	public static com.liferay.portal.kernel.model.LayoutBranch
			updateLayoutBranch(
				long layoutBranchId, String name, String description,
				ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateLayoutBranch(
			layoutBranchId, name, description, serviceContext);
	}

	public static LayoutBranchLocalService getService() {
		if (_service == null) {
			_service = (LayoutBranchLocalService)PortalBeanLocatorUtil.locate(
				LayoutBranchLocalService.class.getName());
		}

		return _service;
	}

	private static LayoutBranchLocalService _service;

}