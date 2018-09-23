package com.khubla.pdxreader.db;

/**
 * table type
 */
public enum FileType {
   indexedDB(0),
   indexPX(1, true),
   nonindexedDB(2),
   noincrementingsecondaryindexXnn(3),
   secondaryindexYnn(4, true),
   incrementingsecondaryindexXnn(5),
   nonincrementingsecondaryindexXGn(6),
   secondaryindexYGn(7, true),
   incrementingsecondaryindexXGn(8);

   private int fileType;
   private boolean tfieldNamePtrArray;

   FileType(int value) {
      this(value, false);
   }

   FileType(int value, boolean present) {
      this.fileType = value;
      this.tfieldNamePtrArray =present;
   }

   public int getFileType() {
      return fileType;
   }

   public boolean hasTfieldNamePtrArray() {
      return tfieldNamePtrArray;
   }


   public static FileType getFromIntValue(int tableType) {
      for(FileType t : FileType.values()) {
         if(t.fileType == tableType) {
            return t;
         }
      }
      return null;
   }

}