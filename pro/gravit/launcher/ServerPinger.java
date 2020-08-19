/*     */ package pro.gravit.launcher;
/*     */ 
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonParser;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.Duration;
/*     */ import java.time.Instant;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Pattern;
/*     */ import pro.gravit.launcher.profiles.ClientProfile;
/*     */ import pro.gravit.launcher.serialize.HInput;
/*     */ import pro.gravit.launcher.serialize.HOutput;
/*     */ import pro.gravit.utils.helper.IOHelper;
/*     */ import pro.gravit.utils.helper.VerifyHelper;
/*     */ 
/*     */ 
/*     */ public final class ServerPinger
/*     */ {
/*  24 */   private JsonParser parser = new JsonParser();
/*     */   
/*     */   private static final String LEGACY_PING_HOST_MAGIC = "§1";
/*     */   private static final String LEGACY_PING_HOST_CHANNEL = "MC|PingHost";
/*     */   
/*     */   public static final class Result
/*     */   {
/*     */     @LauncherAPI
/*     */     public final int onlinePlayers;
/*     */     
/*     */     public Result(int onlinePlayers, int maxPlayers, String raw) {
/*  35 */       this.onlinePlayers = VerifyHelper.verifyInt(onlinePlayers, VerifyHelper.NOT_NEGATIVE, "onlinePlayers can't be < 0");
/*     */       
/*  37 */       this.maxPlayers = VerifyHelper.verifyInt(maxPlayers, VerifyHelper.NOT_NEGATIVE, "maxPlayers can't be < 0");
/*     */       
/*  39 */       this.raw = raw;
/*     */     } @LauncherAPI
/*     */     public final int maxPlayers; @LauncherAPI
/*     */     public final String raw; @LauncherAPI
/*     */     public boolean isOverfilled() {
/*  44 */       return (this.onlinePlayers >= this.maxPlayers);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final Pattern LEGACY_PING_HOST_DELIMETER = Pattern.compile("\000", 16); private static final int PACKET_LENGTH = 65535; private final InetSocketAddress address;
/*     */   private final ClientProfile.Version version;
/*     */   private List<String> servers;
/*     */   
/*     */   private static String readUTF16String(HInput input) throws IOException {
/*  56 */     int length = input.readUnsignedShort() << 1;
/*  57 */     byte[] encoded = input.readByteArray(-length);
/*  58 */     return new String(encoded, StandardCharsets.UTF_16BE);
/*     */   }
/*     */   
/*     */   private static void writeUTF16String(HOutput output, String s) throws IOException {
/*  62 */     output.writeShort((short)s.length());
/*  63 */     output.stream.write(s.getBytes(StandardCharsets.UTF_16BE));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  72 */   private final Object cacheLock = new Object();
/*     */   
/*  74 */   private Result cache = null;
/*     */   
/*  76 */   private Exception cacheException = null;
/*     */   
/*  78 */   private Instant cacheTime = null;
/*     */   
/*     */   @LauncherAPI
/*     */   public ServerPinger(ClientProfile profile) {
/*  82 */     this.address = Objects.<InetSocketAddress>requireNonNull(profile.getServerSocketAddress(), "address");
/*  83 */     this.version = Objects.<ClientProfile.Version>requireNonNull(profile.getVersion(), "version");
/*  84 */     this.servers = Objects.<List<String>>requireNonNull(profile.getServers(), "servers");
/*     */   }
/*     */   
/*     */   private Result doPing() throws IOException {
/*  88 */     return doPing(Integer.valueOf(IOHelper.HTTP_TIMEOUT));
/*     */   }
/*     */   
/*     */   private Result doPing(Integer timeout) throws IOException {
/*  92 */     int online = 0;
/*  93 */     int maxOnline = 0;
/*     */     
/*  95 */     if (!this.servers.isEmpty()) {
/*  96 */       for (String server : this.servers) {
/*     */         try {
/*  98 */           String[] info = server.split(":");
/*     */           
/* 100 */           try (Socket socket = IOHelper.newSocket()) {
/* 101 */             socket1.connect(IOHelper.resolve(InetSocketAddress.createUnresolved(info[0], Integer.parseInt(info[1]))), timeout.intValue());
/* 102 */             try(HInput input = new HInput(socket1.getInputStream()); HOutput output = new HOutput(socket1.getOutputStream())) {
/* 103 */               Result r = (this.version.compareTo((Enum)ClientProfile.Version.MC172) >= 0) ? modernPing(input, output) : legacyPing(input, output, (this.version.compareTo((Enum)ClientProfile.Version.MC164) >= 0));
/*     */               
/* 105 */               online += r.onlinePlayers;
/* 106 */               maxOnline += r.maxPlayers;
/*     */             } 
/*     */           } 
/* 109 */         } catch (Throwable throwable) {}
/*     */       } 
/*     */       
/* 112 */       return new Result(online, maxOnline, online + "/" + maxOnline);
/*     */     } 
/* 114 */     try (Socket socket = IOHelper.newSocket()) {
/* 115 */       socket.connect(IOHelper.resolve(this.address), timeout.intValue());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Result legacyPing(HInput input, HOutput output, boolean mc16) throws IOException {
/* 124 */     output.writeUnsignedByte(254);
/* 125 */     output.writeUnsignedByte(1);
/* 126 */     if (mc16) {
/* 127 */       byte[] customPayloadPacket; output.writeUnsignedByte(250);
/* 128 */       writeUTF16String(output, "MC|PingHost");
/*     */ 
/*     */ 
/*     */       
/* 132 */       try (ByteArrayOutputStream packetArray = IOHelper.newByteArrayOutput()) {
/* 133 */         try (HOutput packetOutput = new HOutput(packetArray)) {
/* 134 */           packetOutput.writeUnsignedByte(this.version.protocol);
/* 135 */           writeUTF16String(packetOutput, this.address.getHostString());
/* 136 */           packetOutput.writeInt(this.address.getPort());
/*     */         } 
/* 138 */         customPayloadPacket = packetArray.toByteArray();
/*     */       } 
/*     */ 
/*     */       
/* 142 */       output.writeShort((short)customPayloadPacket.length);
/* 143 */       output.stream.write(customPayloadPacket);
/*     */     } 
/* 145 */     output.flush();
/*     */ 
/*     */     
/* 148 */     int kickPacketID = input.readUnsignedByte();
/* 149 */     if (kickPacketID != 255) {
/* 150 */       throw new IOException("Illegal kick packet ID: " + kickPacketID);
/*     */     }
/*     */     
/* 153 */     String response = readUTF16String(input);
/*     */     
/* 155 */     String[] splitted = LEGACY_PING_HOST_DELIMETER.split(response);
/* 156 */     if (splitted.length != 6) {
/* 157 */       throw new IOException("Tokens count mismatch");
/*     */     }
/*     */     
/* 160 */     String magic = splitted[0];
/* 161 */     if (!magic.equals("§1"))
/* 162 */       throw new IOException("Magic file mismatch: " + magic); 
/* 163 */     int protocol = Integer.parseInt(splitted[1]);
/* 164 */     if (protocol != this.version.protocol)
/* 165 */       throw new IOException("Protocol mismatch: " + protocol); 
/* 166 */     String clientVersion = splitted[2];
/* 167 */     if (!clientVersion.equals(this.version.name))
/* 168 */       throw new IOException(String.format("Version mismatch: '%s'", new Object[] { clientVersion })); 
/* 169 */     int onlinePlayers = VerifyHelper.verifyInt(Integer.parseInt(splitted[4]), VerifyHelper.NOT_NEGATIVE, "onlinePlayers can't be < 0");
/*     */     
/* 171 */     int maxPlayers = VerifyHelper.verifyInt(Integer.parseInt(splitted[5]), VerifyHelper.NOT_NEGATIVE, "maxPlayers can't be < 0");
/*     */ 
/*     */ 
/*     */     
/* 175 */     return new Result(onlinePlayers, maxPlayers, response);
/*     */   }
/*     */   
/*     */   private Result modernPing(HInput input, HOutput output) throws IOException {
/*     */     byte[] handshakePacket;
/*     */     String response;
/* 181 */     try (ByteArrayOutputStream packetArray = IOHelper.newByteArrayOutput()) {
/* 182 */       try (HOutput packetOutput = new HOutput(packetArray)) {
/* 183 */         packetOutput.writeVarInt(0);
/* 184 */         packetOutput.writeVarInt(this.version.protocol);
/* 185 */         packetOutput.writeString(this.address.getHostString(), 0);
/* 186 */         packetOutput.writeShort((short)this.address.getPort());
/* 187 */         packetOutput.writeVarInt(1);
/*     */       } 
/* 189 */       handshakePacket = packetArray.toByteArray();
/*     */     } 
/*     */ 
/*     */     
/* 193 */     output.writeByteArray(handshakePacket, 65535);
/*     */ 
/*     */     
/* 196 */     output.writeVarInt(1);
/* 197 */     output.writeVarInt(0);
/* 198 */     output.flush();
/*     */ 
/*     */     
/* 201 */     int ab = 0;
/* 202 */     while (ab <= 0) {
/* 203 */       ab = IOHelper.verifyLength(input.readVarInt(), 65535);
/*     */     }
/*     */ 
/*     */     
/* 207 */     byte[] statusPacket = input.readByteArray(-ab);
/* 208 */     try (HInput packetInput = new HInput(statusPacket)) {
/* 209 */       int statusPacketID = packetInput.readVarInt();
/* 210 */       if (statusPacketID != 0)
/* 211 */         throw new IOException("Illegal status packet ID: " + statusPacketID); 
/* 212 */       response = packetInput.readString(65535);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 217 */     JsonObject object = this.parser.parse(response).getAsJsonObject();
/* 218 */     JsonObject playersObject = object.get("players").getAsJsonObject();
/* 219 */     int online = playersObject.get("online").getAsInt();
/* 220 */     int max = playersObject.get("max").getAsInt();
/*     */ 
/*     */     
/* 223 */     return new Result(online, max, response);
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Result ping() throws IOException {
/* 228 */     Instant now = Instant.now();
/*     */     
/* 230 */     synchronized (this.cacheLock) {
/*     */       
/* 232 */       if (this.cacheTime == null || Duration.between(this.cacheTime, now).toMillis() >= IOHelper.SOCKET_TIMEOUT) {
/* 233 */         this.cacheTime = now;
/*     */         try {
/* 235 */           this.cache = doPing();
/* 236 */           this.cacheException = null;
/* 237 */         } catch (IOException|IllegalArgumentException e) {
/* 238 */           this.cache = null;
/* 239 */           this.cacheException = e;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 244 */       if (this.cache == null) {
/* 245 */         if (this.cacheException instanceof IOException)
/* 246 */           throw (IOException)this.cacheException; 
/* 247 */         if (this.cacheException instanceof IllegalArgumentException)
/* 248 */           throw (IllegalArgumentException)this.cacheException; 
/* 249 */         this.cacheException = new IOException("Unavailable");
/* 250 */         throw (IOException)this.cacheException;
/*     */       } 
/*     */ 
/*     */       
/* 254 */       return this.cache;
/*     */     } 
/*     */   }
/*     */   
/*     */   @LauncherAPI
/*     */   public Result ping(int timeout) throws IOException {
/* 260 */     Instant now = Instant.now();
/*     */     
/* 262 */     synchronized (this.cacheLock) {
/*     */       
/* 264 */       if (this.cacheTime == null || Duration.between(this.cacheTime, now).toMillis() >= IOHelper.SOCKET_TIMEOUT) {
/* 265 */         this.cacheTime = now;
/*     */         try {
/* 267 */           this.cache = doPing(Integer.valueOf(timeout));
/* 268 */           this.cacheException = null;
/* 269 */         } catch (IOException|IllegalArgumentException e) {
/* 270 */           this.cache = null;
/* 271 */           this.cacheException = e;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 276 */       if (this.cache == null) {
/* 277 */         if (this.cacheException instanceof IOException)
/* 278 */           throw (IOException)this.cacheException; 
/* 279 */         if (this.cacheException instanceof IllegalArgumentException)
/* 280 */           throw (IllegalArgumentException)this.cacheException; 
/* 281 */         this.cacheException = new IOException("Unavailable");
/* 282 */         throw (IOException)this.cacheException;
/*     */       } 
/*     */ 
/*     */       
/* 286 */       return this.cache;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Main\AppData\Roaming\StreamCraf\\updates\Launcher.jar!\pro\gravit\launcher\ServerPinger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */