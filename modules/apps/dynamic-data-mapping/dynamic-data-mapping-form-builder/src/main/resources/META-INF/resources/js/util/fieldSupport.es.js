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

import {PagesVisitor} from 'dynamic-data-mapping-form-renderer';

export const createField = (props, event) => {
	const {
		defaultLanguageId,
		editingLanguageId,
		fieldNameGenerator,
		spritemap,
	} = props;
	const {fieldType, skipFieldNameGeneration = false} = event;

	let newFieldName;

	if (skipFieldNameGeneration) {
		const {settingsContext} = fieldType;
		const visitor = new PagesVisitor(settingsContext.pages);

		visitor.mapFields(({fieldName, value}) => {
			if (fieldName === 'name') {
				newFieldName = value;
			}
		});
	}
	else {
		newFieldName = fieldNameGenerator(fieldType.label);
	}

	const newField = {
		...fieldType,
		fieldName: newFieldName,
		name: newFieldName,
		settingsContext: {
			...fieldType.settingsContext,
			pages: normalizeSettingsContextPages(
				fieldType.settingsContext.pages,
				editingLanguageId,
				fieldType,
				newFieldName
			),
			type: fieldType.name,
		},
	};

	const {fieldName, name, settingsContext} = newField;

	return {
		...getFieldProperties(
			settingsContext,
			defaultLanguageId,
			editingLanguageId
		),
		fieldName,
		instanceId: generateInstanceId(8),
		name,
		settingsContext,
		spritemap,
		type: fieldType.name,
	};
};

export const formatFieldName = (instanceId, languageId, value) => {
	return `ddm$$${value}$${instanceId}$0$$${languageId}`;
};

export const generateInstanceId = length => {
	let text = '';

	const possible =
		'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

	for (let i = 0; i < length; i++) {
		text += possible.charAt(Math.floor(Math.random() * possible.length));
	}

	return text;
};

export const getField = (pages, fieldName) => {
	const visitor = new PagesVisitor(pages);

	return visitor.findField(field => field.fieldName === fieldName);
};

export const getFieldProperties = (
	{pages},
	defaultLanguageId,
	editingLanguageId
) => {
	const properties = {};
	const visitor = new PagesVisitor(pages);

	visitor.mapFields(
		({fieldName, localizable, localizedValue = {}, type, value}) => {
			if (localizable && localizedValue[editingLanguageId]) {
				properties[fieldName] = localizedValue[editingLanguageId];
			}
			else if (localizable && localizedValue[defaultLanguageId]) {
				properties[fieldName] = localizedValue[defaultLanguageId];
			}
			else if (type == 'options') {
				if (!value[editingLanguageId] && value[defaultLanguageId]) {
					properties[fieldName] = value[defaultLanguageId];
				}
				else {
					properties[fieldName] = value[editingLanguageId];
				}
			}
			else {
				properties[fieldName] = value;
			}
		}
	);

	return properties;
};

export const getParentField = (pages, fieldName) => {
	let parentField = null;
	const visitor = new PagesVisitor(pages);

	visitor.visitFields(field => {
		const nestedFieldsVisitor = new PagesVisitor(field.nestedFields || []);

		if (nestedFieldsVisitor.containsField(fieldName)) {
			parentField = field;

			return true;
		}

		return false;
	});

	return parentField;
};

export const getParentFieldSet = (pages, fieldName) => {
	let parentField = getParentField(pages, fieldName);

	while (parentField) {
		if (isFieldSet(parentField)) {
			return parentField;
		}

		parentField = getParentField(pages, parentField.fieldName);
	}

	return null;
};

export const isFieldSet = field =>
	field.type === 'section' && field.dataDefinitionId;

export const isFieldSetChild = (pages, fieldName) => {
	return !!getParentFieldSet(pages, fieldName);
};

export const localizeField = (field, defaultLanguageId, editingLanguageId) => {
	let value = field.value;

	if (field.dataType === 'json' && typeof value === 'object') {
		value = JSON.stringify(value);
	}

	if (field.localizable && field.localizedValue) {
		let localizedValue = field.localizedValue[editingLanguageId];

		if (localizedValue === undefined) {
			localizedValue = field.localizedValue[defaultLanguageId];
		}

		if (localizedValue !== undefined) {
			value = localizedValue;
		}
	}
	else if (
		field.dataType === 'ddm-options' &&
		value[editingLanguageId] === undefined
	) {
		value = {
			...value,
			[editingLanguageId]: value[defaultLanguageId],
		};
	}

	return {
		...field,
		defaultLanguageId,
		editingLanguageId,
		localizedValue: {
			...(field.localizedValue || {}),
			[editingLanguageId]: value,
		},
		value,
	};
};

export const normalizeSettingsContextPages = (
	pages,
	editingLanguageId,
	fieldType,
	generatedFieldName
) => {
	const visitor = new PagesVisitor(pages);

	return visitor.mapFields(field => {
		const {fieldName} = field;

		if (fieldName === 'name') {
			field = {
				...field,
				value: generatedFieldName,
			};
		}
		else if (fieldName === 'label') {
			field = {
				...field,
				localizedValue: {
					...field.localizedValue,
					[editingLanguageId]: fieldType.label,
				},
				type: 'text',
				value: fieldType.label,
			};
		}
		else if (fieldName === 'type') {
			field = {
				...field,
				value: fieldType.name,
			};
		}
		else if (fieldName === 'validation') {
			field = {
				...field,
				validation: {
					...field.validation,
					fieldName: generatedFieldName,
				},
			};
		}

		return {
			...field,
		};
	});
};
