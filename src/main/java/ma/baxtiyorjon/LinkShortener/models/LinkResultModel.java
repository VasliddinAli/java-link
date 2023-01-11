package ma.baxtiyorjon.LinkShortener.models;

public class LinkResultModel {

    private Boolean success;

    private String fullLink;

    private String shortLink;

    private String linkKey;

    public LinkResultModel(Boolean success, String fullLink, String shortLink, String linkKey) {
        this.success = success;
        this.fullLink = fullLink;
        this.shortLink = shortLink;
        this.linkKey = linkKey;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getFullLink() {
        return fullLink;
    }

    public void setFullLink(String fullLink) {
        this.fullLink = fullLink;
    }

    public String getShortLink() {
        return shortLink;
    }

    public void setShortLink(String shortLink) {
        this.shortLink = shortLink;
    }

    public String getLinkKey() {
        return linkKey;
    }

    public void setLinkKey(String linkKey) {
        this.linkKey = linkKey;
    }
}
