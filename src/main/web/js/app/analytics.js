// Google analytics JS

/**
 * Function that tracks events in Google Analytics.
 * Setting the transport method to 'beacon' lets the hit be sent
 * using 'navigator.sendBeacon' in browser that support it.
 */

var trackEvent = function(category, label) {
    ga('send', 'event', category, 'click', label, {
        'transport': 'beacon'
        //'hitCallback': function(){document.location = url;}
    });
};

$(function() {
    // external link tracking
    $("a[target='_blank']").on( "click", function() {
        var href = $(this).attr("href");
        trackEvent("outbound", href);
    });

    // data-ga-event attribute tracking
    $('a[data-ga-event]').on("click", function() {
        var $this = $(this),
            category = $this.attr('data-ga-event-category'),
            label = $this.attr('data-ga-event-label');
        trackEvent(category, label);
    });

});