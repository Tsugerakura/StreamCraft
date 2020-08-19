package pro.gravit.repackage.io.netty.buffer;

import java.util.List;

public interface PoolArenaMetric {
  int numThreadCaches();
  
  int numTinySubpages();
  
  int numSmallSubpages();
  
  int numChunkLists();
  
  List<PoolSubpageMetric> tinySubpages();
  
  List<PoolSubpageMetric> smallSubpages();
  
  List<PoolChunkListMetric> chunkLists();
  
  long numAllocations();
  
  long numTinyAllocations();
  
  long numSmallAllocations();
  
  long numNormalAllocations();
  
  long numHugeAllocations();
  
  long numDeallocations();
  
  long numTinyDeallocations();
  
  long numSmallDeallocations();
  
  long numNormalDeallocations();
  
  long numHugeDeallocations();
  
  long numActiveAllocations();
  
  long numActiveTinyAllocations();
  
  long numActiveSmallAllocations();
  
  long numActiveNormalAllocations();
  
  long numActiveHugeAllocations();
  
  long numActiveBytes();
}


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\repackage\io\netty\buffer\PoolArenaMetric.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */