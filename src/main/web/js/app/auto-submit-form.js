
function autoSubmitForm() {
    //Function to submit form
    function submitForm(element) {
        var elementForm = $(element).closest(form);
        $(elementForm).trigger('submit');
    }

    //Variables
    var form = '.js-auto-submit__form',
        input = '.js-auto-submit__input',
        $keywordSearch = $('input[type="search"].js-auto-submit__input'),
        $selectUpdated = $('#select-updated'),
        url,
        timer,
        $trigger,
        scrollToTop;

    // Hide submit button
    $('.js-submit-button').hide();

    // Keyword search auto-submit
    $keywordSearch.on('keyup', function (e) {
        if (!(e.keyCode == '9')) { // Don't submit on tab
            // Timed to allow for typing to finish
            var $this = $(this);
            clearTimeout(timer);
            timer = setTimeout(function () {
                submitForm($this);
            }, 500);
        }
    });
    $(form).on('paste search', $keywordSearch, function (e) {
        // Submit instantly on paste/clear
        var $this = $(this);
        submitForm($this);
    });

    // Auto-submit instantly for all other elements
    $(form).on('change', input, function (e) {
        var $target = $(e.target);
        var $targetId = $target.attr('id');
        $trigger = $target;
        if ($targetId !== $keywordSearch.attr('id') && $targetId !== 'select-updated' && $targetId !== 'page-size' && !$target.hasClass('js-auto-submit__input--date')) { //Don't submit again after keyword, select update date, page results size or date input change
            submitForm($target);
        } else if ($target.hasClass('js-auto-submit__input--date')) {
            // Only submit form if all of date inputs contained a value
            var dateType = $target.closest('#inputs-start-date').length ? "start" : "end",
                emptyInputs = 0;
            $('#inputs-' + dateType + '-date input').each(function() {
                if ($(this).val() === "") {
                    emptyInputs += 1;
                }
            });
            if (emptyInputs === 1 || emptyInputs === 2 ) {
                return false;
            } else {
                submitForm($target);
            }
        } else if ($targetId == 'page-size') {
            scrollToTop = true;
            submitForm($target);
        } else if ($targetId == $selectUpdated.attr('id')) { //Clear custom dates on timeseries tool if 'Custom' not selected
            if ($selectUpdated.val() != 'custom') {
                $('#inputs-start-date input, #inputs-end-date input').each(function () {
                    $(this).val('');
                });
            }
            submitForm($target)
        }
    });

    //Bind form submission to store form data and run ajax function
    $(form).submit(function (e) {
        e.preventDefault();
        url = (window.location.pathname) + '?' + $(input).serialize();
        loadNewResults(url, $trigger);
        if (scrollToTop) { // used only for 'result per page' select to scroll to top on change
            $('html, body').animate({scrollTop: $('#main').offset().top}, 1000);
        }
        $trigger = undefined; // reset the focus element
        scrollToTop = false; // reset flag for scrolling on change
        return false;
    });

}


$(function() {
    if (!$('body').hasClass('viewport-sm')) { // on medium viewport and up auto-submit filters form
        autoSubmitForm();
    }
});