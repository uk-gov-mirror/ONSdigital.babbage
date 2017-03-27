#TODO for removal of Elastic from Babbage
To remove all references to ElasticSearch&trade;  from within Babbage and to isolate
into the ElasticSearch Micro Services.

## Filters
Currently the `dp-search-query ` Micro Service has implemented a limited set of filters; `latest` and `withFirstLetter` filters. Currently it is understood that Babbage has a requirement for additional filters:

1. ~~Date Range (fromDate and/or toDate)~~
2. ~~URL Prefix~~
3. URI _this might be a new lookup query_
4. Topic Wildcard
5. Upcoming
6. Published


##Handlers
Current list of Handlers that need to be refactored to use the new `dp-search-query`

* ~~PreviousReleasesRequestHandler~~
* TopicSpecificMethodologyRequestHandler
* ~~PublicationsRequestHandler~~
* RssService
* DataListRequestHandler
* RelatedDataRequestHandler
* RelatedDataRequestHandler
* DataListRequestHandler
* PublicationsRequestHandler
* Calendar
* PublishingManager
* TimeSeriesTool
* PublishedRequestsRequestHandler
* ReleaseCalendar
* AllAdhocsRequestHandler
* ReleaseCalendar
