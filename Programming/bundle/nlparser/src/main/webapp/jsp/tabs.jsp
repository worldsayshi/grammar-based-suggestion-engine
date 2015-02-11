<%-- 
    Document   : foobar
    Created on : Feb 10, 2015, 4:05:57 PM
    Author     : per.fredelius
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div role="tabpanel">
    <!-- Nav tabs -->

    <ul class="nav nav-tabs" role="tablist">
        <c:forEach var="domainEntry" items="${searchDomains}" varStatus="loop">
            <li role="presentation" class="<c:if test="${loop.index=='0'}">active</c:if>">
                <a href="#${domainEntry.key}" aria-controls="home" role="tab" data-toggle="tab">${domainEntry.key}</a>
            </li>
            <%--li role="presentation" class="active"><a href="#home" aria-controls="home" role="tab" data-toggle="tab">Home2</a></li>
            <li role="presentation"><a href="#profile" aria-controls="profile" role="tab" data-toggle="tab">Profile222</a></li>
            <li role="presentation"><a href="#messages" aria-controls="messages" role="tab" data-toggle="tab">Messages</a></li>
            <li role="presentation"><a href="#settings" aria-controls="settings" role="tab" data-toggle="tab">Settings</a></li--%>
        </c:forEach>
    </ul>

    <!-- Tab panes -->
    <div class="tab-content">

        <c:forEach var="domainEntry" items="${searchDomains}" varStatus="loop">
            <div role="tabpanel" class="tab-pane <c:if test="${loop.index=='0'}">active</c:if>" id="${domainEntry.key}">
                <h2 id="MainTitle">${domainEntry.key} Demo</h2>

                <form id="search-form-${domainEntry.key}">
                    <input id="search-input-${domainEntry.key}" name="q" type="text" class="input-large search-input" 
                           placeholder="Type your question.." data-searchdomain="${domainEntry.key}"></input>
                    <c:if test="${fn:length(domainEntry.value) lt 2}" >
                        <input name="lang" type="hidden" value="${domainEntry.value[0]}" />
                    </c:if>
                    <c:if test="${fn:length(domainEntry.value) gt 1}" >
                        <select class="search-lang-select" name="lang">
                            <c:forEach var="lang" items="${domainEntry.value}">
                                <option value="${lang}">${lang}</option>
                            </c:forEach>
                        </select>
                    </c:if>
                </form>
            </div>
        </c:forEach>
        <%--div role="tabpanel" class="tab-pane active" id="home">home</div>
        <div role="tabpanel" class="tab-pane" id="profile">profile</div>
        <div role="tabpanel" class="tab-pane" id="messages">mess</div>
        <div role="tabpanel" class="tab-pane" id="settings">sett</div--%>
    </div>

</div>