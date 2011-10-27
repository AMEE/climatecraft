// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

package net.minecraft.src;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

import com.amee.minecraft.CarbonCounter;

// Referenced classes of package net.minecraft.src:
//            Gui, ScaledResolution, EntityRenderer, EntityPlayerSP, 
//            InventoryPlayer, GameSettings, ItemStack, Block, 
//            Potion, PlayerController, RenderEngine, FoodStats, 
//            Material, RenderHelper, FontRenderer, MathHelper, 
//            World, GuiChat, ChatLine, EntityClientPlayerMP, 
//            KeyBinding, NetClientHandler, GuiSavingLevelString, Tessellator, 
//            BlockPortal, RenderItem, StringTranslate

public class GuiIngame extends Gui
{

    public GuiIngame(Minecraft minecraft)
    {
        chatMessageList = new ArrayList();
        rand = new Random();
        field_933_a = null;
        updateCounter = 0;
        recordPlaying = "";
        recordPlayingUpFor = 0;
        recordIsPlaying = false;
        prevVignetteBrightness = 1.0F;
        mc = minecraft;
    }

    public void renderGameOverlay(float f, boolean flag, int i, int j)
    {
        ScaledResolution scaledresolution = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
        int k = scaledresolution.getScaledWidth();
        int l = scaledresolution.getScaledHeight();
        FontRenderer fontrenderer = mc.fontRenderer;
        mc.entityRenderer.func_905_b();
        GL11.glEnable(3042 /*GL_BLEND*/);
        if(Minecraft.isFancyGraphicsEnabled())
        {
            renderVignette(mc.thePlayer.getEntityBrightness(f), k, l);
        }
        ItemStack itemstack = mc.thePlayer.inventory.armorItemInSlot(3);
        if(!mc.gameSettings.thirdPersonView && itemstack != null && itemstack.itemID == Block.pumpkin.blockID)
        {
            renderPumpkinBlur(k, l);
        }
        if(!mc.thePlayer.func_35160_a(Potion.field_35684_k))
        {
            float f1 = mc.thePlayer.prevTimeInPortal + (mc.thePlayer.timeInPortal - mc.thePlayer.prevTimeInPortal) * f;
            if(f1 > 0.0F)
            {
                renderPortalOverlay(f1, k, l);
            }
        }
        if(!mc.playerController.func_35643_e())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/gui.png"));
            InventoryPlayer inventoryplayer = mc.thePlayer.inventory;
            zLevel = -90F;
            drawTexturedModalRect(k / 2 - 91, l - 22, 0, 0, 182, 22);
            drawTexturedModalRect((k / 2 - 91 - 1) + inventoryplayer.currentItem * 20, l - 22 - 1, 0, 22, 24, 22);
            GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/gui/icons.png"));
            GL11.glEnable(3042 /*GL_BLEND*/);
            GL11.glBlendFunc(775, 769);
            drawTexturedModalRect(k / 2 - 7, l / 2 - 7, 0, 0, 16, 16);
            GL11.glDisable(3042 /*GL_BLEND*/);
            boolean flag1 = (mc.thePlayer.heartsLife / 3) % 2 == 1;
            if(mc.thePlayer.heartsLife < 10)
            {
                flag1 = false;
            }
            int k1 = mc.thePlayer.health;
            int i3 = mc.thePlayer.prevHealth;
            rand.setSeed(updateCounter * 0x4c627);
            boolean flag3 = false;
            FoodStats foodstats = mc.thePlayer.func_35191_at();
            int j4 = foodstats.func_35765_a();
            int i5 = foodstats.func_35769_b();
            if(mc.playerController.shouldDrawHUD())
            {
                int l5 = k / 2 - 91;
                int k6 = k / 2 + 91;
                int i7 = mc.thePlayer.func_35193_as();
                if(i7 > 0)
                {
                    char c = '\266';
                    int j8 = (mc.thePlayer.field_35211_aX * (c + 1)) / mc.thePlayer.func_35193_as();
                    int i9 = (l - 32) + 3;
                    drawTexturedModalRect(l5, i9, 0, 64, c, 5);
                    if(j8 > 0)
                    {
                        drawTexturedModalRect(l5, i9, 0, 69, j8, 5);
                    }
                }
                int l7 = l - 39;
                int k8 = l7 - 10;
                int j9 = mc.thePlayer.getPlayerArmorValue();
                int k9 = -1;
                if(mc.thePlayer.func_35160_a(Potion.field_35681_l))
                {
                    k9 = updateCounter % 25;
                }
                for(int i10 = 0; i10 < 10; i10++)
                {
                    if(j9 > 0)
                    {
                        int l10 = l5 + i10 * 8;
                        if(i10 * 2 + 1 < j9)
                        {
                            drawTexturedModalRect(l10, k8, 34, 9, 9, 9);
                        }
                        if(i10 * 2 + 1 == j9)
                        {
                            drawTexturedModalRect(l10, k8, 25, 9, 9, 9);
                        }
                        if(i10 * 2 + 1 > j9)
                        {
                            drawTexturedModalRect(l10, k8, 16, 9, 9, 9);
                        }
                    }
                    int i11 = 16;
                    if(mc.thePlayer.func_35160_a(Potion.field_35689_u))
                    {
                        i11 += 36;
                    }
                    int l11 = 0;
                    if(flag1)
                    {
                        l11 = 1;
                    }
                    int k12 = l5 + i10 * 8;
                    int l12 = l7;
                    if(k1 <= 4)
                    {
                        l12 += rand.nextInt(2);
                    }
                    if(i10 == k9)
                    {
                        l12 -= 2;
                    }
                    drawTexturedModalRect(k12, l12, 16 + l11 * 9, 0, 9, 9);
                    if(flag1)
                    {
                        if(i10 * 2 + 1 < i3)
                        {
                            drawTexturedModalRect(k12, l12, i11 + 54, 0, 9, 9);
                        }
                        if(i10 * 2 + 1 == i3)
                        {
                            drawTexturedModalRect(k12, l12, i11 + 63, 0, 9, 9);
                        }
                    }
                    if(i10 * 2 + 1 < k1)
                    {
                        drawTexturedModalRect(k12, l12, i11 + 36, 0, 9, 9);
                    }
                    if(i10 * 2 + 1 == k1)
                    {
                        drawTexturedModalRect(k12, l12, i11 + 45, 0, 9, 9);
                    }
                }

                for(int j10 = 0; j10 < 10; j10++)
                {
                    int j11 = l7;
                    int i12 = 16;
                    byte byte4 = 0;
                    if(mc.thePlayer.func_35160_a(Potion.field_35691_s))
                    {
                        i12 += 36;
                        byte4 = 13;
                    }
                    if(mc.thePlayer.func_35191_at().func_35760_d() <= 0.0F && updateCounter % (j4 * 3 + 1) == 0)
                    {
                        j11 += rand.nextInt(3) - 1;
                    }
                    if(flag3)
                    {
                        byte4 = 1;
                    }
                    int i13 = k6 - j10 * 8 - 9;
                    drawTexturedModalRect(i13, j11, 16 + byte4 * 9, 27, 9, 9);
                    if(flag3)
                    {
                        if(j10 * 2 + 1 < i5)
                        {
                            drawTexturedModalRect(i13, j11, i12 + 54, 27, 9, 9);
                        }
                        if(j10 * 2 + 1 == i5)
                        {
                            drawTexturedModalRect(i13, j11, i12 + 63, 27, 9, 9);
                        }
                    }
                    if(j10 * 2 + 1 < j4)
                    {
                        drawTexturedModalRect(i13, j11, i12 + 36, 27, 9, 9);
                    }
                    if(j10 * 2 + 1 == j4)
                    {
                        drawTexturedModalRect(i13, j11, i12 + 45, 27, 9, 9);
                    }
                }

                if(mc.thePlayer.isInsideOfMaterial(Material.water))
                {
                    int k10 = (int)Math.ceil(((double)(mc.thePlayer.air - 2) * 10D) / 300D);
                    int k11 = (int)Math.ceil(((double)mc.thePlayer.air * 10D) / 300D) - k10;
                    for(int j12 = 0; j12 < k10 + k11; j12++)
                    {
                        if(j12 < k10)
                        {
                            drawTexturedModalRect(k6 - j12 * 8 - 9, k8, 16, 18, 9, 9);
                        } else
                        {
                            drawTexturedModalRect(k6 - j12 * 8 - 9, k8, 25, 18, 9, 9);
                        }
                    }

                }
            }
            GL11.glDisable(3042 /*GL_BLEND*/);
            GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
            GL11.glPushMatrix();
            GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GL11.glPopMatrix();
            for(int i6 = 0; i6 < 9; i6++)
            {
                int l6 = (k / 2 - 90) + i6 * 20 + 2;
                int j7 = l - 16 - 3;
                renderInventorySlot(i6, l6, j7, f);
            }

            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        }
        if(mc.thePlayer.func_22060_M() > 0)
        {
            GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
            GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
            int i1 = mc.thePlayer.func_22060_M();
            float f3 = (float)i1 / 100F;
            if(f3 > 1.0F)
            {
                f3 = 1.0F - (float)(i1 - 100) / 10F;
            }
            int i2 = (int)(220F * f3) << 24 | 0x101020;
            drawRect(0, 0, k, l, i2);
            GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
            GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        }
        if(mc.playerController.func_35642_f())
        {
            if(mc.thePlayer.field_35210_aY <= 0);
        }
        if(mc.gameSettings.showDebugInfo)
        {
            GL11.glPushMatrix();
            if(Minecraft.hasPaidCheckTime > 0L)
            {
                GL11.glTranslatef(0.0F, 32F, 0.0F);
            }
            fontrenderer.drawStringWithShadow((new StringBuilder()).append("Minecraft Beta 1.8.1 (").append(mc.debug).append(")").toString(), 2, 2, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.debugInfoRenders(), 2, 12, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.func_6262_n(), 2, 22, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.debugInfoEntities(), 2, 32, 0xffffff);
            fontrenderer.drawStringWithShadow(mc.func_21002_o(), 2, 42, 0xffffff);
            long l1 = Runtime.getRuntime().maxMemory();
            long l2 = Runtime.getRuntime().totalMemory();
            long l3 = Runtime.getRuntime().freeMemory();
            long l4 = l2 - l3;
            String s = (new StringBuilder()).append("Used memory: ").append((l4 * 100L) / l1).append("% (").append(l4 / 1024L / 1024L).append("MB) of ").append(l1 / 1024L / 1024L).append("MB").toString();
            drawString(fontrenderer, s, k - fontrenderer.getStringWidth(s) - 2, 2, 0xe0e0e0);
            s = (new StringBuilder()).append("Allocated memory: ").append((l2 * 100L) / l1).append("% (").append(l2 / 1024L / 1024L).append("MB)").toString();
            drawString(fontrenderer, s, k - fontrenderer.getStringWidth(s) - 2, 12, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("x: ").append(mc.thePlayer.posX).toString(), 2, 64, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("y: ").append(mc.thePlayer.posY).toString(), 2, 72, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("z: ").append(mc.thePlayer.posZ).toString(), 2, 80, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("f: ").append(MathHelper.floor_double((double)((mc.thePlayer.rotationYaw * 4F) / 360F) + 0.5D) & 3).toString(), 2, 88, 0xe0e0e0);
            drawString(fontrenderer, (new StringBuilder()).append("Seed: ").append(mc.theWorld.getRandomSeed()).toString(), 2, 104, 0xe0e0e0);
            GL11.glPopMatrix();
        } else
        {
            fontrenderer.drawStringWithShadow("CO2: " + com.amee.minecraft.Atmosphere.totalAsString(), 2, 2, 0xffffff);
        }
        if(recordPlayingUpFor > 0)
        {
            float f2 = (float)recordPlayingUpFor - f;
            int j1 = (int)((f2 * 256F) / 20F);
            if(j1 > 255)
            {
                j1 = 255;
            }
            if(j1 > 0)
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(k / 2, l - 48, 0.0F);
                GL11.glEnable(3042 /*GL_BLEND*/);
                GL11.glBlendFunc(770, 771);
                int j2 = 0xffffff;
                if(recordIsPlaying)
                {
                    j2 = Color.HSBtoRGB(f2 / 50F, 0.7F, 0.6F) & 0xffffff;
                }
                fontrenderer.drawString(recordPlaying, -fontrenderer.getStringWidth(recordPlaying) / 2, -4, j2 + (j1 << 24));
                GL11.glDisable(3042 /*GL_BLEND*/);
                GL11.glPopMatrix();
            }
        }
        byte byte0 = 10;
        boolean flag2 = false;
        if(mc.currentScreen instanceof GuiChat)
        {
            byte0 = 20;
            flag2 = true;
        }
        GL11.glEnable(3042 /*GL_BLEND*/);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, l - 48, 0.0F);
        for(int k2 = 0; k2 < chatMessageList.size() && k2 < byte0; k2++)
        {
            if(((ChatLine)chatMessageList.get(k2)).updateCounter >= 200 && !flag2)
            {
                continue;
            }
            double d = (double)((ChatLine)chatMessageList.get(k2)).updateCounter / 200D;
            d = 1.0D - d;
            d *= 10D;
            if(d < 0.0D)
            {
                d = 0.0D;
            }
            if(d > 1.0D)
            {
                d = 1.0D;
            }
            d *= d;
            int k3 = (int)(255D * d);
            if(flag2)
            {
                k3 = 255;
            }
            if(k3 > 0)
            {
                byte byte1 = 2;
                int j5 = -k2 * 9;
                String s1 = ((ChatLine)chatMessageList.get(k2)).message;
                drawRect(byte1, j5 - 1, byte1 + 320, j5 + 8, k3 / 2 << 24);
                GL11.glEnable(3042 /*GL_BLEND*/);
                fontrenderer.drawStringWithShadow(s1, byte1, j5, 0xffffff + (k3 << 24));
            }
        }

        GL11.glPopMatrix();
        if((mc.thePlayer instanceof EntityClientPlayerMP) && mc.gameSettings.field_35384_x.field_35965_e)
        {
            NetClientHandler netclienthandler = ((EntityClientPlayerMP)mc.thePlayer).sendQueue;
            java.util.List list = netclienthandler.field_35786_c;
            int j3 = netclienthandler.field_35785_d;
            int i4 = j3;
            int k4 = 1;
            for(; i4 > 20; i4 = ((j3 + k4) - 1) / k4)
            {
                k4++;
            }

            int k5 = 300 / k4;
            if(k5 > 150)
            {
                k5 = 150;
            }
            int j6 = (k - k4 * k5) / 2;
            byte byte2 = 10;
            drawRect(j6 - 1, byte2 - 1, j6 + k5 * k4, byte2 + 9 * i4, 0x80000000);
            for(int k7 = 0; k7 < j3; k7++)
            {
                int i8 = j6 + (k7 % k4) * k5;
                int l8 = byte2 + (k7 / k4) * 9;
                drawRect(i8, l8, (i8 + k5) - 1, l8 + 8, 0x20ffffff);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
                if(k7 >= list.size())
                {
                    continue;
                }
                GuiSavingLevelString guisavinglevelstring = (GuiSavingLevelString)list.get(k7);
                fontrenderer.drawStringWithShadow(guisavinglevelstring.field_35624_a, i8, l8, 0xffffff);
                mc.renderEngine.bindTexture(mc.renderEngine.getTexture("/gui/icons.png"));
                int l9 = 0;
                byte byte3 = 0;
                l9 = 0;
                byte3 = 0;
                if(guisavinglevelstring.field_35623_b < 0)
                {
                    byte3 = 5;
                } else
                if(guisavinglevelstring.field_35623_b < 150)
                {
                    byte3 = 0;
                } else
                if(guisavinglevelstring.field_35623_b < 300)
                {
                    byte3 = 1;
                } else
                if(guisavinglevelstring.field_35623_b < 600)
                {
                    byte3 = 2;
                } else
                if(guisavinglevelstring.field_35623_b < 1000)
                {
                    byte3 = 3;
                } else
                {
                    byte3 = 4;
                }
                zLevel += 100F;
                drawTexturedModalRect((i8 + k5) - 12, l8, 0 + l9 * 10, 176 + byte3 * 8, 10, 8);
                zLevel -= 100F;
            }

        }
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(2896 /*GL_LIGHTING*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
    }

    private void renderPumpkinBlur(int i, int j)
    {
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("%blur%/misc/pumpkinblur.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, j, -90D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(i, j, -90D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(i, 0.0D, -90D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderVignette(float f, int i, int j)
    {
        f = 1.0F - f;
        if(f < 0.0F)
        {
            f = 0.0F;
        }
        if(f > 1.0F)
        {
            f = 1.0F;
        }
        prevVignetteBrightness += (double)(f - prevVignetteBrightness) * 0.01D;
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(0, 769);
        GL11.glColor4f(prevVignetteBrightness, prevVignetteBrightness, prevVignetteBrightness, 1.0F);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("%blur%/misc/vignette.png"));
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, j, -90D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(i, j, -90D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(i, 0.0D, -90D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBlendFunc(770, 771);
    }

    private void renderPortalOverlay(float f, int i, int j)
    {
        if(f < 1.0F)
        {
            f *= f;
            f *= f;
            f = f * 0.8F + 0.2F;
        }
        GL11.glDisable(3008 /*GL_ALPHA_TEST*/);
        GL11.glDisable(2929 /*GL_DEPTH_TEST*/);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, f);
        GL11.glBindTexture(3553 /*GL_TEXTURE_2D*/, mc.renderEngine.getTexture("/terrain.png"));
        float f1 = (float)(Block.portal.blockIndexInTexture % 16) / 16F;
        float f2 = (float)(Block.portal.blockIndexInTexture / 16) / 16F;
        float f3 = (float)(Block.portal.blockIndexInTexture % 16 + 1) / 16F;
        float f4 = (float)(Block.portal.blockIndexInTexture / 16 + 1) / 16F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, j, -90D, f1, f4);
        tessellator.addVertexWithUV(i, j, -90D, f3, f4);
        tessellator.addVertexWithUV(i, 0.0D, -90D, f3, f2);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90D, f1, f2);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(2929 /*GL_DEPTH_TEST*/);
        GL11.glEnable(3008 /*GL_ALPHA_TEST*/);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderInventorySlot(int i, int j, int k, float f)
    {
        ItemStack itemstack = mc.thePlayer.inventory.mainInventory[i];
        if(itemstack == null)
        {
            return;
        }
        float f1 = (float)itemstack.animationsToGo - f;
        if(f1 > 0.0F)
        {
            GL11.glPushMatrix();
            float f2 = 1.0F + f1 / 5F;
            GL11.glTranslatef(j + 8, k + 12, 0.0F);
            GL11.glScalef(1.0F / f2, (f2 + 1.0F) / 2.0F, 1.0F);
            GL11.glTranslatef(-(j + 8), -(k + 12), 0.0F);
        }
        itemRenderer.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, j, k);
        if(f1 > 0.0F)
        {
            GL11.glPopMatrix();
        }
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, mc.renderEngine, itemstack, j, k);
    }

    public void updateTick()
    {
        if(recordPlayingUpFor > 0)
        {
            recordPlayingUpFor--;
        }
        updateCounter++;
        for(int i = 0; i < chatMessageList.size(); i++)
        {
            ((ChatLine)chatMessageList.get(i)).updateCounter++;
        }

    }

    public void clearChatMessages()
    {
        chatMessageList.clear();
    }

    public void addChatMessage(String s)
    {
        int i;
        for(; mc.fontRenderer.getStringWidth(s) > 320; s = s.substring(i))
        {
            for(i = 1; i < s.length() && mc.fontRenderer.getStringWidth(s.substring(0, i + 1)) <= 320; i++) { }
            addChatMessage(s.substring(0, i));
        }

        chatMessageList.add(0, new ChatLine(s));
        for(; chatMessageList.size() > 50; chatMessageList.remove(chatMessageList.size() - 1)) { }
    }

    public void setRecordPlayingMessage(String s)
    {
        recordPlaying = (new StringBuilder()).append("Now playing: ").append(s).toString();
        recordPlayingUpFor = 60;
        recordIsPlaying = true;
    }

    public void addChatMessageTranslate(String s)
    {
        StringTranslate stringtranslate = StringTranslate.getInstance();
        String s1 = stringtranslate.translateKey(s);
        addChatMessage(s1);
    }

    private static RenderItem itemRenderer = new RenderItem();
    private java.util.List chatMessageList;
    private Random rand;
    private Minecraft mc;
    public String field_933_a;
    private int updateCounter;
    private String recordPlaying;
    private int recordPlayingUpFor;
    private boolean recordIsPlaying;
    public float damageGuiPartialTime;
    float prevVignetteBrightness;

}
