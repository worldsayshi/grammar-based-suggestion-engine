<%-- 
    Document   : documents
    Created on : Feb 5, 2015, 11:48:05 AM
    Author     : per.fredelius
--%>

<script id="doc_template" type="text/x-handlebars-template">
    <li class="document" class="media">
        <h2 class="title">{{name}}</h2>
        <div class="media-body">
            {{#if WORKS_IN}}<div><em>Plats:</em> {{WORKS_IN}}</div>{{/if}}
            {{#if KNOWS}}<div><em>Kunskap:</em> {{KNOWS}}</div>{{/if}}
            {{#if WORKS_WITH}}<div><em>Kunder:</em> {{WORKS_WITH}}</div>{{/if}}
            {{#if USES}}<div><em>Använder:</em> {{USES}}</div>{{/if}}
        </div>
    </li>
</script>
<script id="vasttrafik_doc_template" type="text/x-handlebars-template">
    <li class="document">
        <ul class="list-group well">
            {{#each legList}}
            <li class="list-group-item tripLeg">
                <span class="trip-name badge badge-info">{{name}}</span>
                <span class="trip-from badge">Från: {{origin.name}}</span>
                <span class="trip-to badge">Till: {{destination.name}}</span>
                <span class="trip-time badge">Tid: {{destination.time}}</span>
            </li>
            {{/each}}
        </ul>
    </li>
</script>
