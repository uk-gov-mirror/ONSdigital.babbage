package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.onsdigital.content.page.base.Page;

import java.io.IOException;

public class MarkdownHelper extends com.github.jknack.handlebars.MarkdownHelper {

    public CharSequence apply(Object context, Options options) throws IOException {
        String result = super.apply(context, options).toString();

        String path = ((Page) options.context.parent().model()).uri.toString();

        result = result.replaceAll("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>", "chart:" + path + ".../$1");
        result = result.replaceAll("<ons-table\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>", "table:" + path + ".../$1");


        return new Handlebars.SafeString(result);
    }

    public static void main(String[] args) {

        String input = "<p><ons-chart path=\"businessindustryandtrade/businessactivitysizeandlocation/articles/chartandtabletest/20150609/ae0005fa\" /></p><p><ons-table path=\"businessindustryandtrade/businessactivitysizeandlocation/articles/chartandtabletest/20150609/table1\" /></p>";

        String result = input.replaceAll("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>", "chart:$1");

        System.out.println(new Handlebars.SafeString(result));
    }
}


//htmlText = htmlText.replace(/(<ons-chart\spath="[-A-Za-z0-9+&@#\/%?=~_|!:,.;\(\)*[\]$]+"?\s?\/>)/ig, function(match) {
//        var path = $(match).attr('path');
//
//        //var output = '<div class="chart-container"><iframe frameBorder ="0" scrolling = "no" src="http://localhost:8081/florence/chart.html?path=' + path + '.json"></iframe></div>';
//        var output = '<div id="' + path + '"></div><script>new pym.Parent("' + path + '", "/florence/chart.html?path=' + path + '.json", {})</script>';
//        //console.log(output);
//        return output; //'[chart path="' + path + '" ]';
//        });
//
//        htmlText = htmlText.replace(/(<ons-table\spath="[-A-Za-z0-9+&@#\/%?=~_|!:,.;\(\)*[\]$]+"?\s?\/>)/ig, function(match) {
//        var path = $(match).attr('path');
//
//        //var output = '<div class="chart-container"><iframe frameBorder ="0" scrolling = "no" src="http://localhost:8081/florence/chart.html?path=' + path + '.json"></iframe></div>';
//        var output = '<div id="' + path + '"></div><script>new pym.Parent("' + path + '", "/florence/table.html?path=' + path + '.xls", {})</script>';
//        //console.log(output);
//        return output; //'[chart path="' + path + '" ]';
//        });