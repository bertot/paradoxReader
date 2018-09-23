package com.khubla.pdxreader.db;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.khubla.pdxreader.api.PDXReaderException;
import com.khubla.pdxreader.db.DBTableField.FieldType;
import com.khubla.pdxreader.util.ParadoxTime;
import com.khubla.pdxreader.util.StringUtil;

/**
 * @author tom
 */
public class DBTableValue {
   /**
    * the value
    */
   private String value;
   /**
    * the type
    */
   private DBTableField.FieldType type;

   public DBTableField.FieldType getType() {
      return type;
   }

   public String getValue() {
      return value;
   }

   /**
    * Read a table field
    */
   public void read(DBTableField pdxTableField, InputStream inputStream) throws PDXReaderException {
      try {
         /*
          * get the data
          */
         final byte[] data = new byte[pdxTableField.getLength()];
         final int bytesRead = inputStream.read(data);
         if (bytesRead > 0) {
            /*
             * convert to type
             */
            final FieldType fieldType = pdxTableField.getFieldType();
            switch (fieldType) {
               case ALPHA:
                  value = StringUtil.readString(data);
                  break;
               case DATE:
                  // date
                  final long d = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
                  value = Long.toString(d);
                  break;
               case SHORT_INT:
                  final long s = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
                  value = Long.toString(s);
                  break;
               case LONG_INT:
                  data[0] = (byte) (data[0] & 0x7f); // handle unsigned integers
                  final long i = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).getInt();
                  value = Long.toString(i);
                  break;
               case CURRENCY:
                  // currency
                  final double dollars = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN).getDouble();
                  value = Double.toString(dollars);
                  break;
               case MEMO:
                  // Memo
                  value = StringUtil.byteArrayToString(data);
                  break;
               case NUMBER:
                  final long n = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
                  value = Double.toString(n);
                  break;
               case LOGICAL:
                  // Logical
                  value = StringUtil.byteArrayToString(data);
                  break;
               case BINARY:
                  // Binary
                  value = StringUtil.byteArrayToString(data);
                  break;
               case OLE:
                  // OLE
                  value = StringUtil.byteArrayToString(data);
                  break;
               case FORMATTING_MEMO:
                  // formatted memo
                  value = StringUtil.byteArrayToString(data);
                  break;
               case GRAPHIC:
                  // Graphic
                  value = StringUtil.byteArrayToString(data);
                  break;
               case BCD:
                  // BCD
                  value = StringUtil.byteArrayToString(data);
                  break;
               case Bytes:
                  // Bytes
                  value = StringUtil.byteArrayToString(data);
                  break;
               case TIMESTAMP:
                  value = ParadoxTime.getTimeFromParadoxTime(data);
                  break;
               case AUTO_INCREMENT:
                  final short auto = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getShort();
                  value = Short.toString(auto);
                  break;
               default:
                  throw new Exception("Unknown field type '" + fieldType.name() + "'");
            }
         }
      } catch (final Exception e) {
         throw new PDXReaderException("Exception in read", e);
      }
   }

   public void setType(DBTableField.FieldType type) {
      this.type = type;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
