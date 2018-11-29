package android.under_dash.addresses.search.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class Row {
    @JsonProperty("elements")
    private List<Element> elements = null;

    @JsonProperty("elements")
    public List<Element> getElements() {
        return elements;
    }
}