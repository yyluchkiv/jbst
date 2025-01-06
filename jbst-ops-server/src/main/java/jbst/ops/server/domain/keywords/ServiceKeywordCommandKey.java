package jbst.ops.server.domain.keywords;

import jbst.ops.server.properties.atomics.Service;

public record ServiceKeywordCommandKey(
        Service service,
        KeywordCommand keywordCommand
) {
}
