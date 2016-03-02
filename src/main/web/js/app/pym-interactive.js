
$(function() {
    $('div.pym-interactive').each(function(index, element) {
        new pym.Parent($(element).attr('id'), $(element).data('url'));
    });
});
