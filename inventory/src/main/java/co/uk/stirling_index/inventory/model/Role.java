package co.uk.stirling_index.inventory.model;

public enum Role {
    VIEWER("Viewer"),
    // by default, view-only role for general public / unauthenticaticated users
    PARTNER_VIEWER("Partner Viewer"),
    // a large business given specific credentials - a sub-role of business, but does not actively provide products to the web-service upon fetch.
    BUSINESS("Business Account"),
    // business account, API access scoped to their own business only.
    OPERATOR("Operator");
    // operators of the stirling collective website.

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
