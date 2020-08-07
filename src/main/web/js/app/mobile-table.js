$(function() {
    var markdownTable = $('.markdown-table-wrap');

    if (markdownTable.length) {
        $('<button class="btn btn--secondary btn--mobile-table-hide" aria-expanded="false" aria-hidden="true" aria-live="assertive">Close table</button>').insertAfter(markdownTable.find('table'));

        var showButtons = $('.btn--mobile-table-show');
        var hideButtons = $('.btn--mobile-table-hide');
        showButtons.click(function () {
            $(this).attr('aria-hidden', true);
            $(this).attr('aria-expanded', true);
            hideButtons.attr('aria-hidden', false);
            hideButtons.attr('aria-expanded', true);
            $(this).closest('.markdown-table-container').find('.markdown-table-wrap').show();
            $(this).closest('.markdown-table-container').find('.markdown-table-wrap').find('table').attr("tabindex", "0").focus();
        });

        hideButtons.click(function () {
            $(this).attr('aria-hidden', true);
            $(this).attr('aria-expanded', false);
            showButtons.attr('aria-hidden', false);
            showButtons.attr('aria-expanded', false);
            $(this).closest(markdownTable).css('display', '');
            $(this).closest('.markdown-table-container').find('.btn--mobile-table-show').focus();
        });
    }
});
