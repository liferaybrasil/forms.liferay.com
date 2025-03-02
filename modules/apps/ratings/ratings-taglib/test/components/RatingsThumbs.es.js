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

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';
import {act} from 'react-dom/test-utils';

import RatingsThumbs from '../../src/main/resources/META-INF/resources/js/components/RatingsThumbs.es';

const formDataToObj = formData =>
	Array.from(formData.entries()).reduce((accumulator, [key, value]) => {
		accumulator[key] = value;

		return accumulator;
	}, {});

themeDisplay.getPlid = themeDisplay.getPlid || jest.fn(() => 'plid');

const defaultProps = {
	className: 'com.liferay.model.RateableEntry',
	classPK: 'classPK',
	enabled: true,
	signedIn: true,
	url: 'http://url',
};

const renderComponent = (props = defaultProps) =>
	render(<RatingsThumbs {...props} />);

describe('RatingsThumbs', () => {
	afterEach(cleanup);

	describe('when rendered with the default props', () => {
		it('is enabled and has default votes', () => {
			const {getAllByRole} = renderComponent();

			const [thumbUpButton, thumbDownButton] = getAllByRole('button');

			expect(thumbUpButton.value).toBe('0');
			expect(thumbDownButton.value).toBe('0');
			expect(thumbUpButton.disabled).toBe(false);
			expect(thumbDownButton.disabled).toBe(false);
		});
	});

	describe('when rendered with enabled = false', () => {
		it('is disabled', () => {
			const {getAllByRole} = renderComponent({
				...defaultProps,
				enabled: false,
			});

			const [thumbUpButton, thumbDownButton] = getAllByRole('button');

			expect(thumbUpButton.disabled).toBe(true);
			expect(thumbDownButton.disabled).toBe(true);
		});
	});

	describe('when there is no server response', () => {
		beforeEach(() => {
			fetch.mockResponse(JSON.stringify({}));
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user votes up', () => {
			let getAllByRole;

			beforeEach(async () => {
				getAllByRole = renderComponent({
					...defaultProps,
					initialNegativeVotes: 10,
					initialPositiveVotes: 26,
				}).getAllByRole;

				const [thumbUpButton] = getAllByRole('button');

				await act(async () => {
					fireEvent.click(thumbUpButton);
				});
			});

			it('increases the up counter', async () => {
				const [thumbUpButton] = getAllByRole('button');

				expect(thumbUpButton.value).toBe('27');
			});

			it('keeps the down counter', async () => {
				const thumbDownButton = getAllByRole('button')[1];

				expect(thumbDownButton.value).toBe('10');
			});

			describe('and the user votes down', () => {
				beforeEach(async () => {
					const thumbDownButton = getAllByRole('button')[1];

					await act(async () => {
						fireEvent.click(thumbDownButton);
					});
				});

				it('decreases the up counter', async () => {
					const [thumbUpButton] = getAllByRole('button');

					expect(thumbUpButton.value).toBe('26');
				});

				it('increases the down counter', async () => {
					const thumbDownButton = getAllByRole('button')[1];

					expect(thumbDownButton.value).toBe('11');
				});
			});

			describe('and the user votes up again', () => {
				beforeEach(async () => {
					const [thumbUpButton] = getAllByRole('button');

					await act(async () => {
						fireEvent.click(thumbUpButton);
					});
				});

				it('decreases the up counter', async () => {
					const [thumbUpButton] = getAllByRole('button');

					expect(thumbUpButton.value).toBe('26');
				});

				it('keeps the down counter', async () => {
					const thumbDownButton = getAllByRole('button')[1];

					expect(thumbDownButton.value).toBe('10');
				});
			});
		});

		describe('and the user votes down', () => {
			let getAllByRole;

			beforeEach(async () => {
				getAllByRole = renderComponent({
					...defaultProps,
					initialNegativeVotes: 10,
					initialPositiveVotes: 26,
				}).getAllByRole;

				const thumbDownButton = getAllByRole('button')[1];

				await act(async () => {
					fireEvent.click(thumbDownButton);
				});
			});

			it('keeps the up counter', async () => {
				const [thumbUpButton] = getAllByRole('button');

				expect(thumbUpButton.value).toBe('26');
			});

			it('increases the down counter', async () => {
				const thumbDownButton = getAllByRole('button')[1];

				expect(thumbDownButton.value).toBe('11');
			});

			describe('and the user votes up', () => {
				beforeEach(async () => {
					const [thumbUpButton] = getAllByRole('button');

					await act(async () => {
						fireEvent.click(thumbUpButton);
					});
				});

				it('increases the up counter', async () => {
					const [thumbUpButton] = getAllByRole('button');

					expect(thumbUpButton.value).toBe('27');
				});

				it('decreases the down counter', async () => {
					const thumbDownButton = getAllByRole('button')[1];

					expect(thumbDownButton.value).toBe('10');
				});
			});

			describe('and the user votes down again', () => {
				beforeEach(async () => {
					const thumbDownButton = getAllByRole('button')[1];

					await act(async () => {
						fireEvent.click(thumbDownButton);
					});
				});

				it('keeps the up counter', async () => {
					const [thumbUpButton] = getAllByRole('button');

					expect(thumbUpButton.value).toBe('26');
				});

				it('decreases the down counter', async () => {
					const thumbDownButton = getAllByRole('button')[1];

					expect(thumbDownButton.value).toBe('10');
				});
			});
		});
	});

	describe('when there is a valid server response', () => {
		beforeEach(() => {
			fetch.mockResponseOnce(
				JSON.stringify({totalEntries: 59 + 27, totalScore: 59})
			);
		});

		afterEach(() => {
			fetch.resetMocks();
		});

		describe('and the user votes up', () => {
			let getAllByRole;

			beforeEach(async () => {
				getAllByRole = renderComponent().getAllByRole;

				const [thumbUpButton] = getAllByRole('button');

				await act(async () => {
					fireEvent.click(thumbUpButton);
				});
			});

			it('sends a POST request to the server', async () => {
				const [url, {body}] = fetch.mock.calls[0];
				const objFormData = formDataToObj(body);

				expect(url).toBe(defaultProps.url);
				expect(objFormData.className).toBe(defaultProps.className);
				expect(objFormData.score).toBe('1');
			});

			it('updates the counters with the ones from the server', async () => {
				const [thumbUpButton, thumbDownButton] = getAllByRole('button');

				expect(thumbUpButton.value).toBe('59');
				expect(thumbDownButton.value).toBe('27');
			});
		});
	});
});
