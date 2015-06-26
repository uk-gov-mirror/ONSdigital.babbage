//progressive enhancement (jQuery)

// $(function() {
jQuery(window).load(function() {
    jsEnhance();

    function jsEnhance() {
        $('.js-enhance--show').show();
        $('.js-enhance--hide').hide();
        $('.nojs-hidden').removeClass('nojs-hidden');

        jsEnhanceULNavToSelectNav();
        jsEnhanceHome();
        jsEnhanceLinechart();
        jsEnhancePrint();
        jsEnhanceMarkdownCharts();
        jsEnhanceMarkdownTables();

        setTimeout(function() {
            $('#loading-overlay').fadeOut(300);
        }, 500);
    }

    function jsEnhanceULNavToSelectNav() {
        $('.js-enhance--ul-to-select').each(function() {
            var labeltext = $('p:first', this).text();
            var selectoptions = $('ul:first li a', this);

            var label = $('<label>', {
                class: 'definition-emphasis',
                text: labeltext
            });

            //$(document.createElement('select')) is faster
            var newselect = $('<select>', {
                class: 'field field--spaced'
            });

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

        var location = window.location.pathname + "/data";
        console.debug("Downloading timseries data from " + location)

        $.getJSON(location, function(timeseries) {
            console.log("Successfuly read timseries data");
            linechart = linechart(timeseries); //Global variable

        }).fail(function(d, textStatus, error) {
            console.error("Failed reading timseries, status: " + textStatus + ", error: " + error)
        });
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
});