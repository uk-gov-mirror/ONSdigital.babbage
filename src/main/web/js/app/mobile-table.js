$(function() {
    var markdownTable = $('.markdown-table-wrap');

    if (markdownTable.length) {
        $('<button class="btn btn--secondary btn--mobile-table-show">View table</button>').insertAfter(markdownTable);
        $('<button class="btn btn--secondary btn--mobile-table-hide">Close table</button>').insertAfter(markdownTable.find('table'));

        $('.btn--mobile-table-show').click(function () {
            $(this).closest('.markdown-table-container').find('.markdown-table-wrap').show();
        });

        $('.btn--mobile-table-hide').click(function () {
            $(this).closest('.markdown-table-wrap').css('display', '');
        });
    }
});
