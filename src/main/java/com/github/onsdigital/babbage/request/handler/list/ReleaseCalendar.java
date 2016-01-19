//package com.github.onsdigital.babbage.request.handler.list;
//
//import com.github.onsdigital.babbage.content.client.ContentReadException;
//import com.github.onsdigital.babbage.request.handler.base.ListPageBaseRequestHandler;
//import com.github.onsdigital.babbage.request.handler.base.RequestHandler;
//import com.github.onsdigital.babbage.search.ONSQuery;
//import com.github.onsdigital.babbage.search.helpers.SearchResponseHelper;
//import com.github.onsdigital.babbage.search.model.ContentType;
//import com.github.onsdigital.babbage.search.model.field.FilterableField;
//import org.elasticsearch.index.query.AndFilterBuilder;
//import org.elasticsearch.index.query.FilterBuilders;
//import org.elasticsearch.index.query.RangeFilterBuilder;
//import org.elasticsearch.index.query.TermFilterBuilder;
//import org.elasticsearch.search.sort.FieldSortBuilder;
//import org.elasticsearch.search.sort.SortOrder;
//
//import javax.servlet.http.HttpServletRequest;
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//
//import static com.github.onsdigital.babbage.search.helpers.SearchRequestHelper.addOrFilters;
//import static org.elasticsearch.index.query.FilterBuilders.andFilter;
//import static org.elasticsearch.index.query.FilterBuilders.notFilter;
//
///**
// * Created by bren on 22/09/15.
// */
//public class ReleaseCalendar extends ListPageBaseRequestHandler implements RequestHandler {
//    private final static ContentType[] ALLOWED_TYPES = {ContentType.release};
//    private final static String REQUEST_TYPE = "releasecalendar";
//
//    @Override
//    protected ContentType[] getAllowedTypes() {
//        return ALLOWED_TYPES;
//    }
//
//    @Override
//    public boolean isLocalisedUri() {
//        return false;
//    }
//
//    @Override
//    protected boolean isListTopics() {
//        return false;
//    }
//
//    @Override
//    public String getRequestType() {
//        return REQUEST_TYPE;
//    }
//
//    @Override
//    protected List<SearchResponseHelper> doSearch(HttpServletRequest request, ONSQuery... queries) throws IOException, ContentReadException {
//        ONSQuery query = queries[0];
//
//        String view = request.getParameter("view");
//        boolean upcoming = "upcoming".equals(view);//published releases are requested
//
//        TermFilterBuilder published = FilterBuilders.termFilter(FilterableField.published.name(), true);
//        TermFilterBuilder cancelled = FilterBuilders.termFilter(FilterableField.cancelled.name(), true);
//        RangeFilterBuilder due = FilterBuilders.rangeFilter(FilterableField.releaseDate.name()).to(new Date());
//
//        if (upcoming) { //upcoming
//            AndFilterBuilder notPublishedAndNotCancelled = andFilter(notFilter(published), notFilter(cancelled));
//            AndFilterBuilder cancelledAndNotDue = andFilter(cancelled, notFilter(due));
//            addOrFilters(query, notPublishedAndNotCancelled, cancelledAndNotDue);// not published and not cancelled or cancelled and not due
//            query.getSorts().clear();
//            query.addSort(new FieldSortBuilder(FilterableField._score.name()).order(SortOrder.DESC));
//            query.addSort(new FieldSortBuilder(FilterableField.releaseDate.name()).order(SortOrder.ASC).ignoreUnmapped(true));
//        } else {//published
//            AndFilterBuilder publishedAndNotCancelled = andFilter(published, notFilter(cancelled));
//            AndFilterBuilder cancelledAndDue = andFilter(cancelled, due);
//            addOrFilters(query, publishedAndNotCancelled, cancelledAndDue);// published and not cancelled or cancelled and due
//        }
//
//        return super.doSearch(request, query);
//    }
//}
