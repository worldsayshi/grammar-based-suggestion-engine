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
            <li role="presentation" class="<c:if test="${(empty params['domain'] && loop.index=='0') || (not empty params['domain'] && domainEntry.key eq params['domain'])}">active</c:if>">
                <a href="#${domainEntry.key}" aria-controls="home" role="tab" onclick="tabChanged('${domainEntry.key}')" data-toggle="tab">${domainEntry.key}</a>
            </li>
        </c:forEach>
    </ul>

    <!-- Tab panes -->
    <div class="tab-content">

        <c:forEach var="domainEntry" items="${searchDomains}" varStatus="loop">
            <div role="tabpanel" class="tab-pane <c:if test="${(empty params['domain'] && loop.index=='0') || (not empty params['domain'] && domainEntry.key eq params['domain'])}">active</c:if>" id="${domainEntry.key}">
                <h2 id="MainTitle">${domainEntry.key} Demo</h2>

                <p class="description">${descriptions[domainEntry.key]}</p>
                <form id="search-form-${domainEntry.key}" class="search-form">

                    <button type="button" class="speechButton" id="button-${domainEntry.key}" onclick="startButton('${domainEntry.key}')" style="border: 0; background: transparent">
                        <img src="/static/micon.jpg" alt="speak" id="buttonImage-${domainEntry.key}">                   
                    </button>

                    <input id="search-input-${domainEntry.key}" name="q" type="text" class="input-large search-input"  
                           placeholder="Type your question.." data-searchdomain="${domainEntry.key}"></input>
                    <input type="submit" style="position: absolute; left: -9999px"/>

                    <button type="button" class="clearButton" id="clear-${domainEntry.key}" value="" onclick="clearButton()" style="border: 0; background: transparent">
                        <img src="/static/clear.jpg" alt="clear" id="clearImage-${domainEntry.key}">                   
                    </button>

                    <c:if test="${fn:length(domainEntry.value) lt 2}" >
                        <input id="search-lang-select-${domainEntry.key}" class="search-lang-select" name="lang" type="hidden" value="${domainEntry.value[0]}" />
                    </c:if>
                    <c:if test="${fn:length(domainEntry.value) gt 1}" >
                        <select id="search-lang-select-${domainEntry.key}" class="search-lang-select" name="lang" onchange="langSelected(this)">
                            <c:forEach var="lang" items="${domainEntry.value}">
                                <c:choose>
                                    <c:when test="${not empty params['lang'] and params['lang'] eq lang}">
                                        <option value="${lang}" selected="selected">${lang}</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${lang}">${lang}</option>
                                    </c:otherwise>
                                </c:choose>

                            </c:forEach>
                        </select>
                    </c:if>

                    <!-- parameters hardcoded for now-->
                    <c:if test="${domainEntry.key eq 'vasttrafik'}">
                        <input name="noRepetitionTypes" type="hidden" value="station" />
                        <input name="maxAdditionalSuggestedNames" type="hidden" value="1" />
                        <input name="maxSuggestions" type="hidden" value="10" />
                    </c:if>

                    <c:if test="${domainEntry.key eq 'precisionSearch'}">
                        <input name="noRepetitionTypes" type="hidden" value="skill,organization,location" />
                        <input name="maxAdditionalSuggestedNames" type="hidden" value="1" />
                        <input name="maxSuggestions" type="hidden" value="10" />
                    </c:if>
                </form>
            </div>
        </c:forEach>
    </div>

</div>
