package com.khubla.pdxreader.db;

public enum FileVersion {
    PARADOX3(3,30),
    PARADOX4(4,35),
    PARADOX5(5,40),
    PARADOX6(6,40),
    PARADOX7(7,40),
    PARADOX8(8,40),
    PARADOX9(9,40),
    PARADOX10(10,50),
    PARADOX11(11,50),
    PARADOX12(12,70,261),
    DEFAULT(0,0);

    int fileVersionId;
    int fileVersion;
    int tableNameLength;


    FileVersion(int versionId, int versionNumber, int tableNameLen) {
        this.fileVersionId = versionId;
        this.fileVersion = versionNumber;
        this.tableNameLength = tableNameLen;
    }

    FileVersion(int versionId, int versionNumber) {
        this(versionId, versionNumber, 79);
    }

    public static FileVersion getFromFileVersionId(int fileVersionId) {
        for(FileVersion fv : values()) {
            if(fv.fileVersionId == fileVersionId) {
                return fv;
            }
        }

        return DEFAULT;
    }

}
