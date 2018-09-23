package com.khubla.pdxreader.db;

import com.google.common.io.LittleEndianDataInputStream;
import com.khubla.pdxreader.api.PDXReaderException;
import com.khubla.pdxreader.util.StringUtil;

import java.io.IOException;

/**
 * @author tom
 */
public class DBTableField {
    /**
     * field type
     */
    public enum FieldType {
        /**
         * <pre>
         * ALPHA - Alpha, length 255
         * DATE - Date, length 4
         * SHORT_INT - Short Integer, length 2
         * LONG_INT - Long Integer, length 4
         * CURRENCY - Currency, length 8
         * NUMBER - Number, length 8
         * LOGICAL - Logical, length 1
         * MEMO - Memo, variable length
         * BINARY - Binary, variable length
         * FORMATTING_MEMO - Formatting memo, variable length
         * OLE - OLE, variable length
         * GRAPHIC - Graphic Blob, variable length
         * TIME - Time, length 4
         * TIMESTAMP - TimeStamp, length 8
         * AUTO_INCREMENT - AutoIncrement
         * BCD - BCD, length 17
         * Bytes - Bytes, variable length
         * </pre>
         */
        ALPHA(0x01),
        DATE(0x02, 4),
        SHORT_INT(0x03, 2),
        LONG_INT(0x04, 4),
        CURRENCY(0x05, 8),
        NUMBER(0x06, 8),
        LOGICAL(0x09, 1),
        MEMO(0x0c),
        BINARY(0x0d),
        FORMATTING_MEMO(0x0e),
        OLE(0x0f),
        GRAPHIC(0x10),
        TIME(0x14, 4),
        TIMESTAMP(0x15, 8),
        AUTO_INCREMENT(0x16),
        BCD(0x17, 17),
        Bytes(0x18);
        // private int fileType;

        private int headerType;
        private int fixedLength;

        FieldType(int type) {
            this.headerType = type;
            this.fixedLength = -1;
        }

        FieldType(int type, int len) {
            this.headerType = type;
            this.fixedLength = len;
        }

        public int getHeaderType() {
            return headerType;
        }

        public boolean validateLength(int length) {
            return (fixedLength < 0 || length == fixedLength);
        }

        public static FieldType getFromType(int type) {
            for (FieldType ft : FieldType.values()) {
                if (ft.getHeaderType() == type) {
                    return ft;
                }
            }
            return null;
        }
    }

    /**
     * field type
     */
    private FieldType fieldType;

    /**
     * field length
     */
    private int length;
    /**
     * name
     */
    private String name;
    /**
     * fdc (from pxlib)
     */
    private int fdc = 0;

    /**
     * unknown bytes
     */
    private byte[] unknownFieldBytes;
    FieldType getFieldType() {
        return fieldType;
    }

    int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public byte[] getUnknownFieldBytes() {
        return unknownFieldBytes;
    }

    /**
     * names
     */
    void readFieldName(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
        try {
            name = StringUtil.readString(littleEndianDataInputStream);
        } catch (final Exception e) {
            throw new PDXReaderException("Exception in read", e);
        }
    }

    /**
     * types and sizes, 2 bytes per field
     */
    boolean readFieldTypeAndSize(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
        try {
            int type = littleEndianDataInputStream.readUnsignedByte();
            length = littleEndianDataInputStream.readUnsignedByte();

            if (type == 0x00) {
                return false;
            }

            fieldType = FieldType.getFromType(type);

            if (null == fieldType) {
                throw new PDXReaderException("Unknown field type '" + type + "'");
            }

            // BCD Fields have a fixed length of 17 ... information from stream is stored in "fdc" attribute instead
            if(FieldType.BCD.equals(fieldType)) {
                this.fdc = length;
                this.length = 17;
            }

            if (!fieldType.validateLength(length)) {
                throw new PDXReaderException("Invalid field length '" + length + "' for type '" + type + "'(" + fieldType + ") - expected: '" + fieldType.fixedLength + "'");
            }

            return true;
        } catch (IOException e) {
            throw new PDXReaderException("error reading type/length from stream", e);
        }
    }

    public void setUnknownFieldBytes(byte[] unknownFieldBytes) {
        this.unknownFieldBytes = unknownFieldBytes;
    }
}
