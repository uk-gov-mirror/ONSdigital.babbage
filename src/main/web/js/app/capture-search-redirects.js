$(function() {
  // Get the referrer from the url
  var url = window.location.href
  var referrer = getParameterByName("referrer", url)
  if (!referrer) {
    return;
  }
  // Get the search term from the url
  var searchTerm = getParameterByName("searchTerm", url)
  if (!searchTerm) {
    return;
  }
  // Push to GTM
  window.dataLayer.push({
      'event': 'searchRedirect',
      'searchTerm': searchTerm,
      'searchCategory': 'timeseries'
  });

  // Simple function to extract named parameters from a url
  function getParameterByName(name, url) {
      if (!url) url = window.location.href;
      name = name.replace(/[\[\]]/g, "\\$&");
      var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
          results = regex.exec(url);
      if (!results) return null;
      if (!results[2]) return '';
      return decodeURIComponent(results[2].replace(/\+/g, " "));
  }
});
