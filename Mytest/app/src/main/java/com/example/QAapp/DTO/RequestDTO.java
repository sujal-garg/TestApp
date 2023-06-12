package com.example.QAapp.DTO;

import android.content.pm.PackageInfo;

import java.util.List;

import lombok.Data;

@Data
public class RequestDTO {

    private String udid;

    private List<PackageInfo> installedPackages;

    private Integer galleryImageCount;

    public String getUdid() {
        return udid;
    }

    public void setUdid(String udid) {
        this.udid = udid;
    }

    public List<PackageInfo> getInstalledPackages() {
        return installedPackages;
    }

    public void setInstalledPackages(List<PackageInfo> installedPackages) {
        this.installedPackages = installedPackages;
    }

    public Integer getGalleryImageCount() {
        return galleryImageCount;
    }

    public void setGalleryImageCount(Integer galleryImageCount) {
        this.galleryImageCount = galleryImageCount;
    }

}
