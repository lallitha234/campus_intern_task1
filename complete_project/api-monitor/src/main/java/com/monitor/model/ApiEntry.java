package com.monitor.model;
public class ApiEntry {
    private String name, url, description;
    public ApiEntry() {}
    public ApiEntry(String name, String url, String description) {
        this.name = name; this.url = url; this.description = description;
    }
    public String getName()                    { return name; }
    public void   setName(String v)            { this.name = v; }
    public String getUrl()                     { return url; }
    public void   setUrl(String v)             { this.url = v; }
    public String getDescription()             { return description; }
    public void   setDescription(String v)     { this.description = v; }
}
