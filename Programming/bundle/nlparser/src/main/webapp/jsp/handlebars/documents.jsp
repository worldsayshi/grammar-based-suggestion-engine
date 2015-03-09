<%-- 
    Document   : documents
    Created on : Feb 5, 2015, 11:48:05 AM
    Author     : per.fredelius
--%>

<script class="doc-template" data-searchdomain="precisionSearch" data-docpath="" type="text/x-handlebars-template">
    <li class="document" class="media">
        <h2 class="title">{{name}}</h2>
        <div class="media-body">
            {{#if WORKS_IN}}<div><em>Locations:</em> {{WORKS_IN}}</div>{{/if}}
            {{#if KNOWS}}<div><em>Knowledge:</em> {{KNOWS}}</div>{{/if}}
            {{#if WORKS_WITH}}<div><em>Organizations:</em> {{WORKS_WITH}}</div>{{/if}}
            {{#if USES}}<div><em>Uses:</em> {{USES}}</div>{{/if}}
        </div>
    </li>
</script>
<script class="doc-template" data-searchdomain="vasttrafik" data-docpath="trip" type="text/x-handlebars-template">
    <li class="document">

        <ul class="list-group well">
            <p class="trip-title">
                <span>{{legList.[0].origin.name}} at {{legList.[0].origin.time}}  - {{#each legList}}{{#if @last}}{{destination.name}} at {{destination.time}}{{/if}}{{/each}}</span>
                <span>({{tripTime}} minutes, {{transfers}} transfers )</span>
            </p>
            {{#each legList}}
            <li class="list-group-item tripLeg">
                <span class="trip-badge trip-name badge badge-info">{{name}}</span>
                <span class="trip-badge trip-from badge">From: {{origin.name}}</span>
                <span class="trip-badge trip-to badge">To: {{destination.name}}</span>
                <span class="trip-badge trip-time badge">Departure time: {{origin.time}}</span>
                <span class="trip-badge trip-time badge">Arrival time: {{destination.time}}</span>
            </li>
            {{/each}}
        </ul>
    </li>
</script>
