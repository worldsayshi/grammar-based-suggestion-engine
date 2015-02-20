
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
                console.log(list);
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
   
    $(".search-input").typeahead({},{
        source:suggestions.ttAdapter(),
        displayKey: 's',
        name: "grammar-suggestions"
    });
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
    
    for( var i = event.resultIndex; i < event.results.length; ++i){
        q.value += event.results[i][0].transcript;
    }
    
    for (var i = event.resultIndex; i < event.results.length; ++i) {
        if (event.results[i].isFinal) {
            final_text += event.results[i][0].transcript;
        } else {
            interim_text += event.results[i][0].transcript;
        }
    }
    
    q.value = final_text + interim_text;
};

recognition.onstart = function() {
    recognizing = true;
    speakButton.value = "Click to Stop!";
};

recognition.onend = function() {
    recognizing = false;
    speakButton.value = "Click to Speak!";
};
    
function startButton() {
    if (recognizing) {
        recognition.stop();
        return;
    }
    q.value = "";
    interim_text = "";
    final_text = "";

    recognition.start();
};    

