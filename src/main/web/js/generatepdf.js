/*Based on https://www.3quarterstack.ninja/convert-your-web-pages-to-pdf-files-using-phantomjs.html */

// Inject jQuery, so I can use it later to manipulate my page
phantom.injectJs("../ui/js/lib/jquery.min.js");
var page, system, fs, info, csrftoken, sessionid, categories,
    notification, data, address, output;
// Create a page object
page = require('webpage').create();
// Require the system module so I can read the command line arguments
system = require('system');
// Require the FileSystem module, so I can read the cookie file
// fs = require('fs');
// Read the cookie file and split it by spaces
// Because the way I constructed this file, separate each field using spaces
// info = fs.read('/tmp/cookies.txt').split(' ');
// csrftoken = info[0];
// sessionid = info[1];
// Let's presume categories are the checked check boxes
// categories = info[2];
// Let's presume notification is the clicked radio button
// notification = info[3];
// Create a data object contains the information we gaterhed earlier
// data = {categories: categories, notification: notification};
// Now we can add cookies into phantomjs, so when it renders the page, it
// will have the same permission and data as the current user
// phantom.addCookie({'domain':'localhost', 'name':'csrftoken',
//                    'value': csrftoken});
phantom.addCookie({
    'domain': 'localhost',
    'name': 'onsBetaDisclaimer',
    'value': true
});

// Read the url and output file location from the command line argument
address = system.args[1];
output = system.args[2];

page.settings.userName = "stats";
page.settings.password="Magic5yf&Roundabout";
// Set the page size and orientation
page.paperSize = {
    format: "A4",
    orientation: "portrait",
    margin: {
        left: "2.5cm",
        right: "2.5cm",
        top: "1cm",
        bottom: "1cm"
    },
    footer: {
        height: "0.9cm",
        contents: phantom.callback(function(pageNum, numPages) {
            return "<div style='text-align:center;'><small>" + pageNum +
                " / " + numPages + "</small></div>";
        })
    }
};
page.zoomFactor = 1.5;
page.settings.userAgent = 'WebKit/534.46 Mobile/9A405 Safari/7534.48.3';
page.settings.javascriptEnabled=false;
page.viewportSize = {
    width: 1600,
    height: 900
};


// Now we have everything settled, let's render the page
page.open(address, function(status) {
    if (status !== 'success') {
        // If PhantomJS failed to reach the address, print a message
        console.log('Unable to load the address!');
        phantom.exit();
    } else {
        // If we are here, it means we rendered page successfully
        // Use "evaluate" method of page object to manipulate the web page
        // Notice I am passing the data into the function, so I can use
        // them on the page
        // page.evaluate(function(data) {
        // var checked_categories = data.categories,
        //     clicked_notification = data.notification;
        // Check the check boxes
        // for (var i = 0; i < checked_categories.length; i++) {
        //     $('input[name=category][value=' + this + ']'
        //      ).prop('checked', true);
        // }
        // Click the radio button
        // $('input[name=notification][value=clicked_notification]'
        //  ).trigger('click');
        // Display the header and footer
        // $('.header').show();
        // $('.footer').show();
        // Change some styles
        // $('h1').css('color', 'black');
        // $('.subhead').css('background', 'white');
        // $('body').css('background', 'none');
        // }, data);

        // Now create the output file and exit PhantomJS
        var result = page.render(output);
        phantom.exit();
    }
});