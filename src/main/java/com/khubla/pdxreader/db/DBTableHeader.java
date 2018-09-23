package com.khubla.pdxreader.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.io.LittleEndianDataInputStream;
import com.khubla.pdxreader.api.PDXReaderException;
import com.khubla.pdxreader.util.StringUtil;

/**
 * @author tom
 */
public class DBTableHeader {
    private static boolean byteToBool(byte input) throws PDXReaderException {
        if ((input < 0) || (input > 1)) {
            throw new PDXReaderException("Illegal boolean fileType " + Byte.toString(input) + "''");
        }
        return input == 1;
    }

    /**
     * Block size
     */
    private BlockSize blockSize;
    /**
     * table type
     */
    private FileType fileType;
    /**
     * size of single record
     */
    private int recordBufferSize;
    /**
     * length of the header block (bytes)
     */
    private int headerBlockSize;
    /**
     * total records
     */
    private long numberRecords;
    /**
     * number fields
     */
    private int numberFields;
    /**
     * number key fields
     */
    private int numberKeyFields;
    /**
     * data block size code. 1 - 1k, 2 - 2k, 3 - 3k, 4 - 4k, etc
     */
    private int maxTableSize;
    private int blocksInUse;
    private int totalBlocksInFile;
    private int firstDataBlock;
    private int lastDataBlock;
    private int firstFreeBlock;
    /**
     * filename. There is a filename embedded in the files. LONG_INT'm not sure what it does, but LONG_INT do read it.
     */
    private String embeddedFilename;
    /**
     * fields
     */
    private List<DBTableField> fields;
    /**
     * encryption
     */
    private byte[] encryption = new byte[4];
    private int sortOrder;
    private int modified1;
    private int modified2;
    private int indexFieldNumber;
    private int primaryIndexWorkspace;
    private int indexRoot;
    private int numIndexLevels;
    private int change1;
    private int change2;
    private byte[] tableNamePtrPtr;
    private byte[] fldInfoPtr;
    private boolean writeProtected;
    private FileVersion fileVersion;
    private int maxBlocks;
    private int auxPasswords;
    private byte[] cryptInfoStartPtr;
    private byte[] cryptInfoEndPtr;
    private int autoInc;
    private boolean indexUpdateRequired;
    private boolean refIntegrity;
    private int realHeaderSize;

    private int unknown0x12x13;
    private byte[] unknownPtr0x1A;
    private int unknown0x2bx2c;
    private int unknown0x2f;
    private int unknown0x3c;
    private int unknown0x3ex3f;
    private int unknown0x48;
    private int unknown0x50f;
    private int unknown0x53x54;
    private int unknown0x56x57;
    private DBDataHeader dbDataHeader;

    public DBDataHeader getDbDataHeader() {
        return dbDataHeader;
    }

    /**
     * figure out the total records in a block
     */
    public int calculateRecordsPerBlock() {
        return (blockSize.value * 1024) / recordBufferSize;
    }

    public int getAutoInc() {
        return autoInc;
    }

    public int getAuxPasswords() {
        return auxPasswords;
    }

    public int getBlocksInUse() {
        return blocksInUse;
    }

    public BlockSize getBlockSize() {
        return blockSize;
    }

    public int getChange1() {
        return change1;
    }

    public int getChange2() {
        return change2;
    }

    public byte[] getCryptInfoEndPtr() {
        return cryptInfoEndPtr;
    }

    public byte[] getCryptInfoStartPtr() {
        return cryptInfoStartPtr;
    }

    public int getMaxTableSize() {
        return maxTableSize;
    }

    public String getEmbeddedFilename() {
        return embeddedFilename;
    }

    public byte[] getEncryption() {
        return encryption;
    }

    public List<DBTableField> getFields() {
        return fields;
    }

    public int getFileVersion() {
        return fileVersion.fileVersion;
    }

    public int getFileVersionID() {
        return fileVersion.fileVersionId;
    }

    public int getFirstDataBlock() {
        return firstDataBlock;
    }

    public int getFirstFreeBlock() {
        return firstFreeBlock;
    }

    public byte[] getFldInfoPtr() {
        return fldInfoPtr;
    }

    public int getHeaderBlockSize() {
        return headerBlockSize;
    }

    public int getIndexFieldNumber() {
        return indexFieldNumber;
    }

    public int getIndexRoot() {
        return indexRoot;
    }

    public boolean getIndexUpdateRequired() {
        return indexUpdateRequired;
    }

    public int getLastDataBlock() {
        return lastDataBlock;
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public int getModified1() {
        return modified1;
    }

    public int getModified2() {
        return modified2;
    }

    public int getNumberFields() {
        return numberFields;
    }

    public int getNumberKeyFields() {
        return numberKeyFields;
    }

    public long getNumberRecords() {
        return numberRecords;
    }

    public int getNumIndexLevels() {
        return numIndexLevels;
    }

    public int getPrimaryIndexWorkspace() {
        return primaryIndexWorkspace;
    }

    public int getRecordBufferSize() {
        return recordBufferSize;
    }

    public boolean getRefIntegrity() {
        return refIntegrity;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public byte[] getTableNamePtrPtr() {
        return tableNamePtrPtr;
    }

    public FileType getFileType() {
        return fileType;
    }

    public int getTotalBlocksInFile() {
        return totalBlocksInFile;
    }


    public int getUnknown0x3ex3f() {
        return unknown0x3ex3f;
    }

    public int getUnknown0x48() {
        return unknown0x48;
    }


    public int getUnknown0x50f() {
        return unknown0x50f;
    }

    public int getUnknown0x12x13() {
        return unknown0x12x13;
    }

    public byte[] getUnknownPtr0x1A() {
        return unknownPtr0x1A;
    }

    public int getUnknown0x2bx2c() {
        return unknown0x2bx2c;
    }

    public int getUnknown0x2f() {
        return unknown0x2f;
    }

    public int getUnknown0x3c() {
        return unknown0x3c;
    }

    public boolean getWriteProtected() {
        return writeProtected;
    }

    /**
     * read (little endian)
     */
    public void read(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
        try {
            /*
             * record size
             */
            recordBufferSize = littleEndianDataInputStream.readUnsignedShort();
            /*
             * size of this header block
             */
            headerBlockSize = littleEndianDataInputStream.readUnsignedShort();
            /*
             * type of file
             */
            final int fileTypeAsByte = littleEndianDataInputStream.readUnsignedByte();
            this.fileType = FileType.getFromIntValue(fileTypeAsByte);
            if(null == this.fileType) {
                throw new PDXReaderException("Unknown table type '" + fileTypeAsByte + "'");
            }
            /*
             * size of a data block
             */
            maxTableSize = littleEndianDataInputStream.readUnsignedByte();
            blockSize = BlockSize.getFromIntValue(maxTableSize);
            if (null == blockSize) {
                throw new PDXReaderException("Unknown block size code '" + maxTableSize + "'");
            }

            /*
             * total records in file
             */
            numberRecords = littleEndianDataInputStream.readInt();
            /*
             * number of blocks in use
             */
            blocksInUse = littleEndianDataInputStream.readUnsignedShort();
            /*
             * total blocks in file
             */
            totalBlocksInFile = littleEndianDataInputStream.readUnsignedShort();
            firstDataBlock = littleEndianDataInputStream.readUnsignedShort();
            lastDataBlock = littleEndianDataInputStream.readUnsignedShort();
            /*
             * unknown bytes 0x12, 0x13
             */
            unknown0x12x13 = littleEndianDataInputStream.readUnsignedShort();
            /*
             * modified
             */
            modified1 = littleEndianDataInputStream.readUnsignedByte();
            indexFieldNumber = littleEndianDataInputStream.readUnsignedByte();
            primaryIndexWorkspace = littleEndianDataInputStream.readInt();
            /*
             * unknown pointer
             */
            unknownPtr0x1A = readPointer(littleEndianDataInputStream);
            indexRoot = littleEndianDataInputStream.readUnsignedShort();
            numIndexLevels = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x21
            numberFields = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x23
            numberKeyFields = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x24
            littleEndianDataInputStream.read(encryption);
            // byte 0x29
            sortOrder = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x2a
            modified2 = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x2b
            unknown0x2bx2c = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x2d
            change1 = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x2e
            change2 = littleEndianDataInputStream.readUnsignedByte();
            /*
             * unknown
             */
            // byte 0x2f
            unknown0x2f = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x30
            tableNamePtrPtr = readPointer(littleEndianDataInputStream);
            // byte 0x34
            fldInfoPtr = readPointer(littleEndianDataInputStream);
            // byte 0x38
            writeProtected = byteToBool(littleEndianDataInputStream.readByte());
            // byte 0x39
            int fileVersionID = littleEndianDataInputStream.readByte();
            fileVersion = FileVersion.getFromFileVersionId(fileVersionID);
            // byte 0x3a
            maxBlocks = littleEndianDataInputStream.readUnsignedShort();
            // byte 3c
            unknown0x3c = littleEndianDataInputStream.readUnsignedByte();
            // byte 3d
            auxPasswords = littleEndianDataInputStream.readByte();
            // byte 3e / 3f
            unknown0x3ex3f = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x40
            cryptInfoStartPtr = readPointer(littleEndianDataInputStream);
            // byte 0x44
            cryptInfoEndPtr = readPointer(littleEndianDataInputStream);
            // byte 0x48
            unknown0x48 = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x49
            autoInc = littleEndianDataInputStream.readInt();
            // byte 0x4d
            firstFreeBlock = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x4f
            indexUpdateRequired = byteToBool(littleEndianDataInputStream.readByte());
            // byte 0x50
            unknown0x50f = littleEndianDataInputStream.readUnsignedByte();
            // byte 0x51
            realHeaderSize = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x53 / 0x54
            unknown0x53x54 = littleEndianDataInputStream.readUnsignedShort();
            // byte 0x55
            refIntegrity = byteToBool(littleEndianDataInputStream.readByte());
            // byte 0x56 / 57
            unknown0x56x57 = littleEndianDataInputStream.readUnsignedShort();
            /*
             * if file version >= 40 we've got additional DataHeader-Information
             */
            if (fileVersion.fileVersion >= 40) {
                dbDataHeader = new DBDataHeader();
                dbDataHeader.read(littleEndianDataInputStream);
            }

            // byte 0x78 (for v40).
            readFieldTypesAndSizes(littleEndianDataInputStream);


            // skip tableNamePtr
            littleEndianDataInputStream.skipBytes(4);

            /* skip the tfieldNamePtrArray, if not present (only in secondary index files) */
            if(!this.fileType.hasTfieldNamePtrArray()) {
                littleEndianDataInputStream.skipBytes(this.fields.size()*4);
            }

            /*
             * read the name
             */
             final byte[] fn = new byte[this.fileVersion.tableNameLength];
             littleEndianDataInputStream.read(fn);
             embeddedFilename = StringUtil.readString(fn);
            /*
             * now read the field names
             */
            readFieldNames(littleEndianDataInputStream);
        } catch (IOException e) {
            throw new PDXReaderException("IO Exception thrown while reading tableHeader", e);
        }
    }

    /**
     * read the field descriptions
     */
    private void readFieldNames(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
        try {
            for (final DBTableField pdxTableField : fields) {
                pdxTableField.readFieldName(littleEndianDataInputStream);
            }
        } catch (final Exception e) {
            throw new PDXReaderException("Exception in readFieldNames", e);
        }
    }

    /**
     * read the field descriptions
     */
    private void readFieldTypesAndSizes(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
        fields = new ArrayList<DBTableField>();
        for (int i = 0; i < numberFields; i++) {
            final DBTableField pdxTableField = new DBTableField();
            if (pdxTableField.readFieldTypeAndSize(littleEndianDataInputStream)) {
                fields.add(pdxTableField);
            }
        }
    }

    private byte[] readPointer(LittleEndianDataInputStream littleEndianDataInputStream) throws IOException {
        final byte[] ptr = new byte[4];
        littleEndianDataInputStream.read(ptr);
        return ptr;
    }


    public void report() {
        for (final DBTableField pdxTableField : fields) {
            System.out.println("Field '" + pdxTableField.getName() + "' type '" + pdxTableField.getFieldType().toString() + "'");
        }
    }

    public void setAutoInc(int autoInc) {
        this.autoInc = autoInc;
    }

    public void setAuxPasswords(int auxPasswords) {
        this.auxPasswords = auxPasswords;
    }

    public void setBlocksInUse(int blocksInUse) {
        this.blocksInUse = blocksInUse;
    }

    public void setBlockSize(BlockSize blockSize) {
        this.blockSize = blockSize;
    }

    public void setChange1(int change1) {
        this.change1 = change1;
    }

    public void setChange2(int change2) {
        this.change2 = change2;
    }

    public void setCryptInfoEndPtr(byte[] cryptInfoEndPtr) {
        this.cryptInfoEndPtr = cryptInfoEndPtr;
    }

    public void setCryptInfoStartPtr(byte[] cryptInfoStartPtr) {
        this.cryptInfoStartPtr = cryptInfoStartPtr;
    }

    public void setMaxTableSize(int maxTableSize) {
        this.maxTableSize = maxTableSize;
    }

    public void setEmbeddedFilename(String embeddedFilename) {
        this.embeddedFilename = embeddedFilename;
    }

    public void setEncryption(byte[] encryption) {
        this.encryption = encryption;
    }

    public void setFields(List<DBTableField> fields) {
        this.fields = fields;
    }

    public void setFirstDataBlock(int firstDataBlock) {
        this.firstDataBlock = firstDataBlock;
    }

    public void setFirstFreeBlock(int firstFreeBlock) {
        this.firstFreeBlock = firstFreeBlock;
    }

    public void setFldInfoPtr(byte[] fldInfoPtr) {
        this.fldInfoPtr = fldInfoPtr;
    }

    public void setHeaderBlockSize(int headerBlockSize) {
        this.headerBlockSize = headerBlockSize;
    }

    public void setIndexFieldNumber(int indexFieldNumber) {
        this.indexFieldNumber = indexFieldNumber;
    }

    public void setIndexRoot(int indexRoot) {
        this.indexRoot = indexRoot;
    }

    public void setIndexUpdateRequired(boolean indexUpdateRequired) {
        this.indexUpdateRequired = indexUpdateRequired;
    }

    public void setLastDataBlock(int lastDataBlock) {
        this.lastDataBlock = lastDataBlock;
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    public void setModified1(int modified1) {
        this.modified1 = modified1;
    }

    public void setModified2(int modified2) {
        this.modified2 = modified2;
    }

    public void setNumberFields(int numberFields) {
        this.numberFields = numberFields;
    }

    public void setNumberKeyFields(int numberKeyFields) {
        this.numberKeyFields = numberKeyFields;
    }

    public void setNumberRecords(long numberRecords) {
        this.numberRecords = numberRecords;
    }

    public void setNumIndexLevels(int numIndexLevels) {
        this.numIndexLevels = numIndexLevels;
    }

    public void setPrimaryIndexWorkspace(int primaryIndexWorkspace) {
        this.primaryIndexWorkspace = primaryIndexWorkspace;
    }

    public void setRecordBufferSize(int recordBufferSize) {
        this.recordBufferSize = recordBufferSize;
    }

    public void setRefIntegrity(boolean refIntegrity) {
        this.refIntegrity = refIntegrity;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setTableNamePtrPtr(byte[] tableNamePtrPtr) {
        this.tableNamePtrPtr = tableNamePtrPtr;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    public void setTotalBlocksInFile(int totalBlocksInFile) {
        this.totalBlocksInFile = totalBlocksInFile;
    }

    public void setUnknown0x3ex3f(int unknown0x3ex3f) {
        this.unknown0x3ex3f = unknown0x3ex3f;
    }

    public void setUnknown0x48(int unknown0x48) {
        this.unknown0x48 = unknown0x48;
    }


    public void setUnknown0x50f(int unknown0x50f) {
        this.unknown0x50f = unknown0x50f;
    }

    public void setUnknown0x12x13(int unknown0x12x13) {
        this.unknown0x12x13 = unknown0x12x13;
    }

    public void setUnknownPtr0x1A(byte[] unknownPtr0x1A) {
        this.unknownPtr0x1A = unknownPtr0x1A;
    }

    public void setUnknown0x2bx2c(int unknown0x2bx2c) {
        this.unknown0x2bx2c = unknown0x2bx2c;
    }

    public void setUnknown0x2f(int unknown0x2f) {
        this.unknown0x2f = unknown0x2f;
    }

    public void setUnknown0x3c(int unknown0x3c) {
        this.unknown0x3c = unknown0x3c;
    }

    public void setWriteProtected(boolean writeProtected) {
        this.writeProtected = writeProtected;
    }

    public int getRealHeaderSize() {
        return realHeaderSize;
    }
}
