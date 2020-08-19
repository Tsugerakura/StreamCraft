/*    */ package pro.gravit.launcher.hwid;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import java.util.Objects;
/*    */ import java.util.StringJoiner;
/*    */ import pro.gravit.launcher.LauncherAPI;
/*    */ 
/*    */ public class OshiHWID
/*    */   implements HWID
/*    */ {
/* 11 */   public static Gson gson = new Gson(); @LauncherAPI
/* 12 */   public long totalMemory = 0L;
/*    */   
/*    */   @LauncherAPI
/*    */   public String serialNumber;
/*    */   
/*    */   @LauncherAPI
/*    */   public String HWDiskSerial;
/*    */   @LauncherAPI
/*    */   public String processorID;
/*    */   @LauncherAPI
/*    */   public String macAddr;
/*    */   
/*    */   public String getSerializeString() {
/* 25 */     return gson.toJson(this);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLevel() {
/* 31 */     int result = 0;
/* 32 */     if (this.totalMemory != 0L) result += 8; 
/* 33 */     if (this.serialNumber != null && !this.serialNumber.equals("unknown")) result += 12; 
/* 34 */     if (this.HWDiskSerial != null && !this.HWDiskSerial.equals("unknown")) result += 30; 
/* 35 */     if (this.processorID != null && !this.processorID.equals("unknown")) result += 10; 
/* 36 */     if (this.macAddr != null && !this.macAddr.equals("00:00:00:00:00:00")) result += 15; 
/* 37 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compare(HWID hwid) {
/* 42 */     if (hwid instanceof OshiHWID) {
/* 43 */       int rate = 0;
/* 44 */       OshiHWID oshi = (OshiHWID)hwid;
/* 45 */       if (Math.abs(oshi.totalMemory - this.totalMemory) < 1048576L) rate += 5; 
/* 46 */       if (oshi.totalMemory == this.totalMemory) rate += 15; 
/* 47 */       if (oshi.HWDiskSerial.equals(this.HWDiskSerial)) rate += 45; 
/* 48 */       if (oshi.processorID.equals(this.processorID)) rate += 18; 
/* 49 */       if (oshi.serialNumber.equals(this.serialNumber)) rate += 15; 
/* 50 */       if (!oshi.macAddr.isEmpty() && oshi.macAddr.equals(this.macAddr)) rate += 19; 
/* 51 */       return rate;
/*    */     } 
/* 53 */     return 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isNull() {
/* 58 */     return (getLevel() < 15);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 63 */     if (this == o) return true; 
/* 64 */     if (o == null || getClass() != o.getClass()) return false; 
/* 65 */     OshiHWID oshiHWID = (OshiHWID)o;
/* 66 */     return (this.totalMemory == oshiHWID.totalMemory && 
/* 67 */       Objects.equals(this.serialNumber, oshiHWID.serialNumber) && 
/* 68 */       Objects.equals(this.HWDiskSerial, oshiHWID.HWDiskSerial) && 
/* 69 */       Objects.equals(this.processorID, oshiHWID.processorID) && 
/* 70 */       Objects.equals(this.macAddr, oshiHWID.macAddr));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 75 */     return Objects.hash(new Object[] { Long.valueOf(this.totalMemory), this.serialNumber, this.HWDiskSerial, this.processorID, this.macAddr });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     return (new StringJoiner(", ", OshiHWID.class.getSimpleName() + "[", "]"))
/* 81 */       .add("totalMemory=" + this.totalMemory)
/* 82 */       .add("serialNumber='" + this.serialNumber + "'")
/* 83 */       .add("HWDiskSerial='" + this.HWDiskSerial + "'")
/* 84 */       .add("processorID='" + this.processorID + "'")
/* 85 */       .add("macAddr='" + this.macAddr + "'")
/* 86 */       .toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\hwid\OshiHWID.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */