package com.progmatic.progmappbe.helpers;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.dozer.CustomConverter;

public class MarkdownHelper implements CustomConverter {

    public static String markDownToHTMLSafe(String markdownString){
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownString);
        HtmlRenderer renderer = HtmlRenderer
                .builder()
                .escapeHtml(true)
                .sanitizeUrls(true)
                .build();
        String html = renderer.render(document);
        return html;
    }


    @Override
    public Object convert(Object existingDestinationFieldValue, Object sourceFieldValue, Class<?> destinationClass, Class<?> sourceClass) {
        if(sourceFieldValue == null){
            return null;
        }
        if(!(sourceFieldValue instanceof String)){
            return null;
        }
        String from = (String) sourceFieldValue;
        return markDownToHTMLSafe(from);
    }
}
