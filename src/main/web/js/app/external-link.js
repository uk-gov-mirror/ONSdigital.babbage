// Using regex instead of simply using 'host' because it causes error with security on Government browsers (IE9 so far)
function getHostname(url) {
    var m = url.match(/^http(s?):\/\/[^/]+/);
    return m ? m[0] : null;
}

function eachAnchor(anchors) {

    $(anchors).each(function() {
        var href = $(this).attr("href");
        var hostname = getHostname(href);

        if (hostname) {
            if (hostname !== document.domain && hostname.indexOf('ons.gov.uk') == -1) {
                $(this).attr('target', '_blank');
            }
        }

    });
}

$(function() {
    eachAnchor('a[href^="http://"]:not([href*="loop11.com"]):not([href*="ons.gov.uk"])');
    eachAnchor('a[href^="https://"]:not([href*="loop11.com"]):not([href*="ons.gov.uk"])');
    eachAnchor('a[href*="nationalarchives.gov.uk"]');
});
