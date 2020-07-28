$(function() {
    var markdownTable = $('.markdown-table-wrap');

    if (markdownTable.length) {
        $('<button class="btn btn--secondary btn--mobile-table-hide">Close table</button>').insertAfter(markdownTable.find('table'));

        $('.btn--mobile-table-show').click(function () {
            $(this).closest('.markdown-table-container').find('.markdown-table-wrap').show();
            $(this).closest('.markdown-table-container').find('.markdown-table-wrap').find('table').attr("tabindex", "0").focus();
        });

        $('.btn--mobile-table-hide').click(function () {
            $(this).closest(markdownTable).css('display', '');
            $(this).closest('.markdown-table-container').find('.btn--mobile-table-show').focus();
        });
    }
});
