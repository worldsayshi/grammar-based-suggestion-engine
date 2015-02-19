<%-- 
    Document   : documents
    Created on : Feb 5, 2015, 11:48:05 AM
    Author     : per.fredelius
--%>

<script class="doc-template" data-searchdomain="precisionSearch" data-docpath="" type="text/x-handlebars-template">
    <li class="document" class="media">
        <h2 class="title">{{name}}</h2>
        <div class="media-body">
            {{#if WORKS_IN}}<div><em>Plats:</em> {{WORKS_IN}}</div>{{/if}}
            {{#if KNOWS}}<div><em>Kunskap:</em> {{KNOWS}}</div>{{/if}}
            {{#if WORKS_WITH}}<div><em>Kunder:</em> {{WORKS_WITH}}</div>{{/if}}
            {{#if USES}}<div><em>Anv�nder:</em> {{USES}}</div>{{/if}}
        </div>
    </li>
</script>
<script class="doc-template" data-searchdomain="vasttrafik" data-docpath="trip" type="text/x-handlebars-template">
    <li class="document">
        <ul class="list-group well">
            {{#each legList}}
            <li class="list-group-item tripLeg">
                <span class="trip-badge trip-name badge badge-info">{{name}}</span>
                <span class="trip-badge trip-from badge">Fr�n: {{origin.name}}</span>
                <span class="trip-badge trip-to badge">Till: {{destination.name}}</span>
                <span class="trip-badge trip-time badge">Tid: {{destination.time}}</span>
            </li>
            {{/each}}
        </ul>
    </li>
</script>
