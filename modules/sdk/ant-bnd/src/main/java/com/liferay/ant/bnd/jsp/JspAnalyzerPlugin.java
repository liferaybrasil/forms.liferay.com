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

package com.liferay.ant.bnd.jsp;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;

import aQute.lib.io.IO;
import aQute.lib.strings.Strings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Raymond Augé
 */
public class JspAnalyzerPlugin implements AnalyzerPlugin {

	@Override
	public boolean analyzeJar(Analyzer analyzer) throws Exception {
		addManifestPackageImports(analyzer);

		Parameters parameters = OSGiHeader.parseHeader(
			analyzer.getProperty("-jsp"));

		if (parameters.isEmpty()) {
			return false;
		}

		Instructions instructions = new Instructions(parameters);
		boolean matches = false;

		Jar jar = analyzer.getJar();

		Map<String, Resource> resources = jar.getResources();

		Set<String> keys = new HashSet<String>(resources.keySet());

		Set<String> taglibURIs = new HashSet<>();

		for (String key : keys) {
			for (Instruction instruction : instructions.keySet()) {
				if (instruction.matches(key)) {
					if (instruction.isNegated()) {
						break;
					}

					Resource resource = jar.getResource(key);

					String jsp = IO.collect(
						resource.openInputStream(), "UTF-8");

					addApiUses(analyzer, jsp);
					addTaglibRequirements(analyzer, jsp, taglibURIs);

					matches = true;
				}
			}
		}

		if (matches) {
			addRequiredPackageImports(analyzer, _REQUIRED_PACKAGE_NAMES);
		}

		return false;
	}

	protected void addApiUses(Analyzer analyzer, String originalContent) {
		String content = _removeComments(originalContent);

		int contentX = -1;
		int contentY = content.length();

		while (true) {
			contentX = content.lastIndexOf("<%@", contentY);

			if (contentX == -1) {
				break;
			}

			contentY = contentX;

			int importX = content.indexOf("import=\"", contentY);

			int importY = -1;

			if (importX != -1) {
				importX = importX + "import=\"".length();

				importY = content.indexOf("\"", importX);
			}

			if ((importX != -1) && (importY != -1)) {
				String contentFragment = content.substring(importX, importY);

				String[] packageFragments = contentFragment.split("\\s*,\\s*");

				for (String packageFragment : packageFragments) {
					int index = packageFragment.lastIndexOf('.');

					Matcher matcher = _staticImportPattern.matcher(
						packageFragment);

					if (matcher.matches()) {
						packageFragment = matcher.group("package");

						packageFragment = packageFragment.substring(
							0, packageFragment.length() - 1);

						index = packageFragment.length();
					}

					if (index != -1) {
						Packages packages = analyzer.getReferred();

						String packageName = packageFragment.substring(
							0, index);

						Descriptors.PackageRef packageRef =
							analyzer.getPackageRef(packageName);

						packages.put(packageRef, new Attrs());

						addApiUses(analyzer, packageFragment, packageRef);
					}
				}
			}

			contentY -= 3;
		}
	}

	protected void addApiUses(
		Analyzer analyzer, String content, Descriptors.PackageRef packageRef) {

		for (Jar jar : analyzer.getClasspath()) {
			addJarApiUses(analyzer, content, packageRef, jar);
		}
	}

	protected void addJarApiUses(
		Analyzer analyzer, String content, Descriptors.PackageRef packageRef,
		Jar jar) {

		Map<String, Map<String, Resource>> resourceMaps = jar.getDirectories();

		Map<String, Resource> resourceMap = resourceMaps.get(
			packageRef.getPath());

		if ((resourceMap == null) || resourceMap.isEmpty()) {
			return;
		}

		if (content.endsWith("*")) {
			for (Map.Entry<String, Resource> entry : resourceMap.entrySet()) {
				String key = entry.getKey();

				if (!key.endsWith(".class")) {
					continue;
				}

				addResourceApiUses(analyzer, key, entry.getValue());
			}
		}
		else {
			String fqnToPath = Descriptors.fqnToPath(content);

			if (resourceMap.containsKey(fqnToPath)) {
				Resource resource = resourceMap.get(fqnToPath);

				addResourceApiUses(analyzer, content, resource);
			}
		}
	}

	protected void addManifestPackageImports(Analyzer analyzer) {
		Packages packages = analyzer.getClasspathExports();

		for (Jar jar : analyzer.getClasspath()) {
			try {
				Manifest manifest = jar.getManifest();

				if (manifest == null) {
					continue;
				}

				Domain domain = Domain.domain(manifest);

				Parameters parameters = domain.getExportPackage();

				for (Map.Entry<String, Attrs> entry : parameters.entrySet()) {
					Descriptors.PackageRef packageRef = analyzer.getPackageRef(
						entry.getKey());

					Attrs attrs = packages.get(packageRef);

					if (attrs.isEmpty()) {
						packages.put(packageRef, entry.getValue());
					}
				}
			}
			catch (Exception exception) {
			}
		}
	}

	protected void addRequiredPackageImports(
		Analyzer analyzer, String[] packageNames) {

		Packages packages = analyzer.getReferred();

		for (String packageName : packageNames) {
			Descriptors.PackageRef packageRef = analyzer.getPackageRef(
				packageName);

			Matcher matcher = _packagePattern.matcher(packageRef.getFQN());

			if (matcher.matches() && !packages.containsKey(packageRef)) {
				packages.put(packageRef, new Attrs());
			}
		}
	}

	protected void addResourceApiUses(
		Analyzer analyzer, String fqnToPath, Resource resource) {

		Clazz clazz = null;

		try (InputStream inputStream = resource.openInputStream()) {
			clazz = new Clazz(analyzer, fqnToPath, resource);

			clazz.parseClassFile();
		}
		catch (Throwable e) {
			return;
		}

		Set<Descriptors.PackageRef> packageRefs = clazz.getAPIUses();

		for (Descriptors.PackageRef packageRef : packageRefs) {
			Packages packages = analyzer.getReferred();

			packages.put(packageRef, new Attrs());
		}
	}

	protected void addTaglibRequirement(
		Set<String> taglibRequirements, String uri) {

		Parameters parameters = new Parameters();

		Attrs attrs = new Attrs();

		attrs.put(
			Constants.FILTER_DIRECTIVE,
			"\"(&(osgi.extender=jsp.taglib)(uri=" + uri + "))\"");

		parameters.put("osgi.extender", attrs);

		taglibRequirements.add(parameters.toString());
	}

	protected void addTaglibRequirements(
		Analyzer analyzer, String content, Set<String> taglibURIs) {

		Set<String> taglibRequirements = new TreeSet<>();

		for (String uri : getTaglibURIs(content)) {
			if (taglibURIs.contains(uri)) {
				continue;
			}

			taglibURIs.add(uri);

			// Check to see if the JAR provides this TLD itself which would
			// indicate that it already has access to the required classes

			if (containsTLD(analyzer, analyzer.getJar(), "META-INF", uri) ||
				containsTLD(analyzer, analyzer.getJar(), "WEB-INF/tld", uri) ||
				containsTLDInBundleClassPath(analyzer, "META-INF", uri)) {

				continue;
			}

			if (Arrays.binarySearch(_JSTL_CORE_URIS, uri) < 0) {
				addTaglibRequirement(taglibRequirements, uri);
			}
		}

		if (taglibRequirements.isEmpty()) {
			return;
		}

		String value = analyzer.getProperty(Constants.REQUIRE_CAPABILITY);

		if (value != null) {
			Parameters parameters = OSGiHeader.parseHeader(value);

			for (Map.Entry<String, Attrs> entry : parameters.entrySet()) {
				String key = Processor.removeDuplicateMarker(entry.getKey());

				StringBuilder sb = new StringBuilder(key);

				Attrs attrs = entry.getValue();

				if (attrs != null) {
					sb.append(";");

					attrs.append(sb);
				}

				taglibRequirements.add(sb.toString());
			}
		}

		analyzer.setProperty(
			Constants.REQUIRE_CAPABILITY, Strings.join(taglibRequirements));
	}

	protected boolean containsTLD(
		Analyzer analyzer, Jar jar, String root, String uri) {

		Map<String, Map<String, Resource>> resourceMaps = jar.getDirectories();

		Map<String, Resource> resourceMap = resourceMaps.get(root);

		if ((resourceMap == null) || resourceMap.isEmpty()) {
			Resource resource = jar.getResource(root);

			if ((resource != null) &&
				matchesURI(analyzer, root, resource, uri)) {

				return true;
			}

			return false;
		}

		for (Map.Entry<String, Resource> entry : resourceMap.entrySet()) {
			String path = entry.getKey();
			Resource resource = entry.getValue();

			Matcher matcher = _tldPattern.matcher(path);

			if (matcher.matches() &&
				matchesURI(analyzer, path, resource, uri)) {

				return true;
			}
		}

		return false;
	}

	protected boolean containsTLDInBundleClassPath(
		Analyzer analyzer, String root, String uri) {

		Parameters parameters = new Parameters(
			analyzer.getProperty(Constants.BUNDLE_CLASSPATH));

		if (parameters.isEmpty()) {
			return false;
		}

		Jar jar = analyzer.getJar();

		for (String entry : parameters.keySet()) {
			String entryLowerCase = entry.toLowerCase();

			if (!entryLowerCase.endsWith(".jar") &&
				!entryLowerCase.endsWith(".zip")) {

				continue;
			}

			Resource resource = jar.getResource(entry);

			if (resource == null) {
				continue;
			}

			try (ByteArrayOutputStream byteArrayOutputStream =
					new ByteArrayOutputStream()) {

				resource.write(byteArrayOutputStream);

				try (InputStream inputStream = new ByteArrayInputStream(
						byteArrayOutputStream.toByteArray())) {

					Jar classPathJar = new Jar(entry, inputStream);

					if (containsTLD(analyzer, classPathJar, root, uri)) {
						return true;
					}
				}
			}
			catch (Exception exception) {
			}
		}

		return false;
	}

	protected Set<String> getTaglibURIs(String originalContent) {
		String content = _removeComments(originalContent);

		int contentX = -1;
		int contentY = content.length();

		Set<String> taglibURis = new HashSet<String>();

		while (true) {
			contentX = content.lastIndexOf("<%@", contentY);

			if (contentX == -1) {
				break;
			}

			contentY = contentX;

			int importX = content.indexOf("uri=\"", contentY);

			int importY = -1;

			if (importX != -1) {
				importX = importX + "uri=\"".length();

				importY = content.indexOf("\"", importX);
			}

			if ((importX != -1) && (importY != -1)) {
				String s = content.substring(importX, importY);

				taglibURis.add(s);
			}

			contentY -= 3;
		}

		return taglibURis;
	}

	protected boolean matchesURI(
		Analyzer analyzer, String path, Resource resource, final String uri) {

		try {
			URIFinder uriFinder = new URIFinder(uri);

			SAXParser saxParser = _saxParserFactory.newSAXParser();

			XMLReader xmlReader = saxParser.getXMLReader();

			xmlReader.setContentHandler(uriFinder);
			xmlReader.setFeature(_LOAD_EXTERNAL_DTD, false);
			xmlReader.setEntityResolver(new NullEntityResolver());

			xmlReader.parse(new InputSource(resource.openInputStream()));

			return uriFinder.hasURI();
		}
		catch (Exception exception) {
			analyzer.error(
				"Unexpected exception in processing TLD " + path + ": " +
					exception);
		}

		return false;
	}

	private static String _removeComments(String content) {
		Matcher matcher = _commentPattern.matcher(content);

		return matcher.replaceAll("");
	}

	private static final String[] _JSTL_CORE_URIS = {
		"http://java.sun.com/jsp/jstl/core", "http://java.sun.com/jsp/jstl/fmt",
		"http://java.sun.com/jsp/jstl/functions",
		"http://java.sun.com/jsp/jstl/sql", "http://java.sun.com/jsp/jstl/xml"
	};

	private static final String _LOAD_EXTERNAL_DTD =
		"http://apache.org/xml/features/nonvalidating/load-external-dtd";

	private static final String[] _REQUIRED_PACKAGE_NAMES = {
		"javax.servlet", "javax.servlet.http"
	};

	private static final Pattern _commentPattern = Pattern.compile(
		"<%--[\\s\\S]*?--%>");
	private static final Pattern _packagePattern = Pattern.compile(
		"[_A-Za-z$][_A-Za-z0-9$]*(\\.[_A-Za-z$][_A-Za-z0-9$]*)*");
	private static final Pattern _staticImportPattern = Pattern.compile(
		"\\s*static\\s+((?<package>(\\p{javaJavaIdentifierStart}" +
			"\\p{javaJavaIdentifierPart}*\\.)+)(\\p{javaJavaIdentifierStart}" +
				"\\p{javaJavaIdentifierPart}*\\.)" +
					"(\\*|(\\p{javaJavaIdentifierStart}" +
						"\\p{javaJavaIdentifierPart}*)))\\s*");
	private static final Pattern _tldPattern = Pattern.compile(".*\\.tld");

	private final SAXParserFactory _saxParserFactory =
		SAXParserFactory.newInstance();

	private class NullEntityResolver implements EntityResolver {

		@Override
		public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {

			return new InputSource();
		}

	}

	private class URIFinder extends DefaultHandler {

		public URIFinder(String uri) {
			_uri = uri;
		}

		@Override
		public void characters(char[] chars, int start, int length)
			throws SAXException {

			if (!_inURI) {
				return;
			}

			_hasURI = _uri.equals(_trim(chars, start, length));

			_inURI = false;
		}

		public boolean hasURI() {
			return _hasURI;
		}

		@Override
		public void startElement(
				String uri, String localName, String qName,
				Attributes attributes)
			throws SAXException {

			if (qName.equals("uri")) {
				_inURI = true;
			}
		}

		private String _trim(char[] chars, int start, int length) {
			int end = start + length;

			for (int i = start; i < end; i++) {
				if (Character.isWhitespace(chars[i])) {
					start++;
				}
				else {
					break;
				}
			}

			for (int i = end - 1; i >= start; i--) {
				if (Character.isWhitespace(chars[i])) {
					end--;
				}
				else {
					break;
				}
			}

			return new String(chars, start, end - start);
		}

		private boolean _hasURI;
		private boolean _inURI;
		private final String _uri;

	}

}