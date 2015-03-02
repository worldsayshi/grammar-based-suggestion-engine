
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
                if(recognizing && list.length == 1){
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
        var urlTempl = "/api/<%= searchdomain %>/search?<%= query %>";
        var templVars = {
            searchdomain: searchdomain,
            query:query
        };
        var url = _.template(urlTempl)(templVars);
        $.get(url,function(data){
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
});

var recognizing = false;
var recognition = new webkitSpeechRecognition();
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
    $('#search-input-' + currentDomain).focus();
    $('#search-input-' + currentDomain).typeahead('val','replace');

    $('#search-input-' + currentDomain).focus();
    $('#search-input-' + currentDomain).typeahead('val',text);
}

function tabChanged(domain){
    if (recognizing) {
        recognition.stop();
        return;
    }
    
    currentDomain = domain;
};

