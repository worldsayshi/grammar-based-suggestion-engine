<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta charset="utf-8"> 
<%--script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="static/URI.js"></script>
<script src="static/jquery-ui-1.10.4.custom/js/jquery-ui-1.10.4.custom.min.js"></script--%>

<script type="text/javascript" src="static/underscore.js"></script>
<script type="text/javascript" src="static/handlebars-v2.0.0.js"></script>
<script type="text/javascript" src="/webjars/jquery/2.1.3/jquery.min.js"></script>
<link rel='stylesheet' href='/webjars/bootstrap/3.3.2/css/bootstrap.min.css'>
<script type="text/javascript" src="/webjars/bootstrap/3.3.2/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/webjars/typeaheadjs/0.10.4-1/typeahead.bundle.js"></script>

<script type="text/javascript" src="static/script.js"></script>

<!--link rel="stylesheet" type="text/css" href="static/bootstrap/css/bootstrap.css"--> 

<%--link rel="stylesheet" type="text/css" href="static/jquery-ui-1.10.4.custom/css/smoothness/jquery-ui-1.10.4.custom.css"--%>
<link rel="stylesheet" type="text/css" href="static/style.css">
<body>
<!--div style="float:right; width: 400px;border: gray 1px solid;padding: 10px;">To get started with the demo try writing one or more of the following terms in the search box: <br><br>
Java, people, customers, projects, copenhagen</div-->





<%--h2 id="MainTitle">Västtrafik demo</h2>
<input id="input" type="text" class="input-large" placeholder="Type your question.."></input>
<input id="language" type="hidden" value="VasttrafikEngConcat" /--%>
<!--select id="language">
  <option value="VasttrafikEngConcat">English (concat)</option>
  <!--option value="InstrucsEngRGL">English (RGL)</option>
  <option value="InstrucsSweRGL">Swedish (RGL)</option-->
</select-->
<ul id="search_result"></ul>
<%--@include file="handlebars/documents.jsp" --%>

<%@include file="tabs.jsp" %>



</body>
</html>
