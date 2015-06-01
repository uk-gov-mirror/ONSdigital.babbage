package com.github.onsdigital.json;

/**
 * Enumerates the different types of content on the website.
 * <p>
 * Strictly these would be uppercase, but "shouty caps" looks wrong when
 * serialised to Json. There are ways around it, but the simplest solution is to
 * use lowercase - it's not worth the complexity.
 *
 * @author david
 */
public enum ContentType {
    home, landing, product, bulletin, article, timeseries, compendium, staticpage, methodology, qmi, foi, adhoc, dataset, list, result, calendar, version, release, unknown,;
}
