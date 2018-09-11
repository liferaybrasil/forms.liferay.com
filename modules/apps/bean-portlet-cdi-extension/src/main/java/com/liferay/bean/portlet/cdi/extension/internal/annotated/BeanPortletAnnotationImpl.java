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

import com.liferay.bean.portlet.LiferayPortletConfiguration;
import com.liferay.bean.portlet.cdi.extension.internal.BaseBeanPortletImpl;
import com.liferay.bean.portlet.cdi.extension.internal.BeanApp;
import com.liferay.bean.portlet.cdi.extension.internal.PortletDependency;
import com.liferay.bean.portlet.cdi.extension.internal.PublicRenderParameter;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.LiferayPortletMode;
import com.liferay.portal.kernel.util.HashMapDictionary;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.annotations.Dependency;
import javax.portlet.annotations.InitParameter;
import javax.portlet.annotations.LocaleString;
import javax.portlet.annotations.PortletApplication;
import javax.portlet.annotations.PortletConfiguration;
import javax.portlet.annotations.Preference;
import javax.portlet.annotations.RuntimeOption;
import javax.portlet.annotations.SecurityRoleRef;
import javax.portlet.annotations.Supports;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

/**
 * @author Neil Griffin
 * @author Raymond Augé
 */
public class BeanPortletAnnotationImpl extends BaseBeanPortletImpl {

	public BeanPortletAnnotationImpl(
		PortletApplication portletApplication,
		PortletConfiguration portletConfiguration,
		LiferayPortletConfiguration liferayPortletConfiguration,
		String portletClassName) {

		_beanApp = new BeanAppAnnotationImpl(portletApplication);

		_portletConfiguration = portletConfiguration;
		_portletClassName = portletClassName;

		String[] propertyNames = null;

		if (liferayPortletConfiguration != null) {
			propertyNames = liferayPortletConfiguration.properties();
		}

		if ((propertyNames == null) || (propertyNames.length == 0)) {
			_liferayPortletConfigurationProperties = Collections.emptyMap();
		}
		else {
			_liferayPortletConfigurationProperties = new HashMap<>();

			for (String propertyName : propertyNames) {
				String propertyValue = null;

				int equalsPos = propertyName.indexOf(CharPool.EQUAL);

				if (equalsPos > 0) {
					propertyName = propertyName.substring(0, equalsPos);

					propertyValue = propertyName.substring(equalsPos + 1);
				}

				_liferayPortletConfigurationProperties.put(
					propertyName, propertyValue);
			}
		}

		for (Dependency dependency : portletConfiguration.dependencies()) {
			addPortletDependency(
				new PortletDependency(
					dependency.name(), dependency.scope(),
					dependency.version()));
		}

		_portletModes = new HashSet<>(_liferayPortletModes);

		_portletModes.addAll(_beanApp.getCustomPortletModes());
	}

	@Override
	public BeanApp getBeanApp() {
		return _beanApp;
	}

	@Override
	public String getPortletClassName() {
		return _portletClassName;
	}

	@Override
	public String getPortletName() {
		return _portletConfiguration.portletName();
	}

	@Override
	public String getResourceBundle() {
		return _portletConfiguration.resourceBundle();
	}

	@Override
	public Dictionary<String, Object> toDictionary() {
		HashMapDictionary<String, Object> dictionary = toDictionary(_beanApp);

		dictionary.put(
			"javax.portlet.async-supported",
			_portletConfiguration.asyncSupported());

		Map<String, List<String>> containerRuntimeOptions = new HashMap<>(
			_beanApp.getContainerRuntimeOptions());

		for (RuntimeOption runtimeOption :
				_portletConfiguration.runtimeOptions()) {

			containerRuntimeOptions.put(
				runtimeOption.name(), Arrays.asList(runtimeOption.values()));
		}

		for (Map.Entry<String, List<String>> entry :
				containerRuntimeOptions.entrySet()) {

			dictionary.put(
				"javax.portlet.container-runtime-option.".concat(
					entry.getKey()),
				entry.getValue());
		}

		dictionary.put(
			"javax.portlet.expiration-cache",
			_portletConfiguration.cacheExpirationTime());

		for (InitParameter initParameter : _portletConfiguration.initParams()) {
			String value = initParameter.value();

			if (value != null) {
				dictionary.put(
					"javax.portlet.init-param.".concat(initParameter.name()),
					value);
			}
		}

		_putEnglishText(
			dictionary, "javax.portlet.description",
			_portletConfiguration.description());

		_putEnglishText(
			dictionary, "javax.portlet.display-name",
			_portletConfiguration.displayName());

		_putEnglishText(
			dictionary, "javax.portlet.info.keywords",
			_portletConfiguration.keywords());

		_putEnglishText(
			dictionary, "javax.portlet.info.short-title",
			_portletConfiguration.shortTitle());

		_putEnglishText(
			dictionary, "javax.portlet.info.title",
			_portletConfiguration.title(), getPortletName());

		List<String> supportedPortletModes = new ArrayList<>();

		for (Supports supports : _portletConfiguration.supports()) {
			StringBundler portletModesSB = new StringBundler();

			String[] portletModes = supports.portletModes();

			boolean first = true;

			for (String portletMode : portletModes) {
				if (_portletModes.contains(portletMode)) {
					if (first) {
						first = false;
					}
					else {
						portletModesSB.append(",");
					}

					portletModesSB.append(portletMode);
				}
			}

			supportedPortletModes.add(
				toNameValuePair(
					supports.mimeType(), portletModesSB.toString()));
		}

		if (!supportedPortletModes.isEmpty()) {
			dictionary.put("javax.portlet.portlet-mode", supportedPortletModes);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<portlet-preferences>");

		for (Preference preference : _portletConfiguration.prefs()) {
			sb.append("<preference>");
			sb.append("<name>");
			sb.append(preference.name());
			sb.append("</name>");

			for (String value : preference.values()) {
				sb.append("<value>");
				sb.append(value);
				sb.append("</value>");
			}

			sb.append("<read-only>");
			sb.append(preference.isReadOnly());
			sb.append("</read-only>");

			sb.append("</preference>");
		}

		sb.append("</portlet-preferences>");

		dictionary.put("javax.portlet.preferences", sb.toString());

		String resourceBundle = _portletConfiguration.resourceBundle();

		if ((resourceBundle != null) && !resourceBundle.isEmpty()) {
			dictionary.put("javax.portlet.resource-bundle", resourceBundle);
		}

		StringBundler securityRoleRefSB = new StringBundler();

		boolean first = true;

		for (SecurityRoleRef securityRoleRef :
				_portletConfiguration.roleRefs()) {

			if (first) {
				first = false;
			}
			else {
				securityRoleRefSB.append(",");
			}

			securityRoleRefSB.append(securityRoleRef.roleName());
		}

		if (securityRoleRefSB.length() > 0) {
			dictionary.put(
				"javax.portlet.security-role-ref",
				securityRoleRefSB.toString());
		}

		dictionary.put(
			"javax.portlet.supported-locale",
			_portletConfiguration.supportedLocales());

		List<String> supportedPublicRenderParameters = new ArrayList<>();

		for (String identifier : _portletConfiguration.publicParams()) {
			supportedPublicRenderParameters.add(
				toNameValuePair(
					identifier,
					_getPublicRenderParameterNamespaceURI(
						_beanApp, identifier)));
		}

		dictionary.put(
			"javax.portlet.supported-public-render-parameter",
			supportedPublicRenderParameters);

		List<String> supportedWindowStates = new ArrayList<>();

		for (Supports supports : _portletConfiguration.supports()) {
			StringBundler windowStatesSB = new StringBundler();

			String[] windowStates = supports.windowStates();

			first = true;

			for (String windowState : windowStates) {
				if (first) {
					first = false;
				}
				else {
					windowStatesSB.append(",");
				}

				windowStatesSB.append(windowState);
			}

			supportedWindowStates.add(
				toNameValuePair(
					supports.mimeType(), windowStatesSB.toString()));
		}

		if (!supportedWindowStates.isEmpty()) {
			dictionary.put("javax.portlet.window-state", supportedWindowStates);
		}

		dictionary.putAll(_liferayPortletConfigurationProperties);

		return dictionary;
	}

	private static String _getPublicRenderParameterNamespaceURI(
		BeanApp beanApp, String id) {

		Map<String, PublicRenderParameter> publicRenderParameterMap =
			beanApp.getPublicRenderParameterMap();

		PublicRenderParameter publicRenderParameter =
			publicRenderParameterMap.get(id);

		if (publicRenderParameter == null) {
			return XMLConstants.NULL_NS_URI;
		}

		QName qName = publicRenderParameter.getQName();

		if (qName == null) {
			return XMLConstants.NULL_NS_URI;
		}

		String namespaceURI = qName.getNamespaceURI();

		if (namespaceURI == null) {
			return XMLConstants.NULL_NS_URI;
		}

		return namespaceURI;
	}

	private static void _putEnglishText(
		Dictionary<String, Object> dictionary, String key,
		LocaleString[] localeStrings) {

		_putEnglishText(dictionary, key, localeStrings, null);
	}

	private static void _putEnglishText(
		Dictionary<String, Object> dictionary, String key,
		LocaleString[] localeStrings, String defaultValue) {

		for (LocaleString localeString : localeStrings) {
			if (_ENGLISH_EN.equals(localeString.locale())) {
				dictionary.put(key, localeString.value());

				return;
			}
		}

		if (defaultValue != null) {
			dictionary.put(key, defaultValue);
		}
	}

	private static final String _ENGLISH_EN = Locale.ENGLISH.getLanguage();

	private static final Set<String> _liferayPortletModes =
		new HashSet<String>() {
			{
				try {
					for (Field field : LiferayPortletMode.class.getFields()) {
						if (Modifier.isStatic(field.getModifiers()) &&
							(field.getType() == PortletMode.class)) {

							PortletMode portletMode = (PortletMode)field.get(
								null);

							add(portletMode.toString());
						}
					}
				}
				catch (IllegalAccessException iae) {
					throw new ExceptionInInitializerError(iae);
				}
			}
		};

	private final BeanApp _beanApp;
	private final Map<String, String> _liferayPortletConfigurationProperties;
	private final String _portletClassName;
	private final PortletConfiguration _portletConfiguration;
	private final Set<String> _portletModes;

}