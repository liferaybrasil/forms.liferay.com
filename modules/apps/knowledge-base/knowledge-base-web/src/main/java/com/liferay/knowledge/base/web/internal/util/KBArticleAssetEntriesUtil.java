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

package com.liferay.knowledge.base.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetEntryServiceUtil;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.asset.kernel.service.persistence.AssetEntryQuery;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Peter Shin
 */
public class KBArticleAssetEntriesUtil {

	public static List<AssetEntry> getAssetEntries(
			long[] groupIds, long[] classNameIds, long[] assetTagIds,
			long resourcePrimKey, int start, int end, String orderByColumn)
		throws PortalException {

		AssetEntryQuery assetEntryQuery = new AssetEntryQuery();

		assetEntryQuery.setAnyTagIds(assetTagIds);
		assetEntryQuery.setClassNameIds(classNameIds);
		assetEntryQuery.setEnd(end + 1);
		assetEntryQuery.setGroupIds(groupIds);
		assetEntryQuery.setOrderByCol1(orderByColumn);
		assetEntryQuery.setStart(start);

		List<AssetEntry> assetEntries = ListUtil.copy(
			AssetEntryServiceUtil.getEntries(assetEntryQuery));

		AssetEntry assetEntry = null;

		for (AssetEntry curAssetEntry : assetEntries) {
			if (curAssetEntry.getClassPK() == resourcePrimKey) {
				assetEntry = curAssetEntry;
			}
		}

		assetEntries.remove(assetEntry);

		return ListUtil.subList(assetEntries, 0, 10);
	}

	public static long[] getAssetTagIds(long[] groupIds, KBArticle kbArticle)
		throws PortalException {

		List<AssetTag> assetTags = AssetTagServiceUtil.getTags(
			KBArticle.class.getName(), kbArticle.getClassPK());

		long[] tagIds = AssetTagLocalServiceUtil.getTagIds(
			groupIds, StringUtil.split(ListUtil.toString(assetTags, "name")));

		Set<Long> filteredTagIds = new LinkedHashSet<>();

		for (long tagId : tagIds) {
			try {
				AssetTagServiceUtil.getTag(tagId);
			}
			catch (PrincipalException principalException) {

				// LPS-52675

				if (_log.isDebugEnabled()) {
					_log.debug(principalException, principalException);
				}

				continue;
			}

			filteredTagIds.add(tagId);
		}

		return StringUtil.split(StringUtil.merge(filteredTagIds), 0L);
	}

	public static long[] getGroupIds(Group companyGroup, KBArticle kbArticle) {
		return new long[] {kbArticle.getGroupId(), companyGroup.getGroupId()};
	}

	public static String getURL(
			HttpServletRequest httpServletRequest, ThemeDisplay themeDisplay,
			AssetRendererFactory assetRendererFactory,
			AssetRenderer assetRenderer)
		throws Exception {

		long classPK = assetRenderer.getClassPK();

		String className = assetRendererFactory.getClassName();

		String portletId = PortletProviderUtil.getPortletId(
			className, PortletProvider.Action.VIEW);

		PortletURL portletURL = null;

		if (className.equals(BlogsEntry.class.getName())) {
			portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);

			portletURL.setParameter(
				"mvcRenderCommandName", "/blogs/view_entry");
			portletURL.setParameter("entryId", String.valueOf(classPK));
		}
		else if (className.equals(JournalArticle.class.getName())) {
			JournalArticle journalArticle =
				JournalArticleLocalServiceUtil.getLatestArticle(classPK);

			portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);

			portletURL.setParameter("struts_action", "/journal_content/view");
			portletURL.setParameter(
				"groupId", String.valueOf(journalArticle.getGroupId()));
			portletURL.setParameter("articleId", journalArticle.getArticleId());
		}
		else if (className.equals(KBArticle.class.getName())) {
			portletURL = PortletURLFactoryUtil.create(
				httpServletRequest,
				KBPortletKeys.KNOWLEDGE_BASE_ARTICLE_DEFAULT_INSTANCE,
				PortletRequest.RENDER_PHASE);

			portletURL.setParameter("mvcPath", "/article/view_article.jsp");
			portletURL.setParameter("resourcePrimKey", String.valueOf(classPK));
		}
		else if (className.equals(MBMessage.class.getName())) {
			portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);

			portletURL.setParameter(
				"struts_action", "/message_boards/view_message");
			portletURL.setParameter("messageId", String.valueOf(classPK));
		}
		else if (className.equals(WikiPage.class.getName())) {
			WikiPage wikiPage = WikiPageLocalServiceUtil.getPage(classPK);

			portletURL = PortletURLFactoryUtil.create(
				httpServletRequest, portletId, PortletRequest.RENDER_PHASE);

			portletURL.setParameter("struts_action", "/wiki/view");
			portletURL.setParameter(
				"nodeId", String.valueOf(wikiPage.getNodeId()));
			portletURL.setParameter("title", wikiPage.getTitle());
		}

		String currentURL = PortalUtil.getCurrentURL(httpServletRequest);

		if (portletURL == null) {
			return currentURL;
		}

		portletURL.setWindowState(WindowState.MAXIMIZED);
		portletURL.setPortletMode(PortletMode.VIEW);

		portletURL.setParameter("returnToFullPageURL", currentURL);

		return portletURL.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		KBArticleAssetEntriesUtil.class);

}