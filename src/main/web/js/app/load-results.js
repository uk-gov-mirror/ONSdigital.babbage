
function loadNewResults(url, focus) {
    // Selector classes/IDs
    var results = '.results',
        resultsText = '.search-page__results-text',
        paginationContainer = '#js-pagination-container',
        tabsContainer = '.tabs--js',
        checkboxContainer = '.js-checkbox-container',
        atozFilters = '.filters__a-z',
        errorMsg = '.js-auto-submit__error',
        $results = $(results),
        resultsHeight = $results.height();

    //Show 'Loading...' in place of results text before Ajax starts
    updateContents(resultsText, 'Loading...');

    //Ajax request for new URL
    $.ajax({
        url: url,
        success: function (result) {
            //Removes current results from page and loads in new results
            function replaceResults(url, newResults, newResultsText, newPagination) {
                var $newResults = $(newResults);

                $newResults.hide().appendTo(results).fadeIn(300);

                //Re-run functions done on load that are needed after Ajax
                getSparkline();
                hoverState();
                timeseriesTool.refresh();

                //Update results text
                updateContents(resultsText, newResultsText);

                if (newPagination || newPagination == '') {
                    //Update pagination for results
                    updateContents(paginationContainer, newPagination);
                }

                //Pushes new url into browser, if browser compatible (enhancement)
                if (history.pushState) {
                    window.history.pushState({}, '', url);
                }
            }

            //Update filters
            function replaceFilters(newFilters) {
                if ($(newFilters).is(checkboxContainer)) {
                    //Detect what filters are being updated
                    var checkboxId = $(newFilters).find('input').attr('id');

                    //Find corresponding filters on current page
                    var $checkboxFilters = $('#' + checkboxId).closest(checkboxContainer);

                    //Empty and replace checkboxes
                    updateContents($checkboxFilters, $(newFilters).html());
                }

                if ($(newFilters).is(atozFilters)) {
                    //If page A-Z and no checkboxes
                    updateContents('.js-atoz-container', newFilters);
                }

            }

            // Update error message
            function replaceErrorMsg() {
                $errorMsg.each(function(i) {
                    var $this = $(this),
                        id = $this.attr('id');

                    $('#' + id).html($newErrorMsg[i].innerHTML);
                });
            }


            /* Run functions to replace content on page */
            //Errors
            var $errorMsg = $(errorMsg);
            var $newErrorMsg = $(result).find(errorMsg);
            if ($newErrorMsg.children().length > 0 || $errorMsg.children().length > 0) {
                console.log('Current error: ', $errorMsg);
                console.log('New error: ', $newErrorMsg);
                replaceErrorMsg();
            }
            if (($newErrorMsg.children().length > 0)) {
                // Stop rest of replace and update results text if there's an error
                updateContents(resultsText, 'There is an error with the date you have selected.');
                return false;
            }

            // Empty results & pagination
            $results.height(resultsHeight).empty(); // Set height so that footer doesn't move around page erratically
            $('#js-pagination-container').empty();

            //Results
            var newResults = $(result).find(results).html(),
                newResultsText = $(result).find(resultsText).html(),
                newTabsContainer = $(result).find(tabsContainer).html(),
                newPagination = '';
            if ($(result).find(paginationContainer).length > 0) {
                newPagination = $(result).find(paginationContainer).html();
            }
            replaceResults(url, newResults, newResultsText, newPagination);

            //Filters
            if ($(result).find(checkboxContainer).length > 0) {
                var $filters = $(result).find(checkboxContainer);
                $filters.each(function () {
                    replaceFilters(this);
                });
            }
            if ($(result).find(atozFilters).length > 0) {
                var $atozFilters = $(result).find(atozFilters);
                replaceFilters($atozFilters);
            }

            //Tab counts (only when page has tab container and keyword search or custom dates - otherwise no update required
            if (newTabsContainer && $('.filters').find('input[type="search"], input[type="text"]')) {
                updateContents(tabsContainer, newTabsContainer);
            }

            //Ensure focus back onto correct element on page
            if (focus) {
                var focusId = '#' + focus.attr('id');
                $(focusId).focus();
            }

            insertRssLink();
        }
    });

    // Revert results height to auto after Ajax is finished
    $(document).ajaxComplete(function() {
        $results.height('auto');
    });
}

//Remove and replaces content according to selector and results parsed into function
function updateContents(id, newContents) {
    var $element = $(id);

    //Remove values from search and text inputs
    if ($element.is('input[type="search"], input[type="text"], select') && $element.val()) {
        $element.val('');
    }

    //Replace other inputs/elements with new HTML from Ajax results
    if (newContents || newContents == '') {
        $element.empty();
        $element.append(newContents)
    }

    //Reset anything functions running on timeseries tool on load (ie custom date resolver)
    timeseriesTool.refresh();

}