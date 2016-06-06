$(function () {
    if ($('body').hasClass('compendium_landing_page')) {
        $('.js-print-chapters').click(function (e) {
            e.preventDefault();
            addLoadingOverlay();

            var $chapters =  $('.chapter'),
            chapterLength = $chapters.length;

            $chapters.each(function (index) {
                // Synchronously adds div with id to get around Ajax working asynchronously
                $('main').append("<div id='compendium-print" + index + "'></div>");

                var url = $(this).attr('href');

                // Set what content from each page we want to retrieve for printing
                var childIntro = ('.page-intro');
                var childContent = ('.page-content');

                // Get chapter content
                $.get(url, function (response) {
                    // Remove noscript tags around images, they break the charts when requested
                    var html = response.replace(/<\/noscript>/g, '').replace(/<noscript>/g, '');

                    console.log(html);

                    // Add in print page breaks before each chapter and add to compendium landing page
                    var $response = $(html);
                    $response.find(childIntro).addClass('print--break-before').appendTo('#compendium-print' + index);
                    $response.find(childContent).appendTo('#compendium-print' + index).imagesLoaded().then(function() {
                        chaptersComplete(index);
                    });
                });
            });

            // Tally number of chapters complete and print window when done
            function chaptersComplete(index) {
                if (index+1 == chapterLength) {
                    console.log('chapter complete');
                    // window.print();
                    // location.reload();
                }
            }
        });

        // Function to wait until all images are loaded in the DOM - stackoverflow.com/questions/4774746/jquery-ajax-wait-until-all-images-are-loaded
        $.fn.imagesLoaded = function () {

            // get all the images (excluding those with no src attribute)
            var $imgs = this.find('img[src!=""]');
            // if there's no images, just return an already resolved promise
            if (!$imgs.length) {return $.Deferred().resolve().promise();}

            // for each image, add a deferred object to the array which resolves when the image is loaded (or if loading fails)
            var dfds = [];
            $imgs.each(function(){

                var dfd = $.Deferred();
                dfds.push(dfd);
                var img = new Image();
                img.onload = function(){dfd.resolve();}
                img.onerror = function(){dfd.resolve();}
                img.src = this.src;

            });

            // return a master promise object which will resolve when all the deferred objects have resolved
            // IE - when all the images are loaded
            return $.when.apply($,dfds);

        };
    }
});