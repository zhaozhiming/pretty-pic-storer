package com.github.pps.dto;

import javax.persistence.*;

@Entity(name = "takes")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private String uid;

    @Basic
    private String status;

    @Basic
    private String url;

    @Basic
    private Long createdAt;

    public Task(String uid, String status, Long createdAt) {
        this.uid = uid;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Task() {
    }

    public String getStatus() {
        return status;
    }

    public String getUrl() {
        return url;
    }

    public Long getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getCreatedAt() {
        return createdAt;
    }
}
