//progressive enhancement (jQuery)

// $(function() {
jQuery(window).load(function() {

    var browserNotSupported = (function () {
        var div = document.createElement('DIV');
        // http://msdn.microsoft.com/en-us/library/ms537512(v=vs.85).aspx
        div.innerHTML = '<!--[if lte IE 8]><I></I><![endif]-->';
        return div.getElementsByTagName('I').length > 0;
    }());
    if (browserNotSupported) {
        setTimeout(function() {
            $('#loading-overlay').fadeOut(300);
        }, 500);
    } else {
        $('body').append('<script type="text/javascript" src="/js/third-party/pym.min.js"></script>');
        jsEnhance();
    }

    

    function jsEnhance() {
        $('.js-enhance--show').show();
        $('.js-enhance--hide').hide();
        $('.nojs-hidden').removeClass('nojs-hidden');

        //The order of these functions being called is important...
        jsEnhanceULNavToSelectNav();
        jsEnhanceHome();
        jsEnhanceLinechart();
        jsEnhancePrint();
        jsEnhanceNumberSeparator();
        jsEnhanceMarkdownCharts();
        jsEnhanceMarkdownTables();
        jsEnhancePrintCompendium();
        jsEnhanceBoxHeight();
        jsEnhanceBoxHeightResize();
        jsEnhanceDownloadAnalytics();
        
        prototypeModalButtons();

        setTimeout(function() {
            jsEnhanceIframedTables();
            jsEnhanceMobileTables();
        }, 400);


        setTimeout(function() {
            $('#loading-overlay').fadeOut(300);
        }, 500);
    }

    function jsEnhanceULNavToSelectNav() {
        $('.js-enhance--ul-to-select').each(function() {
            var labeltext = $('p:first', this).text();
            var selectoptions = $('ul:first li a', this);


            //IE9 dosent like this...
            // var label = $('<label>', {
            //     class: 'definition-emphasis',
            //     text: labeltext
            // });
            var label = $(document.createElement('label'));
            label.attr('class', 'definition-emphasis');
            label.attr('text', labeltext);



            //$(document.createElement('select')) is faster
            // var newselect = $('<select>', {
            //     class: 'field field--spaced'
            // });
            var newselect = $(document.createElement('select'));
            newselect.attr('class', 'field field--spaced');


            newselect.append($('<option>', {
                value: '',
                text: 'Select a related time series'
            }));

            newselect.change(function() {
                var location = $(this).find('option:selected').val();
                if (location) {
                    window.location = location;
                }
            });

            $.each(selectoptions, function(i, item) {
                newselect.append($('<option>', {
                    value: $(this).attr('href'),
                    text: $(this).text()
                }));
            });

            label.append(newselect);

            $(this).html(label);

        });
    }

    function jsEnhanceHome() {

        var herostatarea = $('.stat__wrap--home');


        $(herostatarea).click(function() {
            var herostatarealink = $('a:last', this).attr('href');

            window.location = herostatarealink;
        });

        $(herostatarea).css({
            'cursor': 'pointer'
        });

        $(herostatarea).hover(function() {
            $(this).css({
                'background-color': '#f8fadc'
            })
        }, function() {
            $(this).css({
                'background-color': 'transparent'
            });
        });

    }


    function jsEnhanceLinechart() {

        var chartContainer = $("[data-chart]");
        if (!chartContainer.length) {
            return;
        }

        var location = stripTrailingSlash(window.location.pathname) + "/data";
        // console.debug("Downloading timseries data from " + location)

        $.getJSON(location, function(timeseries) {
            // console.log("Successfuly read timseries data");
            linechart = linechart(timeseries); //Global variable

        }).fail(function(d, textStatus, error) {
            // console.error("Failed reading timseries, status: " + textStatus + ", error: " + error)
        });
    }

    function stripTrailingSlash(str) {
        if(str.substr(-1) === '/') {
            return str.substr(0, str.length - 1);
        }
        return str;
    }

    function jsEnhanceMarkdownCharts() {

        var chartContainer = $(".markdown-chart-container");
        if (!chartContainer.length) {
            return;
        }

        chartContainer.each(function() {
            var $this = $(this);
            var uri = $this.attr('id');
            $this.empty();

            if (uri.indexOf('/') !== 0) {
                uri = '/' + uri;
            }

            new pym.Parent(uri, uri + "/chart", {});
        });
    }

    function jsEnhancePrint() {
    $('#jsEnhancePrint').click(function()
        {
            window.print();
            return false;
        });
    }


    function jsEnhanceNumberSeparator() {
      // Adapted from http://stackoverflow.com/questions/14075014/jquery-function-to-to-format-number-with-commas-and-decimal
      $( ".stat__figure-enhance" ).each(function( index ) {
        //console.log( index + ": " + $( this ).text() );
        var number = $( this ).text();
        var n= number.toString().split(".");
        //Comma-fies the first part
        n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        // //Combines the two sections
        $( this ).text(n.join("."));
      });
    }

    function jsEnhanceMarkdownTables() {

        var chartContainer = $(".markdown-table-container");
        if (!chartContainer.length) {
            return;
        }

        chartContainer.each(function() {
            var $this = $(this);
            var uri = $this.attr('id');
            $this.empty();

            if (uri.indexOf('/') !== 0) {
                uri = '/' + uri;
            }

            new pym.Parent(uri, uri + "/table", {});
        });
    }

    function jsEnhancePrintCompendium() {      
        $('#jsEnhancePrintCompendium').click(function(e) {
            addLoadingOverlay();

            // TODO Will get function to add in div, so no empty divs on page at load
            //$("<div class='print-content'></div>").insertAfter('.desktop-grid-full-width');

            // TODO Remove existing page content from print
            // $(".wrapper").remove();

            $('.chapter').each(function() {
                var url = $(this).attr('href');
                
                var getContent = ('main');
                
                $('<section>').load(url, function() {
                    $('.print-content').append("<div class='print__break-after'>" + $(this).find(getContent).html() + "</div>");
                });
                
                e.preventDefault();
            });

            $(document).ajaxStop(function() {
                window.print();
                location.reload();
            });
        });
    }

    function jsEnhanceBoxHeight() {
        if ($(window).width() > 608) {
            var highestBox = 0;
            $('.equal-height').each(function(){
            
                if($(this).height() > highestBox) {
                   highestBox = $(this).height(); 
               }
            });  
            
            $('.equal-height').height(highestBox);
        }
    }

    function jsEnhanceBoxHeightResize() {
        $( window ).resize(function() {
            $('.equal-height').height('auto');
            jsEnhanceBoxHeight();
        });
    }

    function jsEnhanceIframedTables() {
        $('iframe').each(function(i) {
            // markdown-table-container
            if($(this).contents().find('div').hasClass('markdown-table-container')) {
                // console.log('iframe with table found');
                $(this).contents().find('iframe').remove();
                $(this).contents().find('script').remove();
                $(this).contents().find('*').css('width','auto');
                $(this).contents().find('*').css('height','auto');

                // var iframedtable =  $(this).contents().find('table');

                var iframecontent = $(this).contents().find('body').html();

                // $('<div class="table-wrapper">' + iframecontent + '</div>').insertAfter($(this));
                $(iframecontent).insertAfter($(this));
                $(this).remove();
             }
        });
    }

    function jsEnhanceMobileTables() {
        //<span class=" icon-table" role="presentation"></span>
        // $('markdown-table-container').addClass('table-holder-mobile');

        $('<button class="invisible btn btn--mobile-table-show">View table</button>').insertAfter($('.markdown-table-wrap'));
        $('<button class="invisible btn btn--mobile-table-hide">Close table</button>').insertAfter($('.markdown-table-wrap table'));
        
        $('.btn--mobile-table-show').click(function(e) {
            // console.log($(this).closest('.markdown-table-container').find('.markdown-table-wrap'));
            $(this).closest('.markdown-table-container').find('.markdown-table-wrap').show();       
        });

        $('.btn--mobile-table-hide').click(function(e) {
            // console.log($(this).closest('.markdown-table-container').find('.markdown-table-wrap'));
            $(this).closest('.markdown-table-wrap').hide();       
        });
    }

    
    /*Track file downloads*/
    function jsEnhanceDownloadAnalytics() {
        $('.download-analytics').click(function(){
            var path = window.location;
            var downloadType = $(this).attr('action');
            
            ga('send', 'pageview', {
                'page': path + downloadType,
                'hitCallback': function() {
                    if(console && console.log) {
                        console.log('event triggered for ' + path + downloadType);
                    }
                }
            });
        });
    }


    function prototypeModalButtons() {
        $('.btn-modal-continue').click(function(e){
            e.preventDefault();
            var d = new Date();
            d.setTime(d.getTime() + (1*24*60*60*1000));
            var expires = "expires="+d.toUTCString();
            document.cookie='onsBetaDisclaimer=true; ' + expires;
            $('#modal-overlay').fadeOut(300);
        });
    }

    // 

    
});