package com.khubla.pdxreader.db;

import com.google.common.io.LittleEndianDataInputStream;
import com.khubla.pdxreader.api.PDXReaderException;

import java.io.IOException;

public class DBDataHeader {
    private int fileVerID3;
    private int fileVerID4;
    private long encryption2;
    private long fileUpdateTime;
    private int hiFieldID;
    private int hiFieldIDinfo;
    private int sometimeNumFields;
    private int dosCodePage;
    private long unknown6Cx6F;
    private int changeCount4;
    private long unknown72x75;
    private int unknown76x77;

    public void read(LittleEndianDataInputStream littleEndianDataInputStream) throws IOException {
        this.fileVerID3 = littleEndianDataInputStream.readUnsignedShort();
        this.fileVerID4 = littleEndianDataInputStream.readUnsignedShort();
        this.encryption2 = littleEndianDataInputStream.readInt();
        this.fileUpdateTime = littleEndianDataInputStream.readInt();
        this.hiFieldID = littleEndianDataInputStream.readUnsignedShort();
        this.hiFieldIDinfo = littleEndianDataInputStream.readUnsignedShort();
        this.sometimeNumFields = littleEndianDataInputStream.readUnsignedShort();
        this.dosCodePage = littleEndianDataInputStream.readUnsignedShort();
        this.unknown6Cx6F = littleEndianDataInputStream.readInt();
        this.changeCount4 = littleEndianDataInputStream.readUnsignedShort();
        this.unknown72x75 = littleEndianDataInputStream.readInt();
        this.unknown76x77 = littleEndianDataInputStream.readUnsignedShort();
    }

    public int getDosCodePage() {
        return dosCodePage;
    }

    public long getFileUpdateTime() {
        return fileUpdateTime;
    }
}
