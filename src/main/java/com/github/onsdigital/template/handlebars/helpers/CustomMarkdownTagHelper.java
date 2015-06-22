package com.github.onsdigital.template.handlebars.helpers;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomMarkdownTagHelper extends com.github.jknack.handlebars.MarkdownHelper {

    private static ChartTagReplacer chartTagReplacer = new ChartTagReplacer();
    private static TableTagReplacer tableTagReplacer = new TableTagReplacer();

    public CharSequence apply(Object context, Options options) throws IOException {
        String result = super.apply(context, options).toString();

        if (context == null) {
            return result;
        }

        result = replaceCustomTags(result, chartTagReplacer);
        result = replaceCustomTags(result, tableTagReplacer);

        return new Handlebars.SafeString(result);
    }

    /**
     * Applies a TagReplacementStrategy to a given input.
     *
     * @param input
     * @param replacementStrategy
     * @return
     */
    public static String replaceCustomTags(String input, TagReplacementStrategy replacementStrategy) {

        Matcher matcher = replacementStrategy.getPattern().matcher(input);

        StringBuffer result = new StringBuffer(input.length());
        while (matcher.find()) {
            matcher.appendReplacement(result, replacementStrategy.replace(matcher));
        }

        matcher.appendTail(result);

        return result.toString();
    }

    public static void main(String[] args) {

        String input = "<p><ons-chart path=\"FirstPath\" />  <ons-chart path=\"SecondPath\" />  <ons-chart path=\"businessindustryandtrade/businessactivitysizeandlocation/articles/chartandtabletest/20150609/table1\" /></p><p><ons-table path=\"businessindustryandtrade/businessactivitysizeandlocation/articles/chartandtabletest/20150609/table1\" /></p>";

        Pattern regex = Pattern.compile("<ons-chart\\spath=\"([-A-Za-z0-9+&@#/%?=~_|!:,.;()*$]+)\"?\\s?/>");
        Matcher matcher = regex.matcher(input);

        StringBuffer result = new StringBuffer(input.length());
        while (matcher.find()){
            System.out.println(matcher.group(1));
            matcher.appendReplacement(result, "**" + matcher.group(1) + "**");
        }

        matcher.appendTail(result);

        System.out.println(result.toString());
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