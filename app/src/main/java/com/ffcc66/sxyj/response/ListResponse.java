package com.ffcc66.sxyj.response;

import java.util.List;

public class ListResponse<T> extends Response {
    private static final long serialVersionUID = 2857885317968129959L;

    private List<T> items;

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
