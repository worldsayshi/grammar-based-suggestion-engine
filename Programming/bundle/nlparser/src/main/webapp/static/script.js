
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
    })
    
    
    
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
           ,docpath:$(this).data('docpath')
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

var recognizing = false;
var recognition = new webkitSpeechRecognition();
recognition.lang = 'en-US';
recognition.continuous = true;
recognition.interimResults = true;

var final_text = "";
var interim_text = "";

recognition.onresult = function(event) {
      
    interim_text = "";
     
    for (var i = event.resultIndex; i < event.results.length; ++i) {
        if (event.results[i].isFinal) {
            final_text += event.results[i][0].transcript;
        } else {
            interim_text += event.results[i][0].transcript;
        }
    }
   
    setCurrentInputText(final_text + interim_text);
};

recognition.onstart = function() {
    recognizing = true;
    setCurrentButtonText("Click to Stop!");
};

recognition.onend = function() {
    recognizing = false;
    setCurrentButtonText("Click to Speak!");
};
    
var currentDomain = "";    
    
function startButton(domain) {
    
    if (recognizing) {
        recognition.stop();
        return;
    }
    
    currentDomain = domain;
    
    console.log("got domain: " + domain);
    
    setCurrentInputText("");
    interim_text = "";
    final_text = "";

    recognition.start();
}; 

function setCurrentButtonText(text){    
    document.getElementById("button-" + currentDomain).value = text;
}

function setCurrentInputText(text){
    $('#search-input-' + currentDomain).focus();
    $('#search-input-' + currentDomain).typeahead('val','blabla');

    $('#search-input-' + currentDomain).focus();
    $('#search-input-' + currentDomain).typeahead('val',text);
}

function tabChanged(){
    if (recognizing) {
        recognition.stop();
        return;
    }
};

function inputted(){
    console.log("mamy input");
}

