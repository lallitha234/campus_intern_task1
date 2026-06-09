package com.monitor.model;
public class ApiStatusResult {
    private String name, url, description, status, errorMessage, checkedAt;
    private long responseTimeMs;
    private int httpStatusCode;
    public String getName()               { return name; }
    public void   setName(String v)       { name = v; }
    public String getUrl()                { return url; }
    public void   setUrl(String v)        { url = v; }
    public String getDescription()        { return description; }
    public void   setDescription(String v){ description = v; }
    public String getStatus()             { return status; }
    public void   setStatus(String v)     { status = v; }
    public String getErrorMessage()       { return errorMessage; }
    public void   setErrorMessage(String v){ errorMessage = v; }
    public String getCheckedAt()          { return checkedAt; }
    public void   setCheckedAt(String v)  { checkedAt = v; }
    public long   getResponseTimeMs()     { return responseTimeMs; }
    public void   setResponseTimeMs(long v){ responseTimeMs = v; }
    public int    getHttpStatusCode()     { return httpStatusCode; }
    public void   setHttpStatusCode(int v){ httpStatusCode = v; }
    public boolean isUp()                 { return "UP".equals(status); }
}
