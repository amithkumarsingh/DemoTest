package com.whitecoats.clinicplus.models;

public class DashboardMenuModel {

    private String pageName;
    private int pageId;
    private int pageIcon;
    private int isHidden;
    private String url;

    public int getIsHidden() {
        return isHidden;
    }

    public int getPageId() {
        return pageId;
    }

    public int getPageIcon() {
        return pageIcon;
    }

    public String getPageName() {
        return pageName;
    }

    public String getUrl() {
        return url;
    }

    public void setIsHidden(int isHidden) {
        this.isHidden = isHidden;
    }

    public void setPageIcon(int pageIcon) {
        this.pageIcon = pageIcon;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "DashboardMenuModel{" +
                "pageName='" + pageName + '\'' +
                ", pageId=" + pageId +
                ", pageIcon=" + pageIcon +
                ", isHidden=" + isHidden +
                ", url='" + url + '\'' +
                '}';
    }
}

