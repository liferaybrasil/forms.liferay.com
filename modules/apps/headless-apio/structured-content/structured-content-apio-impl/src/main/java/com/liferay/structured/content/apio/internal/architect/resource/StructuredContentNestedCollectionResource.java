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

package com.liferay.structured.content.apio.internal.architect.resource;

import static com.liferay.portal.apio.idempotent.Idempotent.idempotent;

import com.liferay.aggregate.rating.apio.architect.identifier.AggregateRatingIdentifier;
import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.NestedCollectionResource;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.apio.architect.routes.NestedCollectionRoutes;
import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.model.AssetTagModel;
import com.liferay.asset.kernel.model.DDMFormValuesReader;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.category.apio.architect.identifier.CategoryIdentifier;
import com.liferay.comment.apio.architect.identifier.CommentIdentifier;
import com.liferay.content.space.apio.architect.identifier.ContentSpaceIdentifier;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.kernel.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.kernel.DDMFormValues;
import com.liferay.dynamic.data.mapping.kernel.Value;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.media.object.apio.architect.identifier.MediaObjectIdentifier;
import com.liferay.person.apio.architect.identifier.PersonIdentifier;
import com.liferay.portal.apio.identifier.ClassNameClassPK;
import com.liferay.portal.apio.permission.HasPermission;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.structure.apio.architect.identifier.ContentStructureIdentifier;
import com.liferay.structured.content.apio.architect.identifier.StructuredContentIdentifier;
import com.liferay.structured.content.apio.internal.architect.form.StructuredContentCreatorForm;
import com.liferay.structured.content.apio.internal.architect.form.StructuredContentUpdaterForm;
import com.liferay.structured.content.apio.internal.model.JournalArticleWrapper;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Provides the information necessary to expose a StructuredContent resources
 * through a web API. The resources are mapped from the internal model {@code
 * JournalArticle}.
 *
 * @author Javier Gamarra
 */
@Component(immediate = true)
public class StructuredContentNestedCollectionResource
	implements
		NestedCollectionResource<JournalArticleWrapper, Long,
			StructuredContentIdentifier, Long, ContentSpaceIdentifier> {

	@Override
	public NestedCollectionRoutes<JournalArticleWrapper, Long, Long>
		collectionRoutes(
			NestedCollectionRoutes.Builder<JournalArticleWrapper, Long, Long>
				builder) {

		return builder.addGetter(
			this::_getPageItems, ThemeDisplay.class
		).addCreator(
			this::_addJournalArticle, ThemeDisplay.class,
			_hasPermission.forAddingIn(ContentSpaceIdentifier.class),
			StructuredContentCreatorForm::buildForm
		).build();
	}

	@Override
	public String getName() {
		return "structured-contents";
	}

	@Override
	public ItemRoutes<JournalArticleWrapper, Long> itemRoutes(
		ItemRoutes.Builder<JournalArticleWrapper, Long> builder) {

		return builder.addGetter(
			this::_getJournalArticleWrapper, ThemeDisplay.class
		).addRemover(
			idempotent(this::_deleteJournalArticle), _hasPermission::forDeleting
		).addUpdater(
			this::_updateJournalArticle, ThemeDisplay.class,
			_hasPermission::forUpdating, StructuredContentUpdaterForm::buildForm
		).build();
	}

	@Override
	public Representor<JournalArticleWrapper> representor(
		Representor.Builder<JournalArticleWrapper, Long> builder) {

		return builder.types(
			"StructuredContent"
		).identifier(
			JournalArticle::getId
		).addBidirectionalModel(
			"contentSpace", "structuredContents", ContentSpaceIdentifier.class,
			JournalArticle::getGroupId
		).addDate(
			"dateCreated", JournalArticle::getCreateDate
		).addDate(
			"dateModified", JournalArticle::getModifiedDate
		).addDate(
			"datePublished", JournalArticle::getDisplayDate
		).addDate(
			"lastReviewed", JournalArticle::getReviewDate
		).addNestedList(
			"values", this::_getJournalArticleDDMFormFieldValues,
			fieldValuesBuilder -> fieldValuesBuilder.types(
				"ContentFieldValue"
			).addLinkedModel(
				"file", MediaObjectIdentifier.class, this::_getFileEntryId
			).addLocalizedStringByLocale(
				"value", this::_getLocalizedString
			).addString(
				"name", DDMFormFieldValue::getName
			).build()
		).addLinkedModel(
			"aggregateRating", AggregateRatingIdentifier.class,
			this::_createClassNameClassPK
		).addLinkedModel(
			"creator", PersonIdentifier.class, JournalArticle::getUserId
		).addLinkedModel(
			"contentStructure", ContentStructureIdentifier.class,
			journalArticleWrapper ->
				journalArticleWrapper.getDDMStructure().getStructureId()
		).addLocalizedStringByLocale(
			"renderedContent", this::_getJournalArticleHtml
		).addRelatedCollection(
			"categories", CategoryIdentifier.class
		).addRelatedCollection(
			"comments", CommentIdentifier.class
		).addString(
			"description", JournalArticleWrapper::getDescription
		).addString(
			"content", JournalArticle::getContent
		).addString(
			"title", JournalArticle::getTitle
		).addStringList(
			"keywords", this::_getJournalArticleAssetTags
		).build();
	}

	private JournalArticleWrapper _addJournalArticle(
			long contentSpaceId,
			StructuredContentCreatorForm structuredContentCreatorForm,
			ThemeDisplay themeDisplay)
		throws PortalException {

		Locale locale = themeDisplay.getLocale();

		ServiceContext serviceContext =
			structuredContentCreatorForm.getServiceContext(contentSpaceId);

		JournalArticle journalArticle = _journalArticleService.addArticle(
			contentSpaceId, 0, 0, 0, null, true,
			structuredContentCreatorForm.getTitleMap(locale),
			structuredContentCreatorForm.getDescriptionMap(locale),
			structuredContentCreatorForm.getText(),
			structuredContentCreatorForm.getStructure(),
			structuredContentCreatorForm.getTemplate(), null,
			structuredContentCreatorForm.getDisplayDateMonth(),
			structuredContentCreatorForm.getDisplayDateDay(),
			structuredContentCreatorForm.getDisplayDateYear(),
			structuredContentCreatorForm.getDisplayDateHour(),
			structuredContentCreatorForm.getDisplayDateMinute(), 0, 0, 0, 0, 0,
			true, 0, 0, 0, 0, 0, true, true, null, serviceContext);

		return new JournalArticleWrapper(journalArticle, themeDisplay);
	}

	private ClassNameClassPK _createClassNameClassPK(
		JournalArticle journalArticle) {

		return ClassNameClassPK.create(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());
	}

	private void _deleteJournalArticle(long journalArticleId)
		throws PortalException {

		JournalArticle journalArticle = _journalArticleService.getArticle(
			journalArticleId);

		_journalArticleService.deleteArticle(
			journalArticle.getGroupId(), journalArticle.getArticleId(),
			journalArticle.getArticleResourceUuid(), new ServiceContext());
	}

	private Long _getFileEntryId(DDMFormFieldValue ddmFormFieldValue) {
		Value value = ddmFormFieldValue.getValue();

		String valueString = value.getString(LocaleUtil.getDefault());

		try {
			if (_isJSONObject(valueString)) {
				JSONObject jsonObject = JSONFactoryUtil.createJSONObject(
					valueString);

				String uuid = jsonObject.getString("uuid");
				long groupId = jsonObject.getLong("groupId");

				FileEntry fileEntry =
					_dlAppService.getFileEntryByUuidAndGroupId(uuid, groupId);

				return fileEntry.getFileEntryId();
			}
		}
		catch (PortalException pe) {
			if (_log.isDebugEnabled()) {
				_log.debug(pe, pe);
			}
		}

		return null;
	}

	private List<DDMFormFieldValue> _getFormFieldValues(
		List<DDMFormFieldValue> ddmFormFieldValues) {

		Stream<DDMFormFieldValue> ddmFormFieldValueStream =
			ddmFormFieldValues.stream();

		List<DDMFormFieldValue> nestedDDMFormFieldValues =
			ddmFormFieldValueStream.flatMap(
				ddmFormFieldValue -> _getFormFieldValues(
					ddmFormFieldValue.getNestedDDMFormFieldValues()
				).stream()
			).collect(
				Collectors.toList()
			);

		nestedDDMFormFieldValues.addAll(ddmFormFieldValues);

		return nestedDDMFormFieldValues;
	}

	private List<String> _getJournalArticleAssetTags(
		JournalArticle journalArticle) {

		List<AssetTag> assetTags = _assetTagLocalService.getTags(
			JournalArticle.class.getName(),
			journalArticle.getResourcePrimKey());

		return ListUtil.toList(assetTags, AssetTagModel::getName);
	}

	private List<DDMFormFieldValue> _getJournalArticleDDMFormFieldValues(
		JournalArticleWrapper journalArticleWrapper) {

		return Try.fromFallible(
			() ->
				AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClass(
					JournalArticle.class)
		).map(
			assetRendererFactory -> assetRendererFactory.getAssetRenderer(
				journalArticleWrapper,
				AssetRendererFactory.TYPE_LATEST_APPROVED)
		).map(
			AssetRenderer::getDDMFormValuesReader
		).map(
			DDMFormValuesReader::getDDMFormValues
		).map(
			DDMFormValues::getDDMFormFieldValues
		).map(
			this::_getFormFieldValues
		).orElse(
			null
		);
	}

	private String _getJournalArticleHtml(
		JournalArticleWrapper journalArticleWrapper, Locale locale) {

		try {
			String content = _journalArticleService.getArticleContent(
				journalArticleWrapper.getGroupId(),
				journalArticleWrapper.getArticleId(), locale.getLanguage(),
				null, journalArticleWrapper.getThemeDisplay());

			return content.replaceAll("[\\t\\n]", "");
		}
		catch (PortalException pe) {
			if (_log.isInfoEnabled()) {
				_log.info(pe, pe);
			}
		}

		return null;
	}

	private JournalArticleWrapper _getJournalArticleWrapper(
			long journalArticleId, ThemeDisplay themeDisplay)
		throws PortalException {

		JournalArticle journalArticle = _journalArticleService.getArticle(
			journalArticleId);

		return new JournalArticleWrapper(journalArticle, themeDisplay);
	}

	private String _getLocalizedString(
		DDMFormFieldValue ddmFormFieldValue, Locale locale) {

		return Try.fromFallible(
			ddmFormFieldValue::getValue
		).map(
			value -> value.getString(locale)
		).filter(
			valueString -> !_isJSONObject(valueString)
		).orElse(
			null
		);
	}

	private PageItems<JournalArticleWrapper> _getPageItems(
		Pagination pagination, long contentSpaceId, ThemeDisplay themeDisplay) {

		List<JournalArticleWrapper> journalArticleWrappers = Stream.of(
			_journalArticleService.getArticles(
				contentSpaceId, 0, pagination.getStartPosition(),
				pagination.getEndPosition(), null)
		).flatMap(
			List::stream
		).map(
			journalArticle -> new JournalArticleWrapper(
				journalArticle, themeDisplay)
		).collect(
			Collectors.toList()
		);
		int count = _journalArticleService.getArticlesCount(contentSpaceId, 0);

		return new PageItems<>(journalArticleWrappers, count);
	}

	private boolean _isJSONObject(String json) {
		try {
			if (json.startsWith("{") &&
				(JSONFactoryUtil.createJSONObject(json) != null)) {

				return true;
			}

			return false;
		}
		catch (JSONException jsone) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON", jsone);
			}

			return false;
		}
	}

	private JournalArticleWrapper _updateJournalArticle(
			long journalArticleId,
			StructuredContentUpdaterForm structuredContentUpdaterForm,
			ThemeDisplay themeDisplay)
		throws PortalException {

		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setAddGroupPermissions(true);
		serviceContext.setAddGuestPermissions(true);
		serviceContext.setScopeGroupId(structuredContentUpdaterForm.getGroup());

		JournalArticle journalArticle = _journalArticleService.updateArticle(
			structuredContentUpdaterForm.getUser(),
			structuredContentUpdaterForm.getGroup(), 0,
			String.valueOf(journalArticleId),
			structuredContentUpdaterForm.getVersion(),
			structuredContentUpdaterForm.getTitleMap(),
			structuredContentUpdaterForm.getDescriptionMap(),
			structuredContentUpdaterForm.getText(), null, serviceContext);

		return new JournalArticleWrapper(journalArticle, themeDisplay);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StructuredContentNestedCollectionResource.class);

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference(
		target = "(model.class.name=com.liferay.journal.model.JournalArticle)"
	)
	private HasPermission<Long> _hasPermission;

	@Reference
	private JournalArticleService _journalArticleService;

}