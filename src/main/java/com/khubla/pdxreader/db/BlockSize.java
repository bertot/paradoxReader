package com.khubla.pdxreader.db;

import com.khubla.pdxreader.api.PDXReaderException;

/**
 * block size
 */
public enum BlockSize {
   oneK(1), twoK(2), threeK(3), fourK(4), eightK(8), sixteenK(16), thirtytwoK(32);
   public int value;

   BlockSize(int value) {
      this.value = value;
   }

   public int getValue() {
      return value;
   }

   public static BlockSize getFromIntValue(int value) {
      for(BlockSize s : values()) {
         if ( s.value == value ) {
            return s;
         }
      }
      return null;
   }
}