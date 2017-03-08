$(function() {    
    // if on mobile inject overlay and button elements
    if ($("body").hasClass("viewport-xs")) {
        var markdownChart = $('.markdown-chart');

        if (markdownChart.length) {
            $('<div class="markdown-chart-overlay"></div>').insertAfter($('.markdown-chart'));
            $('<button class="btn btn--mobile-chart-show">View chart</button>').insertAfter($('.markdown-chart'));
            $('<button class="btn btn--mobile-chart-hide">Close chart</button>').appendTo($('.markdown-chart-overlay'));

            $('.btn--mobile-chart-show').click(function () {
                // the variables
                var $this = $(this),
                    $title = $('<span class="font-size--h4">' + $this.closest('.markdown-chart-container').find('h4').text() + '</span>'),
                    $imgSrc = $this.closest('.markdown-chart-container').find('.js-chart-image-src').attr('href'),
                    width = 700,
                    $img = '<img src="' + $imgSrc + '&width=' + width + '" />',
                    $overlay = $this.closest('.markdown-chart-container').find('.markdown-chart-overlay');

                // check if image has been injected already
                if (!$overlay.find('img').length) {
                    $overlay.append($title);
                    $overlay.append($img);
                }

                // show the overlay
                $overlay.show();
            });

            $('.btn--mobile-chart-hide').click(function () {
                $(this).closest('.markdown-chart-overlay').css('display', '');
            });
        }
    } 
});