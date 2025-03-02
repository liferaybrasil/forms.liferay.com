<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
LayoutRevision layoutRevision = (LayoutRevision)request.getAttribute(WebKeys.LAYOUT_REVISION);

if ((layoutRevision == null) && (layout != null)) {
	layoutRevision = LayoutStagingUtil.getLayoutRevision(layout);
}

LayoutSetBranch layoutSetBranch = (LayoutSetBranch)request.getAttribute(StagingProcessesWebKeys.LAYOUT_SET_BRANCH);

if (layoutSetBranch == null) {
	layoutSetBranch = LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(layoutRevision.getLayoutSetBranchId());
}

boolean workflowEnabled = WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(themeDisplay.getCompanyId(), scopeGroupId, LayoutRevision.class.getName());

boolean hasWorkflowTask = false;

if (workflowEnabled) {
	hasWorkflowTask = StagingUtil.hasWorkflowTask(user.getUserId(), layoutRevision);
}

String taglibHelpMessage = null;

String layoutSetBranchName = HtmlUtil.escape(layoutSetBranchDisplayContext.getLayoutSetBranchDisplayName(layoutSetBranch));

if (layoutRevision.isHead()) {
	taglibHelpMessage = LanguageUtil.format(request, "this-version-will-be-published-when-x-is-published-to-live", layoutSetBranchName, false);
}
else if (hasWorkflowTask) {
	taglibHelpMessage = "you-are-currently-reviewing-this-page.-you-can-make-changes-and-send-them-to-the-next-step-in-the-workflow-when-ready";
}
else {
	taglibHelpMessage = "a-new-version-is-created-automatically-if-this-page-is-modified";
}
%>

<ul class="control-menu-nav staging-layout-revision-details-list">
	<c:if test="<%= !hasWorkflowTask %>">
		<c:if test="<%= !layoutRevision.isHead() && LayoutPermissionUtil.contains(permissionChecker, layoutRevision.getPlid(), ActionKeys.UPDATE) %>">
			<li class="control-menu-nav-item">

				<%
				List<LayoutRevision> pendingLayoutRevisions = LayoutRevisionLocalServiceUtil.getLayoutRevisions(layoutRevision.getLayoutSetBranchId(), layoutRevision.getPlid(), WorkflowConstants.STATUS_PENDING);
				%>

				<portlet:actionURL name="updateLayoutRevision" var="publishURL">
					<portlet:param name="redirect" value="<%= PortalUtil.getLayoutFullURL(themeDisplay) %>" />
					<portlet:param name="layoutRevisionId" value="<%= String.valueOf(layoutRevision.getLayoutRevisionId()) %>" />
					<portlet:param name="major" value="<%= Boolean.TRUE.toString() %>" />
					<portlet:param name="workflowAction" value="<%= String.valueOf(layoutRevision.isIncomplete() ? WorkflowConstants.ACTION_SAVE_DRAFT : WorkflowConstants.ACTION_PUBLISH) %>" />
				</portlet:actionURL>

				<c:choose>
					<c:when test="<%= !workflowEnabled && !layoutRevision.isIncomplete() %>">
						<span class="staging-bar-control-toggle">
							<aui:input id="readyToggle" label="<%= StringPool.BLANK %>" labelOff="ready-for-publication" labelOn="ready-for-publication" name="readyToggle" onChange='<%= liferayPortletResponse.getNamespace() + "submitLayoutRevision('" + publishURL + "')" %>' type="toggle-switch" value="<%= false %>" />
						</span>
					</c:when>
					<c:when test="<%= !workflowEnabled || pendingLayoutRevisions.isEmpty() %>">

						<%
						String label = null;

						if (layoutRevision.isIncomplete()) {
							label = LanguageUtil.format(request, "enable-in-x", layoutSetBranchName, false);
						}
						else if (workflowEnabled) {
							label = "submit-for-publication";
						}
						%>

						<div class="btn-group-item">
							<a class="btn btn-secondary btn-sm" href="javascript:Liferay.fire('<%= liferayPortletResponse.getNamespace() %>submit', {incomplete: <%= layoutRevision.isIncomplete() %>, publishURL: '<%= publishURL %>', currentURL: '<%= currentURL %>'}); void(0);" id="submitLink">
								<liferay-ui:message key="<%= label %>" />
							</a>
						</div>
					</c:when>
				</c:choose>
			</li>
		</c:if>
	</c:if>

	<c:if test="<%= !layoutRevision.isIncomplete() %>">
		<li class="control-menu-nav-item">
			<c:if test="<%= layoutRevision.isHead() %>">
				<span class="staging-bar-control-toggle">
					<aui:input disabled="<%= true %>" id="readyToggle" label="<%= StringPool.BLANK %>" labelOn="ready-for-publication" name="readyToggle" type="toggle-switch" value="<%= true %>" />
				</span>
			</c:if>

			<c:if test="<%= hasWorkflowTask %>">

				<%
				PortletURL portletURL = PortalUtil.getControlPanelPortletURL(request, PortletKeys.MY_WORKFLOW_TASK, PortletRequest.RENDER_PHASE);

				portletURL.setParameter("mvcPath", "/edit_workflow_task.jsp");

				WorkflowTask workflowTask = StagingUtil.getWorkflowTask(user.getUserId(), layoutRevision);

				portletURL.setParameter("workflowTaskId", String.valueOf(workflowTask.getWorkflowTaskId()));

				portletURL.setPortletMode(PortletMode.VIEW);
				portletURL.setWindowState(LiferayWindowState.POP_UP);

				String layoutURL = PortalUtil.getLayoutFriendlyURL(layout, themeDisplay);

				layoutURL = HttpUtil.addParameter(layoutURL, "layoutSetBranchId", layoutRevision.getLayoutSetBranchId());
				layoutURL = HttpUtil.addParameter(layoutURL, "layoutRevisionId", layoutRevision.getLayoutRevisionId());

				portletURL.setParameter("closeRedirect", layoutURL);
				%>

				<liferay-ui:icon
					cssClass="submit-link"
					icon="workflow"
					id="reviewTaskIcon"
					message="workflow"
					method="get"
					url="<%= portletURL.toString() %>"
					useDialog="<%= true %>"
				/>
			</c:if>
		</li>
	</c:if>

	<%
	request.setAttribute(StagingProcessesWebKeys.BRANCHING_ENABLED, Boolean.TRUE.toString());
	request.setAttribute("view_layout_revision_details.jsp-hasWorkflowTask", String.valueOf(hasWorkflowTask));
	request.setAttribute("view_layout_revision_details.jsp-layoutRevision", layoutRevision);
	%>

	<liferay-staging:menu
		cssClass="branching-enabled col-md-4"
		layoutSetBranchId="<%= layoutRevision.getLayoutSetBranchId() %>"
		onlyActions="<%= true %>"
	/>

	<li class="control-menu-nav-item">
		<div class="d-none d-sm-block dropdown">
			<a class="dropdown-toggle taglib-icon" data-toggle="liferay-dropdown" href="javascript:;">
				<aui:icon cssClass="icon-monospaced" image="ellipsis-v" markupView="lexicon" />

				<span class="sr-only">
					<liferay-ui:message key="options" />
				</span>
			</a>

			<ul class="dropdown-menu dropdown-menu-right" role="menu">
				<li>
					<a class="dropdown-item" href="javascript:;" id="manageLayoutSetRevisions" onclick="<%= renderResponse.getNamespace() + "openSitePagesVariationsDialog();" %>">
						<liferay-ui:message key="site-pages-variation" />
					</a>
				</li>

				<c:if test="<%= !layoutRevision.isIncomplete() %>">
					<li>
						<a class="dropdown-item" href="javascript:;" id="manageLayoutRevisions" onclick="<%= renderResponse.getNamespace() + "openPageVariationsDialog();" %>">
							<liferay-ui:message key="page-variations" />
						</a>
					</li>
					<li>
						<a class="dropdown-item" href="javascript:Liferay.fire('<%= liferayPortletResponse.getNamespace() %>viewHistory', {layoutRevisionId: '<%= layoutRevision.getLayoutRevisionId() %>', layoutSetBranchId: '<%= layoutRevision.getLayoutSetBranchId() %>'}); void(0);" id="viewHistoryLink">
							<liferay-ui:message key="history" />
						</a>
					</li>
				</c:if>

				<c:if test="<%= !hasWorkflowTask %>">
					<c:if test="<%= !layoutRevision.isMajor() && (layoutRevision.getParentLayoutRevisionId() != LayoutRevisionConstants.DEFAULT_PARENT_LAYOUT_REVISION_ID) %>">
						<li>
							<a class="dropdown-item" href="javascript:Liferay.fire('<%= liferayPortletResponse.getNamespace() %>undo', {layoutRevisionId: '<%= layoutRevision.getLayoutRevisionId() %>', layoutSetBranchId: '<%= layoutRevision.getLayoutSetBranchId() %>'}); void(0);" id="undoLink">
								<liferay-ui:message key="undo" />
							</a>
						</li>
					</c:if>

					<c:if test="<%= layoutRevision.hasChildren() %>">

						<%
						List<LayoutRevision> childLayoutRevisions = layoutRevision.getChildren();

						LayoutRevision firstChildLayoutRevision = childLayoutRevisions.get(0);

						if (firstChildLayoutRevision.isInactive()) {
						%>

						<li>
							<a class="dropdown-item" href="javascript:Liferay.fire('<%= liferayPortletResponse.getNamespace() %>redo', {layoutRevisionId: '<%= firstChildLayoutRevision.getLayoutRevisionId() %>', layoutSetBranchId: '<%= firstChildLayoutRevision.getLayoutSetBranchId() %>'}); void(0);" id="redoLink">
								<liferay-ui:message key="redo" />
							</a>
						</li>

						<%
						}
						%>

					</c:if>
				</c:if>
			</ul>
		</div>
	</li>

<portlet:renderURL var="layoutRevisionStatusURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
	<portlet:param name="mvcPath" value="/view_layout_revision_status.jsp" />
</portlet:renderURL>

<portlet:renderURL var="markAsReadyForPublicationURL" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>">
	<portlet:param name="mvcPath" value="/view_layout_revision_details.jsp" />
</portlet:renderURL>

<portlet:renderURL var="viewHistoryURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="mvcPath" value="/view_layout_revisions.jsp" />
	<portlet:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranch.getLayoutSetBranchId()) %>" />
</portlet:renderURL>

<aui:script position="inline" use="liferay-staging-version">
	var stagingBar = Liferay.StagingBar;

	stagingBar.init({
		layoutRevisionStatusURL: '<%= layoutRevisionStatusURL %>',
		markAsReadyForPublicationURL: '<%= markAsReadyForPublicationURL %>',
		namespace: '<portlet:namespace />',
		portletId: '<%= portletDisplay.getId() %>',
		viewHistoryURL: '<%= viewHistoryURL %>',
	});
</aui:script>

<liferay-util:buffer
	var="pageVariationsHelpIcon"
>
	<liferay-ui:icon-help message="page-variations-help" />
</liferay-util:buffer>

<liferay-util:buffer
	var="sitePagesVariationsHelpIcon"
>
	<liferay-ui:icon-help message="pages-variations-help" />
</liferay-util:buffer>

<aui:script>
	function <portlet:namespace />openPageVariationsDialog() {
		Liferay.Util.openWindow({
			dialog: {
				after: {
					destroy: function(event) {
						window.location.reload();
					},
				},
				destroyOnHide: true,
			},
			id: 'pagesVariationsDialog',
			title:
				'<liferay-ui:message arguments="<%= pageVariationsHelpIcon %>" key="page-variations-x" />',

			<liferay-portlet:renderURL var="layoutBranchesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="mvcRenderCommandName" value="viewLayoutBranches" />
				<portlet:param name="layoutSetBranchId" value="<%= String.valueOf(layoutSetBranch.getLayoutSetBranchId()) %>" />
			</liferay-portlet:renderURL>

			uri: '<%= HtmlUtil.escapeJS(layoutBranchesURL) %>',
		});
	}

	function <portlet:namespace />openSitePagesVariationsDialog() {
		Liferay.Util.openWindow({
			dialog: {
				after: {
					destroy: function(event) {
						window.location.reload();
					},
				},
				destroyOnHide: true,
			},
			id: 'sitePagesVariationDialog',
			title:
				'<liferay-ui:message arguments="<%= sitePagesVariationsHelpIcon %>" key="site-pages-variation-x" />',

			<liferay-portlet:renderURL var="layoutSetBranchesURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
				<portlet:param name="mvcRenderCommandName" value="viewLayoutSetBranches" />
			</liferay-portlet:renderURL>

			uri: '<%= HtmlUtil.escapeJS(layoutSetBranchesURL) %>',
		});
	}

	function <portlet:namespace />submitLayoutRevision(publishURL) {
		Liferay.fire('<portlet:namespace />submit', {
			currentURL: '<%= currentURL %>',
			incomplete: <%= layoutRevision.isIncomplete() %>,
			publishURL: publishURL,
		});

		Liferay.Util.toggleDisabled('#<portlet:namespace />readyToggle', true);
	}
</aui:script>