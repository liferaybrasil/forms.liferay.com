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

package com.liferay.document.library.app.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.document.library.kernel.exception.DuplicateFileEntryException;
import com.liferay.document.library.kernel.exception.FileExtensionException;
import com.liferay.document.library.kernel.exception.FileNameException;
import com.liferay.document.library.kernel.exception.FileSizeException;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.sync.constants.DLSyncConstants;
import com.liferay.document.library.test.util.BaseDLAppTestCase;
import com.liferay.document.library.workflow.WorkflowHandlerInvocationCounter;
import com.liferay.petra.lang.SafeClosable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.proxy.ProxyModeThreadLocal;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceTestConstants;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.permission.DoAsUserThread;
import com.liferay.portal.test.rule.ExpectedLog;
import com.liferay.portal.test.rule.ExpectedLogs;
import com.liferay.portal.test.rule.ExpectedType;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import java.io.File;
import java.io.InputStream;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.util.JDBCExceptionReporter;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alexander Chow
 */
@RunWith(Arquillian.class)
public class DLAppServiceWhenAddingAFileEntryTest extends BaseDLAppTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAssetEntryShouldHavePublishDate() throws Exception {
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		Assert.assertEquals(
			assetEntry.getCreateDate(), assetEntry.getPublishDate());
	}

	@Test
	public void testAssetTagsShouldBeOrdered() throws Exception {
		String fileName = RandomTestUtil.randomString();

		String[] assetTagNames = {"hello", "world"};

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName, fileName,
			assetTagNames);

		AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
			DLFileEntryConstants.getClassName(), fileEntry.getFileEntryId());

		AssertUtils.assertEqualsSorted(assetTagNames, assetEntry.getTagNames());
	}

	@Test
	public void testShouldCallWorkflowHandler() throws Exception {
		try (WorkflowHandlerInvocationCounter<DLFileEntry>
				workflowHandlerInvocationCounter =
					new WorkflowHandlerInvocationCounter<>(
						DLFileEntryConstants.getClassName())) {

			DLAppServiceTestUtil.addFileEntry(
				group.getGroupId(), parentFolder.getFolderId());

			Assert.assertEquals(
				1,
				workflowHandlerInvocationCounter.getCount(
					"updateStatus", int.class, Map.class));
		}
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameAndExtensionInFolder1()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(),
			DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.STRIPPED_FILE_NAME, null);
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(),
			DLAppServiceTestUtil.FILE_NAME, DLAppServiceTestUtil.FILE_NAME,
			null);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameAndExtensionInFolder2()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(),
			DLAppServiceTestUtil.FILE_NAME, DLAppServiceTestUtil.FILE_NAME,
			null);
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(),
			DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.STRIPPED_FILE_NAME, null);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameAndExtensionInFolder3()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(),
			DLAppServiceTestUtil.FILE_NAME,
			DLAppServiceTestUtil.STRIPPED_FILE_NAME, null);
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(),
			DLAppServiceTestUtil.STRIPPED_FILE_NAME,
			DLAppServiceTestUtil.FILE_NAME, null);
	}

	@Test(expected = DuplicateFileEntryException.class)
	public void testShouldFailIfDuplicateNameInFolder() throws Exception {
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());
	}

	@Test(expected = FileSizeException.class)
	public void testShouldFailIfSizeLimitExceeded() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				DLAppServiceTestUtil.getConfigurationTemporarySwapper(
					"fileMaxSize", 1L)) {

			String fileName = RandomTestUtil.randomString();

			DLAppServiceTestUtil.addFileEntry(
				group.getGroupId(), parentFolder.getFolderId(), fileName);
		}
	}

	@Test(expected = FileNameException.class)
	public void testShouldFailIfSourceFileNameContainsBlacklistedChar()
		throws Exception {

		int i =
			RandomTestUtil.randomInt() % PropsValues.DL_CHAR_BLACKLIST.length;

		String blackListedChar = PropsValues.DL_CHAR_BLACKLIST[i];

		String sourceFileName =
			RandomTestUtil.randomString() + blackListedChar +
				RandomTestUtil.randomString();

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), sourceFileName);
	}

	@Test(expected = FileNameException.class)
	public void testShouldFailIfSourceFileNameEndsWithBlacklistedChar()
		throws Exception {

		int i =
			RandomTestUtil.randomInt() %
				PropsValues.DL_CHAR_LAST_BLACKLIST.length;

		String blackListedChar = PropsValues.DL_CHAR_LAST_BLACKLIST[i];

		String sourceFileName = RandomTestUtil.randomString() + blackListedChar;

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), sourceFileName);
	}

	@Test(expected = FileExtensionException.class)
	public void testShouldFailIfSourceFileNameExtensionNotSupported()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				DLAppServiceTestUtil.getConfigurationTemporarySwapper(
					"fileExtensions", new String[0])) {

			String sourceFileName = "file.jpg";

			DLAppServiceTestUtil.addFileEntry(
				group.getGroupId(), parentFolder.getFolderId(), sourceFileName);
		}
	}

	@Test(expected = FileNameException.class)
	public void testShouldFailIfSourceFileNameIsBlacklisted() throws Exception {
		int i =
			RandomTestUtil.randomInt() % PropsValues.DL_NAME_BLACKLIST.length;

		String blackListedName = PropsValues.DL_NAME_BLACKLIST[i];

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), blackListedName);
	}

	@Test
	public void testShouldFireSyncEvent() throws Exception {
		AtomicInteger counter =
			DLAppServiceTestUtil.registerDLSyncEventProcessorMessageListener(
				DLSyncConstants.EVENT_ADD);

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());

		Assert.assertEquals(1, counter.get());
	}

	@Test
	public void testShouldHaveDefaultVersion() throws Exception {
		String fileName = RandomTestUtil.randomString();

		FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName);

		Assert.assertEquals(
			"Version label incorrect after add", "1.0", fileEntry.getVersion());
	}

	@Test
	public void testShouldInferValidMimeType() throws Exception {
		String fileName = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.APPLICATION_OCTET_STREAM, fileName, StringPool.BLANK,
			StringPool.BLANK, CONTENT.getBytes(), serviceContext);

		Assert.assertEquals(ContentTypes.TEXT_PLAIN, fileEntry.getMimeType());
	}

	@Test
	public void testShouldSucceedIfDuplicateNameInOtherFolder()
		throws Exception {

		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId());
		DLAppServiceTestUtil.addFileEntry(
			group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID);
	}

	@ExpectedLogs(
		expectedLogs = {
			@ExpectedLog(
				expectedLog = "Deadlock found when trying to get lock; try restarting transaction",
				expectedType = ExpectedType.EXACT
			),
			@ExpectedLog(
				expectedLog = "Duplicate entry ",
				expectedType = ExpectedType.PREFIX
			)
		},
		level = "ERROR", loggerClass = JDBCExceptionReporter.class
	)
	@Ignore
	@Test
	public void testShouldSucceedWithConcurrentAccess() throws Exception {
		_users = new User[ServiceTestConstants.THREAD_COUNT];

		for (int i = 0; i < ServiceTestConstants.THREAD_COUNT; i++) {
			User user = UserTestUtil.addUser(
				"DLAppServiceTest" + (i + 1), group.getGroupId());

			_users[i] = user;
		}

		DoAsUserThread[] doAsUserThreads = new DoAsUserThread[_users.length];

		_fileEntryIds = new long[_users.length];

		int successCount = 0;

		for (int i = 0; i < doAsUserThreads.length; i++) {
			doAsUserThreads[i] = new AddFileEntryThread(
				_users[i].getUserId(), i);
		}

		successCount = DLAppServiceTestUtil.runUserThreads(doAsUserThreads);

		Assert.assertEquals(
			StringBundler.concat(
				"Only ", successCount, " out of ", _users.length,
				" threads added successfully"),
			_users.length, successCount);

		for (int i = 0; i < doAsUserThreads.length; i++) {
			doAsUserThreads[i] = new GetFileEntryThread(
				_users[i].getUserId(), i);
		}

		successCount = DLAppServiceTestUtil.runUserThreads(doAsUserThreads);

		Assert.assertEquals(
			StringBundler.concat(
				"Only ", successCount, " out of ", _users.length,
				" threads retrieved successfully"),
			_users.length, successCount);
	}

	@Test
	public void testShouldSucceedWithNullBytes() throws Exception {
		String fileName = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLAppServiceUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, StringPool.BLANK,
			StringPool.BLANK, (byte[])null, serviceContext);
	}

	@Test
	public void testShouldSucceedWithNullFile() throws Exception {
		String fileName = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLAppServiceUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, StringPool.BLANK,
			StringPool.BLANK, (File)null, serviceContext);
	}

	@Test
	public void testShouldSucceedWithNullInputStream() throws Exception {
		String fileName = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(group.getGroupId());

		DLAppServiceUtil.addFileEntry(
			group.getGroupId(), parentFolder.getFolderId(), fileName,
			ContentTypes.TEXT_PLAIN, fileName, StringPool.BLANK,
			StringPool.BLANK, null, 0, serviceContext);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DLAppServiceWhenAddingAFileEntryTest.class);

	private long[] _fileEntryIds;

	@DeleteAfterTestRun
	private User[] _users;

	private class AddFileEntryThread extends DoAsUserThread {

		public AddFileEntryThread(long userId, int index) {
			super(userId);

			_index = index;
		}

		@Override
		public boolean isSuccess() {
			return _success;
		}

		@Override
		protected void doRun() throws Exception {
			try (SafeClosable safeClosable =
					ProxyModeThreadLocal.setWithSafeClosable(true)) {

				FileEntry fileEntry = DLAppServiceTestUtil.addFileEntry(
					group.getGroupId(), parentFolder.getFolderId(),
					"Test-" + _index + ".txt");

				_fileEntryIds[_index] = fileEntry.getFileEntryId();

				if (_log.isDebugEnabled()) {
					_log.debug("Added file " + _index);
				}

				_success = true;
			}
			catch (Exception exception) {
				_log.error("Unable to add file " + _index, exception);
			}
		}

		private int _index;
		private boolean _success;

	}

	private class GetFileEntryThread extends DoAsUserThread {

		public GetFileEntryThread(long userId, int index) {
			super(userId);

			_index = index;
		}

		@Override
		public boolean isSuccess() {
			return _success;
		}

		@Override
		protected void doRun() throws Exception {
			try {
				FileEntry fileEntry = DLAppServiceUtil.getFileEntry(
					_fileEntryIds[_index]);

				InputStream is = fileEntry.getContentStream();

				String content = StringUtil.read(is);

				if (CONTENT.equals(content)) {
					if (_log.isDebugEnabled()) {
						_log.debug("Retrieved file " + _index);
					}

					_success = true;
				}
			}
			catch (Exception exception) {
				_log.error("Unable to get file " + _index, exception);
			}
		}

		private int _index;
		private boolean _success;

	}

}