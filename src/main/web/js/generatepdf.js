/*Based on https://www.3quarterstack.ninja/convert-your-web-pages-to-pdf-files-using-phantomjs.html */

var page, system, address, output;

//Below page size configration based on http://stackoverflow.com/questions/22017746/while-rendering-webpage-to-pdf-using-phantomjs-how-can-i-auto-adjust-my-viewpor
var pageSize = "A4",
    pageOrientation = "portrait",
    dpi = 98, //from experimenting with different combinations of viewportSize and paperSize the pixels per inch comes out to be 150
    pdfViewportWidth = 595,
    pdfViewportHeight = 842,
    cmToInchFactor = 0.393701,
    widthInInches,
    heightInInches,
    temp;

switch (pageSize) {
    case 'Letter':
    default:
        widthInInches = 8.5;
        heightInInches = 11;
        break;
    case 'Legal':
        widthInInches = 8.5;
        heightInInches = 14;
        break;
    case 'A3':
        widthInInches = 11.69
        heightInInches = 16.54;
        break;
    case 'A4':
        widthInInches = 8.27;
        heightInInches = 11.69;
        break;
    case 'A5':
        widthInInches = 5.83;
        heightInInches = 8.27;
        break;
    case 'Tabloid':
        widthInInches = 11;
        heightInInches = 17;
        break;
}

//reduce by the margin (assuming 1cm margin on each side)
widthInInches -= 2 * cmToInchFactor;
heightInInches -= 2 * cmToInchFactor;

//interchange if width is equal to height
if (pageOrientation === 'Landscape') {
    temp = widthInInches;
    widthInInches = heightInInches;
    heightInInches = temp;
}

//calculate corresponding viewport dimension in pixels
pdfViewportWidth = dpi * widthInInches;
pdfViewportHeight = dpi * heightInInches;


// Create a page object
page = require('webpage').create();
page.paperSize = {
    format: pageSize,
    orientation: pageOrientation,
    margin: {
        top: '1cm',
        bottom: '1cm'
    }
};
page.viewportSize = {
    width: pdfViewportWidth,
    height: pdfViewportHeight
};
// Require the system module so I can read the command line arguments
system = require('system');

// Read the url and output file location from the command line argument
address = system.args[1];
output = system.args[2];
for (var i = system.args.length - 1; i > 2; i=i-2) {
    var cookie = system.args[i-1];
    var value = system.args[i];
    
    if(cookie === 'jsEnhanced'){
        continue;
    }
    
    phantom.addCookie({
        'domain': 'localhost',
        'name': cookie,
        'value': value
    });
}

page.settings.userAgent = 'WebKit/534.46 Mobile/9A405 Safari/7534.48.3';
page.settings.javascriptEnabled = false;
// Now we have everything settled, let's render the page
page.open(address, function(status) {
    if (status !== 'success') {
        // If PhantomJS failed to reach the address, print a message
        console.log('Unable to load the address!');
        phantom.exit();
    } else {
        // Now create the output file and exit PhantomJS
        var result = page.render(output);
        phantom.exit();
    }
});