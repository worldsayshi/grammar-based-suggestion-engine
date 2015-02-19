<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
    <head>
        <meta charset="utf-8"> 

        <script type="text/javascript" src="static/underscore.js"></script>
        <script type="text/javascript" src="static/handlebars-v2.0.0.js"></script>
        <script type="text/javascript" src="/webjars/jquery/2.1.3/jquery.min.js"></script>
        <link rel='stylesheet' href='/webjars/bootstrap/3.3.2/css/bootstrap.min.css'>
        <script type="text/javascript" src="/webjars/bootstrap/3.3.2/js/bootstrap.min.js"></script>
        <script type="text/javascript" src="/webjars/typeaheadjs/0.10.4-1/typeahead.bundle.js"></script>

        <script type="text/javascript" src="static/script.js"></script>

        <link rel="stylesheet" type="text/css" href="static/style.css">
    <body>

        <div id="info">
            <p id="info_start">Click on the microphone icon and begin speaking.</p>
            <p id="info_speak_now">Speak now.</p>
            <p id="info_no_speech">No speech was detected. You may need to adjust your
                <a href="//support.google.com/chrome/bin/answer.py?hl=en&amp;answer=1407892">
                    microphone settings</a>.</p>
            <p id="info_no_microphone" style="display:none">
                No microphone was found. Ensure that a microphone is installed and that
                <a href="//support.google.com/chrome/bin/answer.py?hl=en&amp;answer=1407892">
                    microphone settings</a> are configured correctly.</p>
            <p id="info_allow">Click the "Allow" button above to enable your microphone.</p>
            <p id="info_denied">Permission to use microphone was denied.</p>
            <p id="info_blocked">Permission to use microphone is blocked. To change,
                go to chrome://settings/contentExceptions#media-stream</p>
            <p id="info_upgrade">Web Speech API is not supported by this browser.
                Upgrade to <a href="//www.google.com/chrome">Chrome</a>
                version 25 or later.</p>
        </div>   

        <div id="results">
            <span id="final_span" class="final"></span>
            <span id="interim_span" class="interim"></span>
            <p>
        </div>

        <div class="right">
            <button id="start_button" onclick="startButton(event)">
                <img id="start_img" src="mic.gif" alt="Start"></button>
        </div>


    </select-->
    <ul id="search_result"></ul>


    <%@include file="tabs.jsp" %>

</body>
</html>
