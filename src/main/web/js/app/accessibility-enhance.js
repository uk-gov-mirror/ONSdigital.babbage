$(window).on('load', function() {
    if ($('.timeseries__chart').length == 0) {
        // Enhance markdown charts
        $('.highcharts-container').each(function () {
            highchartsAccessibilityAttrs($(this), 'Chart representing data available in following XLS or CSV download');
        });
    } else {
        // Do accessibility goodness to T5
        timeseriesAccessibilityAttrs()
    }
});

function highchartsAccessibilityAttrs(selector, labelText, removeAttrs) {
    if (!removeAttrs) {
        selector.attr('aria-label', labelText);
        selector.find('svg').attr('aria-hidden', 'true').attr('focusable', 'false');
    } else {
        selector.attr('aria-label', '');
        selector.find('svg').attr('aria-hidden', 'false');
    }
}

function timeseriesAccessibilityAttrs(removeAttrs) {
    highchartsAccessibilityAttrs($('.timeseries__chart'), 'Chart representing data available. Select "table" in show data as to display data as a table', removeAttrs);
}
