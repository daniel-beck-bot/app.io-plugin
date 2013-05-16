package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement
public class KickfolioVersionObject {

    private String id = null;
    @Expose
    private String app_id = null;
    private String state = null;
    @Expose
    private String bundle_url = null;
    private String encrypted_bundle_url = null;
    private String version_string = null;
    private String default_image = null;
    private String default_image_landscape = null;
    private String has_job_id = null;
    private String created_at = null;
    private String updated_at = null;
    private String[] comment_ids;

    public String getId() {
        return id;
    }

    public String getApp_id() {
        return app_id;
    }

    public String getState() {
        return state;
    }

    public String getBundle_url() {
        return bundle_url;
    }

    public String getEncrypted_bundle_url() {
        return encrypted_bundle_url;
    }

    public String getVersion_string() {
        return version_string;
    }

    public String getDefault_image() {
        return default_image;
    }

    public String getDefault_image_landscape() {
        return default_image_landscape;
    }

    public String getHas_job_id() {
        return has_job_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String[] getComment_ids() {
        return comment_ids;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public void setBundle_url(String bundle_url) {
        this.bundle_url = bundle_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setEncrypted_bundle_url(String encrypted_bundle_url) {
        this.encrypted_bundle_url = encrypted_bundle_url;
    }

    public void setVersion_string(String version_string) {
        this.version_string = version_string;
    }

    public void setDefault_image(String default_image) {
        this.default_image = default_image;
    }

    public void setDefault_image_landscape(String default_image_landscape) {
        this.default_image_landscape = default_image_landscape;
    }

    public void setHas_job_id(String has_job_id) {
        this.has_job_id = has_job_id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setComment_ids(String[] comment_ids) {
        this.comment_ids = comment_ids;
    }
}
