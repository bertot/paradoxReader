package com.khubla.pdxreader.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;

import com.google.common.io.LittleEndianDataInputStream;
import com.khubla.pdxreader.api.PDXReaderException;
import com.khubla.pdxreader.api.PDXTableListener;

/**
 * @author tom
 */
public class DBTableFile {
   /**
    * max header size
    */
   private final static int MAX_HEADER_SIZE = 10240;
   /**
    * max block size (64K blocks are the biggest blocks)
    */
   private final static int MAX_BLOCK_SIZE = 64 * 1024;
   /**
    * header
    */
   private DBTableHeader dbTableHeader;
   /**
    * blocks
    */
   private Hashtable<Integer, DBTableBlock> blocks;

   public Hashtable<Integer, DBTableBlock> getBlocks() {
      return blocks;
   }

   public DBTableHeader getDbTableHeader() {
      return dbTableHeader;
   }

    /**
     * read
     *
     * @throws PDXReaderException - other problems reading this file
     * @throws FileNotFoundException - if file not exists
     */
    public void read(File file, PDXTableListener pdxReaderListener) throws PDXReaderException, FileNotFoundException {
        /*
         * check if the file exists
         */
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " is not a file");
        }

        /*
         * start
         */
        pdxReaderListener.start();
        /*
         * set up streams
         */
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        final LittleEndianDataInputStream littleEndianDataInputStream = new LittleEndianDataInputStream(bufferedInputStream);
        try {
            /*
             * mark and read the headers
             */
            bufferedInputStream.mark(MAX_HEADER_SIZE);
            readHeaders(littleEndianDataInputStream);
            /*
             * call the api
             */
            pdxReaderListener.header(dbTableHeader);
            /*
             * read the block data
             */
            bufferedInputStream.reset();
            readBlocks(bufferedInputStream, pdxReaderListener);
            /*
             * done
             */
            pdxReaderListener.finish();
        } catch (IOException e) {
            throw new PDXReaderException(e);
        } finally {
            try {
                littleEndianDataInputStream.close();
                bufferedInputStream.close();
            } catch (IOException e) {
                // silently ignore exceptions
            }
        }
    }

   /**
    * read block data
    */
   private void readBlocks(BufferedInputStream bufferedInputStream, PDXTableListener pdxReaderListener) throws PDXReaderException {
      try {
         /*
          * init the array
          */
         blocks = new Hashtable<Integer, DBTableBlock>();
         /*
          * skip to the first block
          */
         final int nSkip = dbTableHeader.getHeaderBlockSize();
         if (nSkip == bufferedInputStream.skip(nSkip)) {
            /*
             * records per block
             */
            final int recordsPerBlock = dbTableHeader.calculateRecordsPerBlock();
            /*
             * total records remaining to read
             */
            long recordsremaining = dbTableHeader.getNumberRecords();
            /*
             * walk blocks
             */
            final int blocksInUse = dbTableHeader.getBlocksInUse();
            for (int i = 0; i < blocksInUse; i++) {
               /*
                * block
                */
               final DBTableBlock pdxTableBlock = new DBTableBlock(i + 1, recordsPerBlock, dbTableHeader.getFields());
               /*
                * mark at the start of the block
                */
               bufferedInputStream.mark(MAX_BLOCK_SIZE);
               /*
                * read the block data
                */
               long recordsToRead = recordsPerBlock;
               if (recordsremaining < recordsPerBlock) {
                  recordsToRead = recordsremaining;
               }
               pdxTableBlock.read(pdxReaderListener, bufferedInputStream, recordsToRead);
               /*
                * fewer records
                */
               recordsremaining -= recordsToRead;
               /*
                * store it. blocks are numbered from 1, not from 0.
                */
               blocks.put(pdxTableBlock.getBlockNumber(), pdxTableBlock);
               /*
                * reset to the start of the block
                */
               bufferedInputStream.reset();
               /*
                * skip ahead to next block
                */
               bufferedInputStream.skip(dbTableHeader.getBlockSize().getValue() * 1024);
            }
         } else {
            throw new PDXReaderException("File format exception");
         }
      } catch (final Exception e) {
         throw new PDXReaderException("Exception in readBlocks", e);
      }
   }

    /**
     * read
     */
    private void readHeaders(LittleEndianDataInputStream littleEndianDataInputStream) throws PDXReaderException {
        /*
         * read header
         */
        dbTableHeader = new DBTableHeader();
        dbTableHeader.read(littleEndianDataInputStream);
    }

   public void setBlocks(Hashtable<Integer, DBTableBlock> blocks) {
      this.blocks = blocks;
   }

   public void setDbTableHeader(DBTableHeader dbTableHeader) {
      this.dbTableHeader = dbTableHeader;
   }
}
