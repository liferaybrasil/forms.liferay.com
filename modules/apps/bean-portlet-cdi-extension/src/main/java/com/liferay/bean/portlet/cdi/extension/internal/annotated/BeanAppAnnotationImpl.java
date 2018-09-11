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

package com.liferay.bean.portlet.cdi.extension.internal.annotated;

import com.liferay.bean.portlet.cdi.extension.internal.BeanApp;
import com.liferay.bean.portlet.cdi.extension.internal.Event;
import com.liferay.bean.portlet.cdi.extension.internal.PublicRenderParameter;
import com.liferay.bean.portlet.cdi.extension.internal.URLGenerationListener;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.annotations.CustomPortletMode;
import javax.portlet.annotations.EventDefinition;
import javax.portlet.annotations.PortletApplication;
import javax.portlet.annotations.PublicRenderParameterDefinition;
import javax.portlet.annotations.RuntimeOption;

/**
 * @author Neil Griffin
 */
public class BeanAppAnnotationImpl implements BeanApp {

	public BeanAppAnnotationImpl(PortletApplication portletApplication) {
		_containerRuntimeOptions = new HashMap<>();

		for (RuntimeOption runtimeOption :
				portletApplication.runtimeOptions()) {

			_containerRuntimeOptions.put(
				runtimeOption.name(), Arrays.asList(runtimeOption.values()));
		}

		_customPortletModes = new LinkedHashSet<>();

		for (CustomPortletMode customPortletMode :
				portletApplication.customPortletModes()) {

			if (!customPortletMode.portalManaged()) {
				_customPortletModes.add(customPortletMode.name());
			}
		}

		_defaultNamespace = portletApplication.defaultNamespaceURI();

		_eventDefinitions = new ArrayList<>();

		for (EventDefinition eventDefinition : portletApplication.events()) {
			_eventDefinitions.add(new EventAnnotationImpl(eventDefinition));
		}

		_publicRenderParameterMap = new HashMap<>();

		for (PublicRenderParameterDefinition
				publicRenderParameterDefinition:
					portletApplication.publicParams()) {

			PublicRenderParameter publicRenderParameter =
				new PublicRenderParameterAnnotationImpl(
					publicRenderParameterDefinition);

			_publicRenderParameterMap.put(
				publicRenderParameter.getIdentifier(), publicRenderParameter);
		}

		_specVersion = GetterUtil.getString(
			portletApplication.version(), "3.0");

		_urlGenerationListeners = new ArrayList<>();
	}

	@Override
	public Map<String, List<String>> getContainerRuntimeOptions() {
		return _containerRuntimeOptions;
	}

	@Override
	public Set<String> getCustomPortletModes() {
		return _customPortletModes;
	}

	@Override
	public String getDefaultNamespace() {
		return _defaultNamespace;
	}

	@Override
	public List<Event> getEvents() {
		return _eventDefinitions;
	}

	@Override
	public Map<String, PublicRenderParameter> getPublicRenderParameterMap() {
		return _publicRenderParameterMap;
	}

	@Override
	public String getSpecVersion() {
		return _specVersion;
	}

	@Override
	public List<URLGenerationListener> getURLGenerationListeners() {
		return _urlGenerationListeners;
	}

	private final Map<String, List<String>> _containerRuntimeOptions;
	private final Set<String> _customPortletModes;
	private final String _defaultNamespace;
	private final List<Event> _eventDefinitions;
	private final Map<String, PublicRenderParameter> _publicRenderParameterMap;
	private final String _specVersion;
	private final List<URLGenerationListener> _urlGenerationListeners;

}