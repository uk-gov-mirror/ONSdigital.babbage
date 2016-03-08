$(function() {
    if ($('body').hasClass('compendium_landing_page')) {
        $('.js-print-chapters').click(function (e) {
            addLoadingOverlay();

            $('.chapter').each(function (index) {
                // Synchronously adds div with id to get around Ajax working asynchronously
                $('main').append("<div id='compendium-print" + index + "'></div>");

                var url = $(this).attr('href');

                // Set what content from each page we want to retrieve for printing
                var childIntro = ('.page-intro');
                var childContent = ('.page-content');

                $.get(url, function (data) {
                    $(data).find(childIntro).addClass('print--break-before').appendTo('#compendium-print' + index);
                    $(data).find(childContent).appendTo('#compendium-print' + index);
                });


                e.preventDefault();

            });

            $(document).ajaxStop(function () {
                window.print();
                location.reload();
            });
        });
    }
});
