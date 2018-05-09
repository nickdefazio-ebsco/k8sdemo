package com.k8s.demo.title.title;

public class Title {

    private String titleId;
    private String titleName;
    private String titleType;
    
    public Title(){
    	
    }

    public Title(final String titleId, final String titleName, final String titleType){
        this.titleId = titleId;
        this.titleName = titleName;
        this.titleType = titleType;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getTitleName() {
        return titleName;
    }

    public String getTitleType() {
        return titleType;
    }
}