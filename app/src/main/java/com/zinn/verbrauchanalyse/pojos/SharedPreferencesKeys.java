package com.zinn.verbrauchanalyse.pojos;

public enum SharedPreferencesKeys {

    SHARED_PREFERENCES("shared_preferences"),

    FILE_URI("file_uri");

    private String key;

    SharedPreferencesKeys(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
