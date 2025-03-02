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
 * Provides the local service utility for PasswordTracker. This utility wraps
 * <code>com.liferay.portal.service.impl.PasswordTrackerLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see PasswordTrackerLocalService
 * @generated
 */
public class PasswordTrackerLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.PasswordTrackerLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the password tracker to the database. Also notifies the appropriate model listeners.
	 *
	 * @param passwordTracker the password tracker
	 * @return the password tracker that was added
	 */
	public static com.liferay.portal.kernel.model.PasswordTracker
		addPasswordTracker(
			com.liferay.portal.kernel.model.PasswordTracker passwordTracker) {

		return getService().addPasswordTracker(passwordTracker);
	}

	/**
	 * Creates a new password tracker with the primary key. Does not add the password tracker to the database.
	 *
	 * @param passwordTrackerId the primary key for the new password tracker
	 * @return the new password tracker
	 */
	public static com.liferay.portal.kernel.model.PasswordTracker
		createPasswordTracker(long passwordTrackerId) {

		return getService().createPasswordTracker(passwordTrackerId);
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
	 * Deletes the password tracker with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * @param passwordTrackerId the primary key of the password tracker
	 * @return the password tracker that was removed
	 * @throws PortalException if a password tracker with the primary key could not be found
	 */
	public static com.liferay.portal.kernel.model.PasswordTracker
			deletePasswordTracker(long passwordTrackerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().deletePasswordTracker(passwordTrackerId);
	}

	/**
	 * Deletes the password tracker from the database. Also notifies the appropriate model listeners.
	 *
	 * @param passwordTracker the password tracker
	 * @return the password tracker that was removed
	 */
	public static com.liferay.portal.kernel.model.PasswordTracker
		deletePasswordTracker(
			com.liferay.portal.kernel.model.PasswordTracker passwordTracker) {

		return getService().deletePasswordTracker(passwordTracker);
	}

	public static void deletePasswordTrackers(long userId) {
		getService().deletePasswordTrackers(userId);
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordTrackerModelImpl</code>.
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
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordTrackerModelImpl</code>.
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

	public static com.liferay.portal.kernel.model.PasswordTracker
		fetchPasswordTracker(long passwordTrackerId) {

		return getService().fetchPasswordTracker(passwordTrackerId);
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
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Returns the password tracker with the primary key.
	 *
	 * @param passwordTrackerId the primary key of the password tracker
	 * @return the password tracker
	 * @throws PortalException if a password tracker with the primary key could not be found
	 */
	public static com.liferay.portal.kernel.model.PasswordTracker
			getPasswordTracker(long passwordTrackerId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPasswordTracker(passwordTrackerId);
	}

	/**
	 * Returns a range of all the password trackers.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.portal.model.impl.PasswordTrackerModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of password trackers
	 * @param end the upper bound of the range of password trackers (not inclusive)
	 * @return the range of password trackers
	 */
	public static java.util.List
		<com.liferay.portal.kernel.model.PasswordTracker> getPasswordTrackers(
			int start, int end) {

		return getService().getPasswordTrackers(start, end);
	}

	/**
	 * Returns the number of password trackers.
	 *
	 * @return the number of password trackers
	 */
	public static int getPasswordTrackersCount() {
		return getService().getPasswordTrackersCount();
	}

	/**
	 * @throws PortalException
	 */
	public static com.liferay.portal.kernel.model.PersistedModel
			getPersistedModel(java.io.Serializable primaryKeyObj)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

	public static boolean isSameAsCurrentPassword(
			long userId, String newClearTextPwd)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().isSameAsCurrentPassword(userId, newClearTextPwd);
	}

	public static boolean isValidPassword(long userId, String newClearTextPwd)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().isValidPassword(userId, newClearTextPwd);
	}

	public static void trackPassword(long userId, String encPassword)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().trackPassword(userId, encPassword);
	}

	/**
	 * Updates the password tracker in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * @param passwordTracker the password tracker
	 * @return the password tracker that was updated
	 */
	public static com.liferay.portal.kernel.model.PasswordTracker
		updatePasswordTracker(
			com.liferay.portal.kernel.model.PasswordTracker passwordTracker) {

		return getService().updatePasswordTracker(passwordTracker);
	}

	public static PasswordTrackerLocalService getService() {
		if (_service == null) {
			_service =
				(PasswordTrackerLocalService)PortalBeanLocatorUtil.locate(
					PasswordTrackerLocalService.class.getName());
		}

		return _service;
	}

	private static PasswordTrackerLocalService _service;

}