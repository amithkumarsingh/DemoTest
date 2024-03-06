package com.whitecoats.model;

public class DashBoardMoreListModel {

    private String iconName;
    private String menuName;
    private int position;
    private int menuId;
    private int hiddenForDoctor;
    private String pageName;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public int getHiddenForDoctor() {
        return hiddenForDoctor;
    }

    public void setHiddenForDoctor(int hiddenForDoctor) {
        this.hiddenForDoctor = hiddenForDoctor;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


}
