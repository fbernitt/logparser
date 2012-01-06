package de.thecodex.logparser.flyway;

public enum SupportedDatabase {
    H2("h2"),
    POSTGRES("postgres");

    private final String migrationFolder;

    SupportedDatabase(String migrationFolder) {
        this.migrationFolder = migrationFolder;
    }

    public String getMigrationFolder() {
        return this.migrationFolder;
    }
}
