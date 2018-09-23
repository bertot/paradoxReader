package com.khubla.pdxreader.px;

import com.google.common.io.LittleEndianDataInputStream;
import com.khubla.pdxreader.api.PDXReaderException;
import com.khubla.pdxreader.db.BlockSize;
import com.khubla.pdxreader.db.FileType;

/**
 * @author tom
 */
public class PXFileHeader {
   /**
    * Block size
    */
   private BlockSize blockSize;
   /**
    * table type
    */
   private FileType fileType;
   /**
    * length of the header block (bytes)
    */
   private int headerBlockSize;
   /**
    * length of index record (-6)
    */
   private int indexRecordLength;
   /**
    * total records
    */
   private long numberRecords;
   /**
    * data block size code. 1 - 1k, 2 - 2k, 3 - 3k, 4 - 4k.
    */
   private int dataBlockSizeCode;
   private int blocksInUse;
   private int totalBlocksInFile;
   private int firstDataBlock;
   private int lastDataBlock;
   private int indexRootBlock;
   private int numberIndexLevels;
   private int numberIndexFields;

   public int getBlocksInUse() {
      return blocksInUse;
   }

   public BlockSize getBlockSize() {
      return blockSize;
   }

   public int getDataBlockSizeCode() {
      return dataBlockSizeCode;
   }

   public int getFirstDataBlock() {
      return firstDataBlock;
   }

   public int getHeaderBlockSize() {
      return headerBlockSize;
   }

   public int getIndexRecordLength() {
      return indexRecordLength;
   }

   public int getIndexRootBlock() {
      return indexRootBlock;
   }

   public int getLastDataBlock() {
      return lastDataBlock;
   }

   public int getNumberIndexFields() {
      return numberIndexFields;
   }

   public int getNumberIndexLevels() {
      return numberIndexLevels;
   }

   public long getNumberRecords() {
      return numberRecords;
   }

   public FileType getFileType() {
      return fileType;
   }

   public int getTotalBlocksInFile() {
      return totalBlocksInFile;
   }

   /**
    * read (little endian)
    */
   public void read(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
      try {
         /*
          * record size
          */
         indexRecordLength = littleEndianDataInputStream.readUnsignedShort();
         /*
          * size of this header block
          */
         headerBlockSize = littleEndianDataInputStream.readUnsignedShort();
         /*
          * type of file
          */
         final int fileTypeAsByte = littleEndianDataInputStream.readUnsignedByte();
         this.fileType = FileType.getFromIntValue(fileTypeAsByte);
         if(null == fileType) {
            throw new PDXReaderException("Unknown file type '" + fileTypeAsByte + "'");
         }

         /*
          * size of a data block
          */
         dataBlockSizeCode = littleEndianDataInputStream.readUnsignedByte();
         this.blockSize = BlockSize.getFromIntValue(dataBlockSizeCode);
         if(null == blockSize) {
            throw new PDXReaderException("Unknown block size code '" + dataBlockSizeCode + "'");
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
         if (1 != firstDataBlock) {
            throw new Exception("firstDataBlock was expected to be 1, but '" + firstDataBlock + "' was found");
         }
         lastDataBlock = littleEndianDataInputStream.readUnsignedShort();
         indexRootBlock = littleEndianDataInputStream.readUnsignedShort();
         numberIndexLevels = littleEndianDataInputStream.readUnsignedByte();
         /*
          * The number of fields in the index is the same as the number of key fields for the table.
          */
         numberIndexFields = littleEndianDataInputStream.readUnsignedByte();
      } catch (final Exception e) {
         throw new PDXReaderException("Exception in read", e);
      }
   }

   public void setBlocksInUse(int blocksInUse) {
      this.blocksInUse = blocksInUse;
   }

   public void setBlockSize(BlockSize blockSize) {
      this.blockSize = blockSize;
   }

   public void setDataBlockSizeCode(int dataBlockSizeCode) {
      this.dataBlockSizeCode = dataBlockSizeCode;
   }

   public void setFirstDataBlock(int firstDataBlock) {
      this.firstDataBlock = firstDataBlock;
   }

   public void setHeaderBlockSize(int headerBlockSize) {
      this.headerBlockSize = headerBlockSize;
   }

   public void setIndexRecordLength(int indexRecordLength) {
      this.indexRecordLength = indexRecordLength;
   }

   public void setIndexRootBlock(int indexRootBlock) {
      this.indexRootBlock = indexRootBlock;
   }

   public void setLastDataBlock(int lastDataBlock) {
      this.lastDataBlock = lastDataBlock;
   }

   public void setNumberIndexFields(int numberIndexFields) {
      this.numberIndexFields = numberIndexFields;
   }

   public void setNumberIndexLevels(int numberIndexLevels) {
      this.numberIndexLevels = numberIndexLevels;
   }

   public void setNumberRecords(long numberRecords) {
      this.numberRecords = numberRecords;
   }

   public void setFileType(FileType fileType) {
      this.fileType = fileType;
   }

   public void setTotalBlocksInFile(int totalBlocksInFile) {
      this.totalBlocksInFile = totalBlocksInFile;
   }
}
