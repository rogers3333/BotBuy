package com.app.botbuy.chat.embedding;

import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.logical.And;
import dev.langchain4j.store.embedding.filter.logical.Or;

public final class FilterParser {

    private FilterParser() {
    }

    public static String parse(Filter filter) {
        return parseFilter(filter);
    }

    private static String parseFilter(Filter filter) {
        if (filter instanceof IsEqualTo isEqualTo) {
            return parseIsEqualTo(isEqualTo);
        }
        if (filter instanceof And and) {
            return parseAnd(and);
        }
        if (filter instanceof Or or) {
            return parseOr(or);
        }
        throw new UnsupportedOperationException("Unsupported filter type: " + filter.getClass());
    }

    private static String parseIsEqualTo(IsEqualTo isEqualTo) {
        return isEqualTo.key() + "=" + String.format("'%s'", isEqualTo.comparisonValue());
    }

    private static String parseAnd(And and) {
        String left = parseFilter(and.left());
        String right = parseFilter(and.right());
        return left + " and " + right;
    }

    private static String parseOr(Or or) {
        String left = parseFilter(or.left());
        String right = parseFilter(or.right());
        return left + " or " + right;
    }
}
