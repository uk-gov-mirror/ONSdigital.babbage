// global vars
// ---------------

var CookieUtils = {
  getCookieValue: function (a, b) {
    b = document.cookie.match('(^|;)\\s*' + a + '\\s*=\\s*([^;]+)');
    return b ? b.pop() : '';
  }
};







// function calls
// ---------------

addPrototypeModal();
addLoadingOverlay();






// functions
// ---------------

function addLoadingOverlay() {
    var body = document.getElementsByTagName('body')[0];
    var overlay = document.createElement('div');
    overlay.id = 'loading-overlay';
    overlay.style['width'] = '100%';
    overlay.style['height'] = '100%';
    overlay.style['position'] = 'fixed';
    overlay.style['display'] = 'table';
    overlay.style['top'] = '0';
    overlay.style['bottom'] = '0';
    overlay.style['left'] = '0';
    overlay.style['right'] = '0';
    overlay.style['background'] = '#ffffff';
    overlay.style['z-index'] = '99995';
    overlay.style['right'] = '0';

    var loader = document.createElement('div');
    loader.innerHTML = '<p>Loading...</p>';
    loader.className = 'loader print-hidden';

    overlay.appendChild(loader);
    body.appendChild(overlay);
}



function addPrototypeModal() {

    // console.log(CookieUtils.getCookieValue('onsBetaDisclaimer'));

    var body = document.getElementsByTagName('body')[0];
    var overlay = document.createElement('div');
    overlay.id = 'modal-overlay';
    overlay.style['width'] = '100%';
    overlay.style['height'] = '100%';
    overlay.style['position'] = 'fixed';
    overlay.style['display'] = 'table';
    overlay.style['top'] = '0';
    overlay.style['bottom'] = '0';
    overlay.style['left'] = '0';
    overlay.style['right'] = '0';
    overlay.style['background'] = 'rgba(255,255,255,0.95)';
    overlay.style['z-index'] = '99990';
    overlay.style['right'] = '0';

    var modal = document.createElement('div');
    modal.innerHTML = '<div><header><h1>ONS Beta <span class="modal-beta">&beta;</span</h1></header><section><p>Welcome to an experimental prototype (beta) for the Office for National Statistics website.</p><p>PLEASE BE AWARE – this is a test website. It may contain inaccuracies or be misleading.</p><p><a href="http://www.ons.gov.uk" title="ONS web site">www.ons.gov.uk</a> remains the official website for ONS information.</p><p>Your suggestions will help us make this site better, so if you have any comments please send us <a href="" onclick="_bugHerd.win.bugherd.applicationView.anonymousbar.toggleOptions()" title="Feedback">feedback</a>.</p><ul class="modal-nav"><li class="float-left"><a class="btn-modal-cancel" href="http://www.ons.gov.uk">Cancel</a></li><li class="float-right"><a href="#" class="btn-modal-continue">Proceed</a></li></ul></section></div>';
    modal.className = 'prototype-modal print-hidden';

    if(!CookieUtils.getCookieValue('onsBetaDisclaimer')) {
        // console.log('no cookie');

        overlay.appendChild(modal);
        body.appendChild(overlay);
    } else {
        // console.log('has cookie'); 
        try {
            body.removeChild(overlay);
        }catch(err) {
            // console.log(err);
            // console.log('tried removing #modal-overlay but not in dom to remove :)');
        }
    }
}


// moved function to js-enhance to make use of jquery fade functionality
// function acceptBetaDiscalimer() {
//     document.cookie='onsBetaDisclaimer=true';

//     var body = document.getElementsByTagName('body')[0];
//     var overlay = document.getElementById('modal-overlay');
    
//     body.removeChild(overlay);
// }



