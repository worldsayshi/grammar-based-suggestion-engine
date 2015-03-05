
var colors = ["red", "blue", "green", "yellow", "brown", "black"];

$(function () {
    // Javascript to enable link to tab
    var url = document.location.toString();
    if (url.match('#')) {
        $('.nav-tabs a[href=#' + url.split('#')[1] + ']').tab('show');
    }

    // Change hash for page-reload
    $('.nav-tabs a').on('shown', function (e) {
        window.location.hash = e.target.hash;
    });
    
    
    
    var suggestions = new Bloodhound({
        datumTokenizer: function(d) {
            return Bloodhound.tokenizers.whitespace(d.value);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        limit: 1000,
        remote: {
            url:"/api/<%= searchdomain %>/suggestSentences?<%= query %>",
            replace: function (url) {
                var templVars = {
                    searchdomain:$(lastFocusedInput).data("searchdomain"),
                    query:$(lastFocusedInput).closest("form").serialize()
                };
                var newUrl = _.template(url)(templVars);
                return newUrl;
            },
            filter: function(list) {
                
                //if in speech mode and there is only one suggestion
                //perform search automatically
                if((loading || recognizing) && list.length == 1){
                    loading = false;
                    $('#search-input-' + currentDomain).typeahead('val',list[0]);
                    $('#search-input-' + currentDomain).typeahead('close');
                    
                    doSearch(
                        $('#search-input-' + currentDomain).closest("form").serialize(),
                        $('#search-input-' + currentDomain).data("searchdomain"));
                }
                
                return $.map(list, function(word) {
                    return {
                        s: word
                    };
                });
            }
        }
    });
    suggestions.initialize();
    
    var lastFocusedInput = undefined;
    $("input").focus(function () {
        lastFocusedInput = this;
    });
    
    var docTemplates = {};
    $('.doc-template').each(function () {
        var searchDomain = $(this).data('searchdomain');
        docTemplates[searchDomain] = {
            templ:Handlebars.compile($(this).html())
            ,
            docpath:$(this).data('docpath')
        };
    });
   
    function doSearch(query,searchdomain) {  
        //cancell previous search
        if(currentRequest)
        {
            currentRequest.abort();
        }
        
        var urlTempl = "/api/<%= searchdomain %>/search?<%= query %>";
        var templVars = {
            searchdomain: searchdomain,
            query:query
        };
        var u = _.template(urlTempl)(templVars);
        var currentQ = $('#search-input-' + currentDomain).val();
        
        $("#results_title").empty().append("<b>Results for: '" + currentQ + "'</b>");
        $("#search_result").empty().append("<img src='/static/loader.gif' alt='loading' height='100' width='100'>");
              
        var url = window.location.href;
        url = setParameter('q', currentQ, url);
        url = removeHost(url);
        window.history.pushState('test','testTitle',url);      
              
        currentRequest = $.get(u,function(data){
                var docpath = docTemplates[searchdomain].docpath;
                var docs = getDocsWithDocPath(data,docpath);
                var templ = docTemplates[searchdomain].templ;
            
                $("#search_result").empty().append($.map(docs, function (doc, ix) {
                    return templ(doc);
                }));
        });
    }
   
    $(".search-input").typeahead({},{
        source:suggestions.ttAdapter(),
        displayKey: 's',
        name: "grammar-suggestions"
    }).on('typeahead:selected',function (e, datum) {
        doSearch(
            $(this).closest("form").serialize(),
            $(this).data("searchdomain"));
    });
    
    function getDocsWithDocPath(data,docpath) {
        var elem = data;
        var pathSegments = _.compact(docpath.split(".")); // remove empty elements
        _.each(pathSegments,function (seg) {
            elem = elem[seg];
        });
        return elem;
    }
});


//Speech recognition stuff ------------------------------------------------------------

// hide if the browser does not support google speech (only chrome supports this)
$(document).ready(function(){
    if (!('webkitSpeechRecognition' in window)) {
        $(".speechButton").hide();
    }
    
    $(".clearButton").hide();
    
    currentDomain = $(".tab-pane.active").attr("id");
    
    if(params.indexOf("query="))
    
        var queryString = extractQueryParam();
    if(queryString && queryString.length!=0){
        loading = true;
        setCurrentInputText(queryString);
    }
    
});

function extractQueryParam(){
    if(params.indexOf("query=")>=0){
        var result = params.substring(params.indexOf("query="));
        result = result.substring(result.indexOf("=") + 1); 
        result = result.substring(0, Math.min(result.indexOf(","),result.indexOf("}")));
    }
    
    return result;
}

var recognizing = false;
var recognition = new webkitSpeechRecognition();
var loading = false;
var currentRequest;
recognition.lang = 'en-US';
recognition.continuous = true;
recognition.interimResults = true;

var final_text = "";
var interim_text = "";

recognition.onresult = function(event) {    
    interim_text = "";     
    lastWord = ""; 
     
    for (var i = event.resultIndex; i < event.results.length; ++i) {
        if (event.results[i].isFinal) {
            final_text += event.results[i][0].transcript;
            lastWord = event.results[i][0].transcript;
        } else {
            interim_text += event.results[i][0].transcript;
        }
    }
   
    if(endsWith(lastWord.trim(),"reset")){
        final_text = "";
    }
   
    setCurrentInputText(final_text + interim_text);
};

recognition.onstart = function() {
    recognizing = true;
    setCurrentButtonImage("/static/micoff.jpg");
    document.getElementById("search-input-" + currentDomain).readOnly = true;
    $(".clearButton").show();
};

recognition.onend = function() {
    recognizing = false;
    setCurrentButtonImage("/static/micon.jpg");
    document.getElementById("search-input-" + currentDomain).readOnly = false;
    $(".clearButton").hide();
};
    
var currentDomain = "";    

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

function startButton(domain) {
    
    if (recognizing) {
        recognition.stop();
        return;
    }
    
    currentDomain = domain;
    
    setCurrentInputText("");
    interim_text = "";
    final_text = "";
    recognition.start();
}; 

function clearButton() {
    final_text = "";
    setCurrentInputText("");
};

function setCurrentButtonImage(src){    
    document.getElementById("buttonImage-" + currentDomain).src = src;
}

function setCurrentInputText(text){
    
    text = text.replace(/^\s+/g, "");
    
    $('#search-input-' + currentDomain).focus();
    $('#search-input-' + currentDomain).typeahead('val','replace');

    $('#search-input-' + currentDomain).focus();
    $('#search-input-' + currentDomain).typeahead('val',text);
}

function tabChanged(domain){
    
    //cancell previous search
    if(currentRequest)
    {
        currentRequest.abort();
    }
    
    if (recognizing) {
        recognition.stop();
        return;
    }
    $("#results_title").empty();
    $("#search_result").empty();
    $('#search-input-' + currentDomain).typeahead('val',"");
    
    currentDomain = domain;
    
    var url = window.location.href;
    url = setParameter('domain', domain, url);
    url = setParameter('lang', $('#search-lang-select-' + currentDomain).val(), url);
    url = removeParameter('q', url);
    url = removeHost(url);
    window.history.pushState('test','testTitle',url);
};


// routing stuff

function langSelected(sel)
{
    $('#search-input-' + currentDomain).typeahead('val',"");
    
    var url = window.location.href;
    url = setParameter('lang', sel.value, url);
    url = removeParameter('q', url);
    url = removeHost(url);
    window.history.pushState('test','testTitle', url);
};

function removeHost(url)
{
    return url.replace (/^[a-z]{4}\:\/{2}[a-z]{1,}\:[0-9]{1,4}.(.*)/, '$1');
}

function removeParameter(paramName, url)
{
    if (url.indexOf(paramName + "=") >= 0)
    {
        var prefix = url.substring(0, url.indexOf(paramName));
        if(endsWith(prefix,"&")){
            prefix = prefix.substring(0,prefix.length - 1);
        }
        var suffix = url.substring(url.indexOf(paramName));
        suffix = suffix.substring(suffix.indexOf("=") + 1);
        suffix = (suffix.indexOf("&") >= 0) ? suffix.substring(suffix.indexOf("&")) : "";
        url = prefix + suffix;
    }

    return url;
}

function setParameter(paramName, paramValue, url)
{
    if (url.indexOf(paramName + "=") >= 0)
    {
        var prefix = url.substring(0, url.indexOf(paramName));
        var suffix = url.substring(url.indexOf(paramName));
        suffix = suffix.substring(suffix.indexOf("=") + 1);
        suffix = (suffix.indexOf("&") >= 0) ? suffix.substring(suffix.indexOf("&")) : "";
        url = prefix + paramName + "=" + paramValue + suffix;
    }
    else
    {
        if (url.indexOf("?") < 0)
            url += "?" + paramName + "=" + paramValue;
        else
            url += "&" + paramName + "=" + paramValue;
    }
    
    return url;
};

