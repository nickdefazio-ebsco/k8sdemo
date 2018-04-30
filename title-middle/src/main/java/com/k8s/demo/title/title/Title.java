package com.k8s.demo.title.title;


public class Title {

    private final String titleId;
    private final String titleName;

    public Title(final String titleId, final String titleName){
        this.titleId = titleId;
        this.titleName = titleName;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getTitleName() {
        return titleName;
    }
}
