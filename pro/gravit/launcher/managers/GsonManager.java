/*    */ package pro.gravit.launcher.managers;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.GsonBuilder;
/*    */ import pro.gravit.launcher.hasher.HashedEntry;
/*    */ import pro.gravit.launcher.hasher.HashedEntryAdapter;
/*    */ import pro.gravit.utils.helper.CommonHelper;
/*    */ 
/*    */ public class GsonManager
/*    */ {
/*    */   public GsonBuilder gsonBuilder;
/*    */   public Gson gson;
/*    */   public GsonBuilder configGsonBuilder;
/*    */   public Gson configGson;
/*    */   
/*    */   public void initGson() {
/* 17 */     this.gsonBuilder = CommonHelper.newBuilder();
/* 18 */     this.configGsonBuilder = CommonHelper.newBuilder();
/* 19 */     this.configGsonBuilder.setPrettyPrinting();
/* 20 */     registerAdapters(this.gsonBuilder);
/* 21 */     registerAdapters(this.configGsonBuilder);
/* 22 */     preConfigGson(this.configGsonBuilder);
/* 23 */     preGson(this.gsonBuilder);
/* 24 */     this.gson = this.gsonBuilder.create();
/* 25 */     this.configGson = this.configGsonBuilder.create();
/*    */   }
/*    */   
/*    */   public void registerAdapters(GsonBuilder builder) {
/* 29 */     builder.registerTypeAdapter(HashedEntry.class, new HashedEntryAdapter());
/*    */   }
/*    */   
/*    */   public void preConfigGson(GsonBuilder gsonBuilder) {}
/*    */   
/*    */   public void preGson(GsonBuilder gsonBuilder) {}
/*    */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\managers\GsonManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */