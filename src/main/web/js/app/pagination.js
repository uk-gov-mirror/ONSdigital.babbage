// Ajax in new results when pagination clicked

$(function() {
    var $container = $('#js-pagination-container');
    
    if ($container.length > 0) {
        //Bind pagination link click event
        $container.on('click', 'a.page-link', function(e) {
            e.preventDefault();
            var url = $(e.target).attr('href');
            $('html, body').animate({scrollTop: $('#main').offset().top}, 1000);
            loadNewResults(url);
        });
    }

    // if ($container.length) {
    //     $links.click(function(e) {
    //         e.preventDefault();
    //         var $this = $(this),
    //             $activeLink = $('.btn--plain-active');
    //         if (!$this.hasClass(activeClass) && !$this.hasClass('page-link--prev') && !$this.hasClass('page-link--next')) {
    //             // If a page number then remove active class from current anchor set clicked that anchor to active
    //             $activeLink.removeClass(activeClass);
    //             $this.addClass(activeClass);
    //         } else if ($this.hasClass('page-link--prev')) {
    //             // If previous set the anchor before the current active one to be active
    //             console.log('prev');
    //         } else if ($this.hasClass('page-link--next')) {
    //             // If next set the anchor after the current active one to be active
    //             console.log();
    //             $activeLink.removeClass(activeClass);
    //             $activeLink.parent().next().find('.page-link').addClass(activeClass);
    //         }
    //     });
    // }
});
