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


                    <!-- parameters hardcoded for now-->
                    <c:if test="${domainEntry.key eq 'vasttrafik'}">
                        <input name="noRepetitionTypes" type="hidden" value="station" />
                        <input name="maxAdditionalSuggestedNames" type="hidden" value="2" />
                    </c:if>

                    <c:if test="${domainEntry.key eq 'precisionSearch'}">
                        <input name="noRepetitionTypes" type="hidden" value="skill,organization,location" />
                        <input name="maxAdditionalSuggestedNames" type="hidden" value="2" />
                    </c:if>
                </form>
            </div>
        </c:forEach>
    </div>

</div>