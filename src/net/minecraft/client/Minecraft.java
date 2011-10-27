// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.client;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import net.minecraft.src.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.glu.GLU;

import com.amee.minecraft.CarbonCounter;

// Referenced classes of package net.minecraft.client:
//            MinecraftApplet

public abstract class Minecraft
    implements Runnable
{

    public Minecraft(Component component, Canvas canvas, MinecraftApplet minecraftapplet, int i, int j, boolean flag)
    {
        fullscreen = false;
        hasCrashed = false;
        timer = new Timer(20F);
        session = null;
        hideQuitButton = true;
        isGamePaused = false;
        currentScreen = null;
        ticksRan = 0;
        leftClickCounter = 0;
        guiAchievement = new GuiAchievement(this);
        skipRenderWorld = false;
        playerModelBiped = new ModelBiped(0.0F);
        objectMouseOver = null;
        sndManager = new SoundManager();
        field_35001_ab = 0;
        textureWaterFX = new TextureWaterFX();
        textureLavaFX = new TextureLavaFX();
        running = true;
        debug = "";
        isTakingScreenshot = false;
        prevFrameTime = -1L;
        inGameHasFocus = false;
        isRaining = false;
        systemTime = System.currentTimeMillis();
        joinPlayerCounter = 0;
        StatList.func_27360_a();
        tempDisplayHeight = j;
        fullscreen = flag;
        mcApplet = minecraftapplet;
        new ThreadSleepForever(this, "Timer hack thread");
        mcCanvas = canvas;
        displayWidth = i;
        displayHeight = j;
        fullscreen = flag;
        if(minecraftapplet == null || "true".equals(minecraftapplet.getParameter("stand-alone")))
        {
            hideQuitButton = false;
        }
        theMinecraft = this;
    }

    public void onMinecraftCrash(UnexpectedThrowable unexpectedthrowable)
    {
        hasCrashed = true;
        displayUnexpectedThrowable(unexpectedthrowable);
    }

    public abstract void displayUnexpectedThrowable(UnexpectedThrowable unexpectedthrowable);

    public void setServer(String s, int i)
    {
        serverName = s;
        serverPort = i;
    }

    public void startGame()
        throws LWJGLException
    {
        if(mcCanvas != null)
        {
            Graphics g = mcCanvas.getGraphics();
            if(g != null)
            {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, displayWidth, displayHeight);
                g.dispose();
            }
            Display.setParent(mcCanvas);
        } else
        if(fullscreen)
        {
            Display.setFullscreen(true);
            displayWidth = Display.getDisplayMode().getWidth();
            displayHeight = Display.getDisplayMode().getHeight();
            if(displayWidth <= 0)
            {
                displayWidth = 1;
            }
            if(displayHeight <= 0)
            {
                displayHeight = 1;
            }
        } else
        {
            Display.setDisplayMode(new DisplayMode(displayWidth, displayHeight));
        }
        Display.setTitle("Minecraft Minecraft Beta 1.8.1");
        try
        {
            PixelFormat pixelformat = new PixelFormat();
            pixelformat = pixelformat.withDepthBits(24);
            Display.create(pixelformat);
        }
        catch(LWJGLException lwjglexception)
        {
            lwjglexception.printStackTrace();
            try
            {
                Thread.sleep(1000L);
            }
            catch(InterruptedException interruptedexception) { }
            Display.create();
        }
        mcDataDir = getMinecraftDir();
        saveLoader = new SaveConverterMcRegion(new File(mcDataDir, "saves"));
        gameSettings = new GameSettings(this, mcDataDir);
        texturePackList = new TexturePackList(this, mcDataDir);
        renderEngine = new RenderEngine(texturePackList, gameSettings);
        loadScreen();
        fontRenderer = new FontRenderer(gameSettings, "/font/default.png", renderEngine);
        ColorizerWater.func_28182_a(renderEngine.getTextureContents("/misc/watercolor.png"));
        ColorizerGrass.func_28181_a(renderEngine.getTextureContents("/misc/grasscolor.png"));
        ColorizerFoliage.func_28152_a(renderEngine.getTextureContents("/misc/foliagecolor.png"));
        entityRenderer = new EntityRenderer(this);
        RenderManager.instance.itemRenderer = new ItemRenderer(this);
        statFileWriter = new StatFileWriter(session, mcDataDir);
        AchievementList.openInventory.setStatStringFormatter(new StatStringFormatKeyInv(this));
        loadScreen();
        Keyboard.create();
        Mouse.create();
        mouseHelper = new MouseHelper(mcCanvas);
        try
        {
            Controllers.create();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        checkGLError("Pre startup");
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glShadeModel(7425 /*GL_SMOOTH*/);
        GL11.glClearDepth(1.0D);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glCullFace(1029 /*GL_BACK*/);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        checkGLError("Startup");
        glCapabilities = new OpenGlCapsChecker();
        sndManager.loadSoundSettings(gameSettings);
        renderEngine.registerTextureFX(textureLavaFX);
        renderEngine.registerTextureFX(textureWaterFX);
        renderEngine.registerTextureFX(new TexturePortalFX());
        renderEngine.registerTextureFX(new TextureCompassFX(this));
        renderEngine.registerTextureFX(new TextureWatchFX(this));
        renderEngine.registerTextureFX(new TextureWaterFlowFX());
        renderEngine.registerTextureFX(new TextureLavaFlowFX());
        renderEngine.registerTextureFX(new TextureFlamesFX(0));
        renderEngine.registerTextureFX(new TextureFlamesFX(1));
        renderGlobal = new RenderGlobal(this, renderEngine);
        GL11.glViewport(0, 0, displayWidth, displayHeight);
        effectRenderer = new EffectRenderer(theWorld, renderEngine);
        try
        {
            downloadResourcesThread = new ThreadDownloadResources(mcDataDir, this);
            downloadResourcesThread.start();
        }
        catch(Exception exception1) { }
        checkGLError("Post startup");
        ingameGUI = new GuiIngame(this);
        if(serverName != null)
        {
            displayGuiScreen(new GuiConnecting(this, serverName, serverPort));
        } else
        {
            displayGuiScreen(new GuiMainMenu());
        }
        loadingScreen = new LoadingScreenRenderer(this);
    }

    private void loadScreen()
        throws LWJGLException
    {
        ScaledResolution scaledresolution = new ScaledResolution(gameSettings, displayWidth, displayHeight);
        GL11.glClear(16640);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, scaledresolution.scaledWidthD, scaledresolution.scaledHeightD, 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);
        GL11.glViewport(0, 0, displayWidth, displayHeight);
        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
        GL11.glDisable(2912 /*GL_FOG*/);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, renderEngine.getTexture("/title/mojang.png"));
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_I(0xffffff);
        tessellator.addVertexWithUV(0.0D, displayHeight, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(displayWidth, displayHeight, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
        char c = '\u0100';
        char c1 = '\u0100';
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.setColorOpaque_I(0xffffff);
        scaledTessellator((scaledresolution.getScaledWidth() - c) / 2, (scaledresolution.getScaledHeight() - c1) / 2, 0, 0, c, c1);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glDisable(2912 /*GL_FOG*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glAlphaFunc(516, 0.1F);
        Display.swapBuffers();
    }

    public void scaledTessellator(int i, int j, int k, int l, int i1, int j1)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(i + 0, j + j1, 0.0D, (float)(k + 0) * f, (float)(l + j1) * f1);
        tessellator.addVertexWithUV(i + i1, j + j1, 0.0D, (float)(k + i1) * f, (float)(l + j1) * f1);
        tessellator.addVertexWithUV(i + i1, j + 0, 0.0D, (float)(k + i1) * f, (float)(l + 0) * f1);
        tessellator.addVertexWithUV(i + 0, j + 0, 0.0D, (float)(k + 0) * f, (float)(l + 0) * f1);
        tessellator.draw();
    }

    public static File getMinecraftDir()
    {
        if(minecraftDir == null)
        {
            minecraftDir = getAppDir("minecraft");
        }
        return minecraftDir;
    }

    public static File getAppDir(String s)
    {
        String s1 = System.getProperty("user.home", ".");
        File file;
        switch(EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()])
        {
        case 1: // '\001'
        case 2: // '\002'
            file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
            break;

        case 3: // '\003'
            String s2 = System.getenv("APPDATA");
            if(s2 != null)
            {
                file = new File(s2, (new StringBuilder()).append(".").append(s).append('/').toString());
            } else
            {
                file = new File(s1, (new StringBuilder()).append('.').append(s).append('/').toString());
            }
            break;

        case 4: // '\004'
            file = new File(s1, (new StringBuilder()).append("Library/Application Support/").append(s).toString());
            break;

        default:
            file = new File(s1, (new StringBuilder()).append(s).append('/').toString());
            break;
        }
        if(!file.exists() && !file.mkdirs())
        {
            throw new RuntimeException((new StringBuilder()).append("The working directory could not be created: ").append(file).toString());
        } else
        {
            return file;
        }
    }

    private static EnumOS2 getOs()
    {
        String s = System.getProperty("os.name").toLowerCase();
        if(s.contains("win"))
        {
            return EnumOS2.windows;
        }
        if(s.contains("mac"))
        {
            return EnumOS2.macos;
        }
        if(s.contains("solaris"))
        {
            return EnumOS2.solaris;
        }
        if(s.contains("sunos"))
        {
            return EnumOS2.solaris;
        }
        if(s.contains("linux"))
        {
            return EnumOS2.linux;
        }
        if(s.contains("unix"))
        {
            return EnumOS2.linux;
        } else
        {
            return EnumOS2.unknown;
        }
    }

    public ISaveFormat getSaveLoader()
    {
        return saveLoader;
    }

    public void displayGuiScreen(GuiScreen guiscreen)
    {
        if(currentScreen instanceof GuiUnused)
        {
            return;
        }
        if(currentScreen != null)
        {
            currentScreen.onGuiClosed();
        }
        if(guiscreen instanceof GuiMainMenu)
        {
            statFileWriter.func_27175_b();
        }
        statFileWriter.syncStats();
        if(guiscreen == null && theWorld == null)
        {
            guiscreen = new GuiMainMenu();
        } else
        if(guiscreen == null && thePlayer.health <= 0)
        {
            guiscreen = new GuiGameOver();
        }
        if(guiscreen instanceof GuiMainMenu)
        {
            ingameGUI.clearChatMessages();
        }
        currentScreen = guiscreen;
        if(guiscreen != null)
        {
            setIngameNotInFocus();
            ScaledResolution scaledresolution = new ScaledResolution(gameSettings, displayWidth, displayHeight);
            int i = scaledresolution.getScaledWidth();
            int j = scaledresolution.getScaledHeight();
            guiscreen.setWorldAndResolution(this, i, j);
            skipRenderWorld = false;
        } else
        {
            setIngameFocus();
        }
    }

    private void checkGLError(String s)
    {
        int i = GL11.glGetError();
        if(i != 0)
        {
            String s1 = GLU.gluErrorString(i);
            System.out.println("########## GL ERROR ##########");
            System.out.println((new StringBuilder()).append("@ ").append(s).toString());
            System.out.println((new StringBuilder()).append(i).append(": ").append(s1).toString());
        }
    }

    public void shutdownMinecraftApplet()
    {
        try
        {
            statFileWriter.func_27175_b();
            statFileWriter.syncStats();
            if(mcApplet != null)
            {
                mcApplet.clearApplet();
            }
            try
            {
                if(downloadResourcesThread != null)
                {
                    downloadResourcesThread.closeMinecraft();
                }
            }
            catch(Exception exception) { }
            System.out.println("Stopping!");
            try
            {
                changeWorld1(null);
            }
            catch(Throwable throwable) { }
            try
            {
                GLAllocation.deleteTexturesAndDisplayLists();
            }
            catch(Throwable throwable1) { }
            sndManager.closeMinecraft();
            Mouse.destroy();
            Keyboard.destroy();
        }
        finally
        {
            Display.destroy();
            if(!hasCrashed)
            {
                System.exit(0);
            }
        }
        System.gc();
    }

    public void run()
    {
        running = true;
        try
        {
            startGame();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            onMinecraftCrash(new UnexpectedThrowable("Failed to start game", exception));
            return;
        }
        try
        {
            long l = System.currentTimeMillis();
            int i = 0;
            do
            {
                if(!running)
                {
                    break;
                }
                try
                {
                    if(mcApplet != null && !mcApplet.isActive())
                    {
                        break;
                    }
                    AxisAlignedBB.clearBoundingBoxPool();
                    Vec3D.initialize();
                    if(mcCanvas == null && Display.isCloseRequested())
                    {
                        shutdown();
                    }
                    if(isGamePaused && theWorld != null)
                    {
                        float f = timer.renderPartialTicks;
                        timer.updateTimer();
                        timer.renderPartialTicks = f;
                    } else
                    {
                        timer.updateTimer();
                    }
                    long l1 = System.nanoTime();
                    for(int j = 0; j < timer.elapsedTicks; j++)
                    {
                        ticksRan++;
                        try
                        {
                            runTick();
                            continue;
                        }
                        catch(MinecraftException minecraftexception1)
                        {
                            theWorld = null;
                        }
                        changeWorld1(null);
                        displayGuiScreen(new GuiConflictWarning());
                    }

                    long l2 = System.nanoTime() - l1;
                    checkGLError("Pre render");
                    RenderBlocks.fancyGrass = gameSettings.fancyGraphics;
                    sndManager.func_338_a(thePlayer, timer.renderPartialTicks);
                    GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
                    if(theWorld != null)
                    {
                        theWorld.updatingLighting();
                    }
                    if(!Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    if(thePlayer != null && thePlayer.isEntityInsideOpaqueBlock())
                    {
                        gameSettings.thirdPersonView = false;
                    }
                    if(!skipRenderWorld)
                    {
                        if(playerController != null)
                        {
                            playerController.setPartialTime(timer.renderPartialTicks);
                        }
                        entityRenderer.updateCameraAndRender(timer.renderPartialTicks);
                    }
                    if(!Display.isActive() && fullscreen)
                    {
                        toggleFullscreen();
                    }
                    if(gameSettings.showDebugInfo)
                    {
                        displayDebugInfo(l2);
                    } else
                    {
                        prevFrameTime = System.nanoTime();
                    }
                    guiAchievement.updateAchievementWindow();
                    Thread.yield();
                    if(Keyboard.isKeyDown(65))
                    {
                        Display.update();
                    }
                    screenshotListener();
                    if(mcCanvas != null && !fullscreen && (mcCanvas.getWidth() != displayWidth || mcCanvas.getHeight() != displayHeight))
                    {
                        displayWidth = mcCanvas.getWidth();
                        displayHeight = mcCanvas.getHeight();
                        if(displayWidth <= 0)
                        {
                            displayWidth = 1;
                        }
                        if(displayHeight <= 0)
                        {
                            displayHeight = 1;
                        }
                        resize(displayWidth, displayHeight);
                    }
                    checkGLError("Post render");
                    i++;
                    isGamePaused = !isMultiplayerWorld() && currentScreen != null && currentScreen.doesGuiPauseGame();
                    while(System.currentTimeMillis() >= l + 1000L) 
                    {
                        debug = (new StringBuilder()).append(i).append(" fps, ").append(WorldRenderer.chunksUpdated).append(" chunk updates").toString();
                        WorldRenderer.chunksUpdated = 0;
                        l += 1000L;
                        i = 0;
                    }
                }
                catch(MinecraftException minecraftexception)
                {
                    theWorld = null;
                    changeWorld1(null);
                    displayGuiScreen(new GuiConflictWarning());
                }
                catch(OutOfMemoryError outofmemoryerror)
                {
                    func_28002_e();
                    displayGuiScreen(new GuiErrorScreen());
                    System.gc();
                }
            } while(true);
        }
        catch(MinecraftError minecrafterror) { }
        catch(Throwable throwable)
        {
            func_28002_e();
            throwable.printStackTrace();
            onMinecraftCrash(new UnexpectedThrowable("Unexpected error", throwable));
        }
        finally
        {
            shutdownMinecraftApplet();
        }
    }

    public void func_28002_e()
    {
        try
        {
            field_28006_b = new byte[0];
            renderGlobal.func_28137_f();
        }
        catch(Throwable throwable) { }
        try
        {
            System.gc();
            AxisAlignedBB.clearBoundingBoxes();
            Vec3D.clearVectorList();
        }
        catch(Throwable throwable1) { }
        try
        {
            System.gc();
            changeWorld1(null);
        }
        catch(Throwable throwable2) { }
        System.gc();
    }

    private void screenshotListener()
    {
        if(Keyboard.isKeyDown(60))
        {
            if(!isTakingScreenshot)
            {
                isTakingScreenshot = true;
                ingameGUI.addChatMessage(ScreenShotHelper.saveScreenshot(minecraftDir, displayWidth, displayHeight));
            }
        } else
        {
            isTakingScreenshot = false;
        }
    }

    private void displayDebugInfo(long l)
    {
        long l1 = 0xfe502aL;
        if(prevFrameTime == -1L)
        {
            prevFrameTime = System.nanoTime();
        }
        long l2 = System.nanoTime();
        tickTimes[numRecordedFrameTimes & frameTimes.length - 1] = l;
        frameTimes[numRecordedFrameTimes++ & frameTimes.length - 1] = l2 - prevFrameTime;
        prevFrameTime = l2;
        GL11.glClear(256);
        GL11.glMatrixMode(5889 /*GL_PROJECTION*/);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, displayWidth, displayHeight, 0.0D, 1000D, 3000D);
        GL11.glMatrixMode(5888 /*GL_MODELVIEW0_ARB*/);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000F);
        GL11.glLineWidth(1.0F);
        GL11.glDisable(3553 /*GL_TEXTURE_2D*/);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(7);
        int i = (int)(l1 / 0x30d40L);
        tessellator.setColorOpaque_I(0x20000000);
        tessellator.addVertex(0.0D, displayHeight - i, 0.0D);
        tessellator.addVertex(0.0D, displayHeight, 0.0D);
        tessellator.addVertex(frameTimes.length, displayHeight, 0.0D);
        tessellator.addVertex(frameTimes.length, displayHeight - i, 0.0D);
        tessellator.setColorOpaque_I(0x20200000);
        tessellator.addVertex(0.0D, displayHeight - i * 2, 0.0D);
        tessellator.addVertex(0.0D, displayHeight - i, 0.0D);
        tessellator.addVertex(frameTimes.length, displayHeight - i, 0.0D);
        tessellator.addVertex(frameTimes.length, displayHeight - i * 2, 0.0D);
        tessellator.draw();
        long l3 = 0L;
        for(int j = 0; j < frameTimes.length; j++)
        {
            l3 += frameTimes[j];
        }

        int k = (int)(l3 / 0x30d40L / (long)frameTimes.length);
        tessellator.startDrawing(7);
        tessellator.setColorOpaque_I(0x20400000);
        tessellator.addVertex(0.0D, displayHeight - k, 0.0D);
        tessellator.addVertex(0.0D, displayHeight, 0.0D);
        tessellator.addVertex(frameTimes.length, displayHeight, 0.0D);
        tessellator.addVertex(frameTimes.length, displayHeight - k, 0.0D);
        tessellator.draw();
        tessellator.startDrawing(1);
        for(int i1 = 0; i1 < frameTimes.length; i1++)
        {
            int j1 = ((i1 - numRecordedFrameTimes & frameTimes.length - 1) * 255) / frameTimes.length;
            int k1 = (j1 * j1) / 255;
            k1 = (k1 * k1) / 255;
            int i2 = (k1 * k1) / 255;
            i2 = (i2 * i2) / 255;
            if(frameTimes[i1] > l1)
            {
                tessellator.setColorOpaque_I(0xff000000 + k1 * 0x10000);
            } else
            {
                tessellator.setColorOpaque_I(0xff000000 + k1 * 256);
            }
            long l4 = frameTimes[i1] / 0x30d40L;
            long l5 = tickTimes[i1] / 0x30d40L;
            tessellator.addVertex((float)i1 + 0.5F, (float)((long)displayHeight - l4) + 0.5F, 0.0D);
            tessellator.addVertex((float)i1 + 0.5F, (float)displayHeight + 0.5F, 0.0D);
            tessellator.setColorOpaque_I(0xff000000 + k1 * 0x10000 + k1 * 256 + k1 * 1);
            tessellator.addVertex((float)i1 + 0.5F, (float)((long)displayHeight - l4) + 0.5F, 0.0D);
            tessellator.addVertex((float)i1 + 0.5F, (float)((long)displayHeight - (l4 - l5)) + 0.5F, 0.0D);
        }

        tessellator.draw();
        GL11.glEnable(3553 /*GL_TEXTURE_2D*/);
    }

    public void shutdown()
    {
        running = false;
    }

    public void setIngameFocus()
    {
        if(!Display.isActive())
        {
            return;
        }
        if(inGameHasFocus)
        {
            return;
        } else
        {
            inGameHasFocus = true;
            mouseHelper.grabMouseCursor();
            displayGuiScreen(null);
            leftClickCounter = 10000;
            return;
        }
    }

    public void setIngameNotInFocus()
    {
        if(!inGameHasFocus)
        {
            return;
        } else
        {
            KeyBinding.func_35959_a();
            inGameHasFocus = false;
            mouseHelper.ungrabMouseCursor();
            return;
        }
    }

    public void displayInGameMenu()
    {
        if(currentScreen != null)
        {
            return;
        } else
        {
            displayGuiScreen(new GuiIngameMenu());
            return;
        }
    }

    private void sendClickBlockToController(int i, boolean flag)
    {
        if(!flag)
        {
            leftClickCounter = 0;
        }
        if(i == 0 && leftClickCounter > 0)
        {
            return;
        }
        if(flag && objectMouseOver != null && objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && i == 0)
        {
            int j = objectMouseOver.blockX;
            int k = objectMouseOver.blockY;
            int l = objectMouseOver.blockZ;
            playerController.sendBlockRemoving(j, k, l, objectMouseOver.sideHit);
            if(thePlayer.func_35190_e(j, k, l))
            {
                effectRenderer.addBlockHitEffects(j, k, l, objectMouseOver.sideHit);
                thePlayer.swingItem();
            }
        } else
        {
            playerController.resetBlockRemoving();
        }
    }

    private void clickMouse(int i)
    {
        if(i == 0 && leftClickCounter > 0)
        {
            return;
        }
        if(i == 0)
        {
            thePlayer.swingItem();
        }
        if(i == 1)
        {
            field_35001_ab = 6;
        }
        boolean flag = true;
        ItemStack itemstack = thePlayer.inventory.getCurrentItem();
        if(objectMouseOver == null)
        {
            if(i == 0 && playerController.func_35641_g())
            {
                leftClickCounter = 10;
            }
        } else
        if(objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY)
        {
            if(i == 0)
            {
                playerController.attackEntity(thePlayer, objectMouseOver.entityHit);
            }
            if(i == 1)
            {
                playerController.interactWithEntity(thePlayer, objectMouseOver.entityHit);
            }
        } else
        if(objectMouseOver.typeOfHit == EnumMovingObjectType.TILE)
        {
            int j = objectMouseOver.blockX;
            int k = objectMouseOver.blockY;
            int l = objectMouseOver.blockZ;
            int i1 = objectMouseOver.sideHit;
            if(i == 0)
            {
                playerController.clickBlock(j, k, l, objectMouseOver.sideHit);
            } else
            {
                ItemStack itemstack2 = itemstack;
                int j1 = itemstack2 == null ? 0 : itemstack2.stackSize;
                if(playerController.sendPlaceBlock(thePlayer, theWorld, itemstack2, j, k, l, i1))
                {
                    flag = false;
                    thePlayer.swingItem();
                }
                if(itemstack2 == null)
                {
                    return;
                }
                if(itemstack2.stackSize == 0)
                {
                    thePlayer.inventory.mainInventory[thePlayer.inventory.currentItem] = null;
                } else
                if(itemstack2.stackSize != j1 || playerController.func_35640_h())
                {
                    entityRenderer.itemRenderer.func_9449_b();
                }
            }
        }
        if(flag && i == 1)
        {
            ItemStack itemstack1 = thePlayer.inventory.getCurrentItem();
            if(itemstack1 != null && playerController.sendUseItem(thePlayer, theWorld, itemstack1))
            {
                entityRenderer.itemRenderer.func_9450_c();
            }
        }
    }

    public void toggleFullscreen()
    {
        try
        {
            fullscreen = !fullscreen;
            if(fullscreen)
            {
                Display.setDisplayMode(Display.getDesktopDisplayMode());
                displayWidth = Display.getDisplayMode().getWidth();
                displayHeight = Display.getDisplayMode().getHeight();
                if(displayWidth <= 0)
                {
                    displayWidth = 1;
                }
                if(displayHeight <= 0)
                {
                    displayHeight = 1;
                }
            } else
            {
                if(mcCanvas != null)
                {
                    displayWidth = mcCanvas.getWidth();
                    displayHeight = mcCanvas.getHeight();
                } else
                {
                    displayWidth = tempDisplayWidth;
                    displayHeight = tempDisplayHeight;
                }
                if(displayWidth <= 0)
                {
                    displayWidth = 1;
                }
                if(displayHeight <= 0)
                {
                    displayHeight = 1;
                }
            }
            if(currentScreen != null)
            {
                resize(displayWidth, displayHeight);
            }
            Display.setFullscreen(fullscreen);
            Display.update();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    private void resize(int i, int j)
    {
        if(i <= 0)
        {
            i = 1;
        }
        if(j <= 0)
        {
            j = 1;
        }
        displayWidth = i;
        displayHeight = j;
        if(currentScreen != null)
        {
            ScaledResolution scaledresolution = new ScaledResolution(gameSettings, i, j);
            int k = scaledresolution.getScaledWidth();
            int l = scaledresolution.getScaledHeight();
            currentScreen.setWorldAndResolution(this, k, l);
        }
    }

    private void func_28001_B()
    {
        (new ThreadCheckHasPaid(this)).start();
    }

    public void runTick()
    {
        if(field_35001_ab > 0)
        {
            field_35001_ab--;
        }
        if(ticksRan == 6000)
        {
            func_28001_B();
        }
        statFileWriter.func_27178_d();
        ingameGUI.updateTick();
        entityRenderer.getMouseOver(1.0F);
        if(thePlayer != null)
        {
            net.minecraft.src.IChunkProvider ichunkprovider = theWorld.getIChunkProvider();
            if(ichunkprovider instanceof ChunkProviderLoadOrGenerate)
            {
                ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate)ichunkprovider;
                int j = MathHelper.floor_float((int)thePlayer.posX) >> 4;
                int i1 = MathHelper.floor_float((int)thePlayer.posZ) >> 4;
                chunkproviderloadorgenerate.setCurrentChunkOver(j, i1);
            }
        }
        if(!isGamePaused && theWorld != null)
        {
            playerController.updateController();
        }
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, renderEngine.getTexture("/terrain.png"));
        if(!isGamePaused)
        {
            renderEngine.updateDynamicTextures();
        }
        if(currentScreen == null && thePlayer != null)
        {
            if(thePlayer.health <= 0)
            {
                displayGuiScreen(null);
            } else
            if(thePlayer.isPlayerSleeping() && theWorld != null && theWorld.multiplayerWorld)
            {
                displayGuiScreen(new GuiSleepMP());
            }
        } else
        if(currentScreen != null && (currentScreen instanceof GuiSleepMP) && !thePlayer.isPlayerSleeping())
        {
            displayGuiScreen(null);
        }
        if(currentScreen != null)
        {
            leftClickCounter = 10000;
        }
        if(currentScreen != null)
        {
            currentScreen.handleInput();
            if(currentScreen != null)
            {
                currentScreen.guiParticles.update();
                currentScreen.updateScreen();
            }
        }
        if(currentScreen == null || currentScreen.allowUserInput)
        {
            do
            {
                if(!Mouse.next())
                {
                    break;
                }
                KeyBinding.func_35963_a(Mouse.getEventButton() - 100, Mouse.getEventButtonState());
                if(Mouse.getEventButtonState())
                {
                    KeyBinding.func_35960_a(Mouse.getEventButton() - 100);
                }
                long l = System.currentTimeMillis() - systemTime;
                if(l <= 200L)
                {
                    int k = Mouse.getEventDWheel();
                    if(k != 0)
                    {
                        thePlayer.inventory.changeCurrentItem(k);
                        if(gameSettings.field_22275_C)
                        {
                            if(k > 0)
                            {
                                k = 1;
                            }
                            if(k < 0)
                            {
                                k = -1;
                            }
                            gameSettings.field_22272_F += (float)k * 0.25F;
                        }
                    }
                    if(currentScreen == null)
                    {
                        if(!inGameHasFocus && Mouse.getEventButtonState())
                        {
                            setIngameFocus();
                        }
                    } else
                    if(currentScreen != null)
                    {
                        currentScreen.handleMouseInput();
                    }
                }
            } while(true);
            if(leftClickCounter > 0)
            {
                leftClickCounter--;
            }
            do
            {
                if(!Keyboard.next())
                {
                    break;
                }
                KeyBinding.func_35963_a(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if(Keyboard.getEventKeyState())
                {
                    KeyBinding.func_35960_a(Keyboard.getEventKey());
                }
                if(Keyboard.getEventKeyState())
                {
                    if(Keyboard.getEventKey() == 87)
                    {
                        toggleFullscreen();
                    } else
                    {
                        if(currentScreen != null)
                        {
                            currentScreen.handleKeyboardInput();
                        } else
                        {
                            if(Keyboard.getEventKey() == 1)
                            {
                                displayInGameMenu();
                            }
                            if(Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61))
                            {
                                forceReload();
                            }
                            if(Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61))
                            {
                                boolean flag = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                                gameSettings.setOptionValue(EnumOptions.RENDER_DISTANCE, flag ? -1 : 1);
                            }
                            if(Keyboard.getEventKey() == 59)
                            {
                                gameSettings.hideGUI = !gameSettings.hideGUI;
                            }
                            if(Keyboard.getEventKey() == 61)
                            {
                                gameSettings.showDebugInfo = !gameSettings.showDebugInfo;
                            }
                            if(Keyboard.getEventKey() == 63)
                            {
                                gameSettings.thirdPersonView = !gameSettings.thirdPersonView;
                            }
                        }
                        int i = 0;
                        while(i < 9) 
                        {
                            if(Keyboard.getEventKey() == 2 + i)
                            {
                                thePlayer.inventory.currentItem = i;
                            }
                            i++;
                        }
                    }
                }
            } while(true);
            for(; gameSettings.keyBindInventory.func_35962_c(); displayGuiScreen(new GuiInventory(thePlayer))) { }
            for(; gameSettings.keyBindDrop.func_35962_c(); thePlayer.dropCurrentItem()) { }
            for(; isMultiplayerWorld() && gameSettings.keyBindChat.func_35962_c(); displayGuiScreen(new GuiChat())) { }
            if(thePlayer.func_35196_Z())
            {
                if(!gameSettings.field_35381_w.field_35965_e)
                {
                    playerController.func_35638_c(thePlayer);
                }
            } else
            {
                for(; gameSettings.field_35382_v.func_35962_c(); clickMouse(0)) { }
                for(; gameSettings.field_35381_w.func_35962_c(); clickMouse(1)) { }
                for(; gameSettings.field_35383_y.func_35962_c(); clickMiddleMouseButton()) { }
            }
            if(gameSettings.field_35381_w.field_35965_e && field_35001_ab == 0 && !thePlayer.func_35196_Z())
            {
                clickMouse(1);
            }
            sendClickBlockToController(0, currentScreen == null && gameSettings.field_35382_v.field_35965_e && inGameHasFocus);
        }
        if(theWorld != null)
        {
            if(thePlayer != null)
            {
                joinPlayerCounter++;
                if(joinPlayerCounter == 30)
                {
                    joinPlayerCounter = 0;
                    theWorld.joinEntityInSurroundings(thePlayer);
                }
            }
            theWorld.difficultySetting = gameSettings.difficulty;
            if(theWorld.multiplayerWorld)
            {
                theWorld.difficultySetting = 1;
            }
            if(!isGamePaused)
            {
                entityRenderer.updateRenderer();
            }
            if(!isGamePaused)
            {
                renderGlobal.updateClouds();
            }
            if(!isGamePaused)
            {
                if(theWorld.field_27172_i > 0)
                {
                    theWorld.field_27172_i--;
                }
                theWorld.updateEntities();
            }
            if(!isGamePaused || isMultiplayerWorld())
            {
                theWorld.setAllowedMobSpawns(gameSettings.difficulty > 0, true);
                theWorld.tick();
            }
            if(!isGamePaused && theWorld != null)
            {
                theWorld.randomDisplayUpdates(MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ));
            }
            if(!isGamePaused)
            {
                effectRenderer.updateEffects();
            }
        }
        systemTime = System.currentTimeMillis();
    }

    private void forceReload()
    {
        System.out.println("FORCING RELOAD!");
        sndManager = new SoundManager();
        sndManager.loadSoundSettings(gameSettings);
        downloadResourcesThread.reloadResources();
    }

    public boolean isMultiplayerWorld()
    {
        return theWorld != null && theWorld.multiplayerWorld;
    }

    public void startWorld(String s, String s1, WorldSettings worldsettings)
    {
        changeWorld1(null);
        System.gc();
        if(saveLoader.isOldMapFormat(s))
        {
            convertMapFormat(s, s1);
        } else
        {
            if(loadingScreen != null)
            {
                loadingScreen.printText("Switching level");
                loadingScreen.displayLoadingString("");
            }
            net.minecraft.src.ISaveHandler isavehandler = saveLoader.getSaveLoader(s, false);
            World world = null;
            world = new World(isavehandler, s1, worldsettings);
            if(world.isNewWorld)
            {
                statFileWriter.readStat(StatList.createWorldStat, 1);
                statFileWriter.readStat(StatList.startGameStat, 1);
                changeWorld2(world, "Generating level");
            } else
            {
                statFileWriter.readStat(StatList.loadWorldStat, 1);
                statFileWriter.readStat(StatList.startGameStat, 1);
                changeWorld2(world, "Loading level");
            }
            CarbonCounter.init(ingameGUI);
        }
    }

    public void usePortal()
    {
        System.out.println("Toggling dimension!!");
        if(thePlayer.dimension == -1)
        {
            thePlayer.dimension = 0;
        } else
        {
            thePlayer.dimension = -1;
        }
        theWorld.setEntityDead(thePlayer);
        thePlayer.isDead = false;
        double d = thePlayer.posX;
        double d1 = thePlayer.posZ;
        double d2 = 8D;
        if(thePlayer.dimension == -1)
        {
            d /= d2;
            d1 /= d2;
            thePlayer.setLocationAndAngles(d, thePlayer.posY, d1, thePlayer.rotationYaw, thePlayer.rotationPitch);
            if(thePlayer.isEntityAlive())
            {
                theWorld.updateEntityWithOptionalForce(thePlayer, false);
            }
            World world = null;
            world = new World(theWorld, WorldProvider.getProviderForDimension(-1));
            changeWorld(world, "Entering the Nether", thePlayer);
        } else
        {
            d *= d2;
            d1 *= d2;
            thePlayer.setLocationAndAngles(d, thePlayer.posY, d1, thePlayer.rotationYaw, thePlayer.rotationPitch);
            if(thePlayer.isEntityAlive())
            {
                theWorld.updateEntityWithOptionalForce(thePlayer, false);
            }
            World world1 = null;
            world1 = new World(theWorld, WorldProvider.getProviderForDimension(0));
            changeWorld(world1, "Leaving the Nether", thePlayer);
        }
        thePlayer.worldObj = theWorld;
        if(thePlayer.isEntityAlive())
        {
            thePlayer.setLocationAndAngles(d, thePlayer.posY, d1, thePlayer.rotationYaw, thePlayer.rotationPitch);
            theWorld.updateEntityWithOptionalForce(thePlayer, false);
            (new Teleporter()).placeInPortal(theWorld, thePlayer);
        }
    }

    public void changeWorld1(World world)
    {
        changeWorld2(world, "");
    }

    public void changeWorld2(World world, String s)
    {
        changeWorld(world, s, null);
    }

    public void changeWorld(World world, String s, EntityPlayer entityplayer)
    {
        statFileWriter.func_27175_b();
        statFileWriter.syncStats();
        renderViewEntity = null;
        if(loadingScreen != null)
        {
            loadingScreen.printText(s);
            loadingScreen.displayLoadingString("");
        }
        sndManager.playStreaming(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        if(theWorld != null)
        {
            theWorld.saveWorldIndirectly(loadingScreen);
        }
        theWorld = world;
        if(world != null)
        {
            if(playerController != null)
            {
                playerController.func_717_a(world);
            }
            if(!isMultiplayerWorld())
            {
                if(entityplayer == null)
                {
                    thePlayer = (EntityPlayerSP)world.func_4085_a(net.minecraft.src.EntityPlayerSP.class);
                }
            } else
            if(thePlayer != null)
            {
                thePlayer.preparePlayerToSpawn();
                if(world != null)
                {
                    world.entityJoinedWorld(thePlayer);
                }
            }
            if(!world.multiplayerWorld)
            {
                preloadWorld(s);
            }
            if(thePlayer == null)
            {
                thePlayer = (EntityPlayerSP)playerController.createPlayer(world);
                thePlayer.preparePlayerToSpawn();
                playerController.flipPlayer(thePlayer);
            }
            thePlayer.movementInput = new MovementInputFromOptions(gameSettings);
            if(renderGlobal != null)
            {
                renderGlobal.changeWorld(world);
            }
            if(effectRenderer != null)
            {
                effectRenderer.clearEffects(world);
            }
            playerController.func_6473_b(thePlayer);
            if(entityplayer != null)
            {
                world.emptyMethod1();
            }
            net.minecraft.src.IChunkProvider ichunkprovider = world.getIChunkProvider();
            if(ichunkprovider instanceof ChunkProviderLoadOrGenerate)
            {
                ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate)ichunkprovider;
                int i = MathHelper.floor_float((int)thePlayer.posX) >> 4;
                int j = MathHelper.floor_float((int)thePlayer.posZ) >> 4;
                chunkproviderloadorgenerate.setCurrentChunkOver(i, j);
            }
            world.spawnPlayerWithLoadedChunks(thePlayer);
            if(world.isNewWorld)
            {
                world.saveWorldIndirectly(loadingScreen);
            }
            renderViewEntity = thePlayer;
        } else
        {
            thePlayer = null;
        }
        System.gc();
        systemTime = 0L;
    }

    private void convertMapFormat(String s, String s1)
    {
        loadingScreen.printText((new StringBuilder()).append("Converting World to ").append(saveLoader.func_22178_a()).toString());
        loadingScreen.displayLoadingString("This may take a while :)");
        saveLoader.convertMapFormat(s, loadingScreen);
        startWorld(s, s1, new WorldSettings(0L, 0, true));
    }

    private void preloadWorld(String s)
    {
        if(loadingScreen != null)
        {
            loadingScreen.printText(s);
            loadingScreen.displayLoadingString("Building terrain");
        }
        char c = '\200';
        if(playerController.func_35643_e())
        {
            c = '@';
        }
        int i = 0;
        int j = (c * 2) / 16 + 1;
        j *= j;
        net.minecraft.src.IChunkProvider ichunkprovider = theWorld.getIChunkProvider();
        ChunkCoordinates chunkcoordinates = theWorld.getSpawnPoint();
        if(thePlayer != null)
        {
            chunkcoordinates.posX = (int)thePlayer.posX;
            chunkcoordinates.posZ = (int)thePlayer.posZ;
        }
        if(ichunkprovider instanceof ChunkProviderLoadOrGenerate)
        {
            ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate)ichunkprovider;
            chunkproviderloadorgenerate.setCurrentChunkOver(chunkcoordinates.posX >> 4, chunkcoordinates.posZ >> 4);
        }
        for(int k = -c; k <= c; k += 16)
        {
            for(int l = -c; l <= c; l += 16)
            {
                if(loadingScreen != null)
                {
                    loadingScreen.setLoadingProgress((i++ * 100) / j);
                }
                theWorld.getBlockId(chunkcoordinates.posX + k, 64, chunkcoordinates.posZ + l);
                if(playerController.func_35643_e())
                {
                    continue;
                }
                while(theWorld.updatingLighting()) ;
            }

        }

        if(!playerController.func_35643_e())
        {
            if(loadingScreen != null)
            {
                loadingScreen.displayLoadingString("Simulating world for a bit");
            }
            char c1 = '\u07D0';
            theWorld.dropOldChucks();
        }
    }

    public void installResource(String s, File file)
    {
        int i = s.indexOf("/");
        String s1 = s.substring(0, i);
        s = s.substring(i + 1);
        if(s1.equalsIgnoreCase("sound"))
        {
            sndManager.addSound(s, file);
        } else
        if(s1.equalsIgnoreCase("newsound"))
        {
            sndManager.addSound(s, file);
        } else
        if(s1.equalsIgnoreCase("streaming"))
        {
            sndManager.addStreaming(s, file);
        } else
        if(s1.equalsIgnoreCase("music"))
        {
            sndManager.addMusic(s, file);
        } else
        if(s1.equalsIgnoreCase("newmusic"))
        {
            sndManager.addMusic(s, file);
        }
    }

    public OpenGlCapsChecker getOpenGlCapsChecker()
    {
        return glCapabilities;
    }

    public String debugInfoRenders()
    {
        return renderGlobal.getDebugInfoRenders();
    }

    public String func_6262_n()
    {
        return renderGlobal.getDebugInfoEntities();
    }

    public String func_21002_o()
    {
        return theWorld.func_21119_g();
    }

    public String debugInfoEntities()
    {
        return (new StringBuilder()).append("P: ").append(effectRenderer.getStatistics()).append(". T: ").append(theWorld.getDebugLoadedEntities()).toString();
    }

    public void respawn(boolean flag, int i)
    {
        if(!theWorld.multiplayerWorld && !theWorld.worldProvider.canRespawnHere())
        {
            usePortal();
        }
        ChunkCoordinates chunkcoordinates = null;
        ChunkCoordinates chunkcoordinates1 = null;
        boolean flag1 = true;
        if(thePlayer != null && !flag)
        {
            chunkcoordinates = thePlayer.getPlayerSpawnCoordinate();
            if(chunkcoordinates != null)
            {
                chunkcoordinates1 = EntityPlayer.verifyRespawnCoordinates(theWorld, chunkcoordinates);
                if(chunkcoordinates1 == null)
                {
                    thePlayer.addChatMessage("tile.bed.notValid");
                }
            }
        }
        if(chunkcoordinates1 == null)
        {
            chunkcoordinates1 = theWorld.getSpawnPoint();
            flag1 = false;
        }
        net.minecraft.src.IChunkProvider ichunkprovider = theWorld.getIChunkProvider();
        if(ichunkprovider instanceof ChunkProviderLoadOrGenerate)
        {
            ChunkProviderLoadOrGenerate chunkproviderloadorgenerate = (ChunkProviderLoadOrGenerate)ichunkprovider;
            chunkproviderloadorgenerate.setCurrentChunkOver(chunkcoordinates1.posX >> 4, chunkcoordinates1.posZ >> 4);
        }
        theWorld.setSpawnLocation();
        theWorld.updateEntityList();
        int j = 0;
        if(thePlayer != null)
        {
            j = thePlayer.entityId;
            theWorld.setEntityDead(thePlayer);
        }
        renderViewEntity = null;
        thePlayer = (EntityPlayerSP)playerController.createPlayer(theWorld);
        thePlayer.dimension = i;
        renderViewEntity = thePlayer;
        thePlayer.preparePlayerToSpawn();
        if(flag1)
        {
            thePlayer.setPlayerSpawnCoordinate(chunkcoordinates);
            thePlayer.setLocationAndAngles((float)chunkcoordinates1.posX + 0.5F, (float)chunkcoordinates1.posY + 0.1F, (float)chunkcoordinates1.posZ + 0.5F, 0.0F, 0.0F);
        }
        playerController.flipPlayer(thePlayer);
        theWorld.spawnPlayerWithLoadedChunks(thePlayer);
        thePlayer.movementInput = new MovementInputFromOptions(gameSettings);
        thePlayer.entityId = j;
        thePlayer.func_6420_o();
        playerController.func_6473_b(thePlayer);
        preloadWorld("Respawning");
        if(currentScreen instanceof GuiGameOver)
        {
            displayGuiScreen(null);
        }
    }

    public static void startMainThread1(String s, String s1)
    {
        startMainThread(s, s1, null);
    }

    public static void startMainThread(String s, String s1, String s2)
    {
        boolean flag = false;
        String s3 = s;
        Frame frame = new Frame("Minecraft");
        Canvas canvas = new Canvas();
        frame.setLayout(new BorderLayout());
        frame.add(canvas, "Center");
        canvas.setPreferredSize(new Dimension(854, 480));
        frame.pack();
        frame.setLocationRelativeTo(null);
        MinecraftImpl minecraftimpl = new MinecraftImpl(frame, canvas, null, 854, 480, flag, frame);
        Thread thread = new Thread(minecraftimpl, "Minecraft main thread");
        thread.setPriority(10);
        minecraftimpl.minecraftUri = "www.minecraft.net";
        if(s3 != null && s1 != null)
        {
            minecraftimpl.session = new Session(s3, s1);
        } else
        {
            minecraftimpl.session = new Session((new StringBuilder()).append("Player").append(System.currentTimeMillis() % 1000L).toString(), "");
        }
        if(s2 != null)
        {
            String as[] = s2.split(":");
            minecraftimpl.setServer(as[0], Integer.parseInt(as[1]));
        }
        frame.setVisible(true);
        frame.addWindowListener(new GameWindowListener(minecraftimpl, thread));
        thread.start();
    }

    public NetClientHandler getSendQueue()
    {
        if(thePlayer instanceof EntityClientPlayerMP)
        {
            return ((EntityClientPlayerMP)thePlayer).sendQueue;
        } else
        {
            return null;
        }
    }

    public static void main(String args[])
    {
        String s = null;
        String s1 = null;
        s = (new StringBuilder()).append("Player").append(System.currentTimeMillis() % 1000L).toString();
        if(args.length > 0)
        {
            s = args[0];
        }
        s1 = "-";
        if(args.length > 1)
        {
            s1 = args[1];
        }
        startMainThread1(s, s1);
    }

    public static boolean isGuiEnabled()
    {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion;
    }

    public static boolean isDebugInfoEnabled()
    {
        return theMinecraft != null && theMinecraft.gameSettings.showDebugInfo;
    }

    public boolean lineIsCommand(String s)
    {
        if(!s.startsWith("/"));
        return false;
    }

    private void clickMiddleMouseButton()
    {
        if(objectMouseOver != null)
        {
            int i = theWorld.getBlockId(objectMouseOver.blockX, objectMouseOver.blockY, objectMouseOver.blockZ);
            if(i == Block.grass.blockID)
            {
                i = Block.dirt.blockID;
            }
            if(i == Block.stairDouble.blockID)
            {
                i = Block.stairSingle.blockID;
            }
            if(i == Block.bedrock.blockID)
            {
                i = Block.stone.blockID;
            }
            thePlayer.inventory.setCurrentItem(i, playerController instanceof PlayerControllerTest);
        }
    }

    public static byte field_28006_b[] = new byte[0xa00000];
    private static Minecraft theMinecraft;
    public PlayerController playerController;
    private boolean fullscreen;
    private boolean hasCrashed;
    public int displayWidth;
    public int displayHeight;
    private OpenGlCapsChecker glCapabilities;
    private Timer timer;
    public World theWorld;
    public RenderGlobal renderGlobal;
    public EntityPlayerSP thePlayer;
    public EntityLiving renderViewEntity;
    public EffectRenderer effectRenderer;
    public Session session;
    public String minecraftUri;
    public Canvas mcCanvas;
    public boolean hideQuitButton;
    public volatile boolean isGamePaused;
    public RenderEngine renderEngine;
    public FontRenderer fontRenderer;
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    private ThreadDownloadResources downloadResourcesThread;
    private int ticksRan;
    private int leftClickCounter;
    private int tempDisplayWidth;
    private int tempDisplayHeight;
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public ModelBiped playerModelBiped;
    public MovingObjectPosition objectMouseOver;
    public GameSettings gameSettings;
    protected MinecraftApplet mcApplet;
    public SoundManager sndManager;
    public MouseHelper mouseHelper;
    public TexturePackList texturePackList;
    public File mcDataDir;
    private ISaveFormat saveLoader;
    public static long frameTimes[] = new long[512];
    public static long tickTimes[] = new long[512];
    public static int numRecordedFrameTimes = 0;
    public static long hasPaidCheckTime = 0L;
    private int field_35001_ab;
    public StatFileWriter statFileWriter;
    private String serverName;
    private int serverPort;
    private TextureWaterFX textureWaterFX;
    private TextureLavaFX textureLavaFX;
    private static File minecraftDir = null;
    public volatile boolean running;
    public String debug;
    boolean isTakingScreenshot;
    long prevFrameTime;
    public boolean inGameHasFocus;
    public boolean isRaining;
    long systemTime;
    private int joinPlayerCounter;

}
