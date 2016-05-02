/*
 * Copyright (c) 2012, Gerrit Grunwald
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * The names of its contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.chrisali.javaflightsim.instrumentpanel.gauges;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.ease.Spline;

import eu.hansolo.steelseries.gauges.AbstractGauge;
import eu.hansolo.steelseries.gauges.AbstractRadial;
import eu.hansolo.steelseries.tools.FrameDesign;
import eu.hansolo.steelseries.tools.LcdColor;
import eu.hansolo.steelseries.tools.Section;

public class VerticalSpeed extends AbstractRadial {

	private static final long serialVersionUID = 1L;
	
    private double visibleValue = 0;
    private double angleStep;
    private final Point2D CENTER = new Point2D.Double();
    private BufferedImage bImage;
    private BufferedImage fImage;
    private BufferedImage pointerImage;
    private BufferedImage disabledImage;
    private Timeline timeline = new Timeline(this);
    private final FontRenderContext RENDER_CONTEXT = new FontRenderContext(null, true, true);
    private TextLayout unitLayout;
    private final Rectangle2D UNIT_BOUNDARY = new Rectangle2D.Double();
    private TextLayout valueLayout;
    private final Rectangle2D VALUE_BOUNDARY = new Rectangle2D.Double();
    private final Rectangle2D LCD = new Rectangle2D.Double();

    public VerticalSpeed() {
        super();
        setLcdColor(LcdColor.BLACK_LCD);
        setValueCoupled(false);
        setLcdDecimals(0);
        setLcdVisible(false);
        setUnitString("x1000 FT/MIN");
		setTitle(this.toString());
        calcAngleStep();
        init(getInnerBounds().width, getInnerBounds().height);
    }

    @Override
    public final AbstractGauge init(int WIDTH, int HEIGHT) {
        final int GAUGE_WIDTH = isFrameVisible() ? WIDTH : getGaugeBounds().width;
        final int GAUGE_HEIGHT = isFrameVisible() ? HEIGHT : getGaugeBounds().height;

        if (GAUGE_WIDTH <= 1 || GAUGE_HEIGHT <= 1) {
            return this;
        }

        if (!isFrameVisible()) {
            setFramelessOffset(-getGaugeBounds().width * 0.0841121495, -getGaugeBounds().width * 0.0841121495);
        } else {
            setFramelessOffset(getGaugeBounds().x, getGaugeBounds().y);
        }
        
        if (isDigitalFont()) {
            setLcdValueFont(getModel().getDigitalBaseFont().deriveFont(0.7f * GAUGE_WIDTH * 0.15f));
        } else {
            setLcdValueFont(getModel().getStandardBaseFont().deriveFont(0.625f * GAUGE_WIDTH * 0.15f));
        }

        if (isCustomLcdUnitFontEnabled()) {
            setLcdUnitFont(getCustomLcdUnitFont().deriveFont(0.25f * GAUGE_WIDTH * 0.15f));
        } else {
            setLcdUnitFont(getModel().getStandardBaseFont().deriveFont(0.25f * GAUGE_WIDTH * 0.15f));
        }

        setLcdInfoFont(getModel().getStandardInfoFont().deriveFont(0.15f * GAUGE_WIDTH * 0.15f));

        // Create Background Image
        if (bImage != null) {
            bImage.flush();
        }
        bImage = UTIL.createImage(GAUGE_WIDTH, GAUGE_WIDTH, Transparency.TRANSLUCENT);

        // Create Foreground Image
        if (fImage != null) {
            fImage.flush();
        }
        fImage = UTIL.createImage(GAUGE_WIDTH, GAUGE_WIDTH, Transparency.TRANSLUCENT);

        if (isFrameVisible()) {
        	FRAME_FACTORY.createRadialFrame(GAUGE_WIDTH, FrameDesign.TILTED_BLACK, getCustomFrameDesign(), getFrameEffect(), bImage);
        }

        if (isBackgroundVisible()) {
            create_BACKGROUND_Image(GAUGE_WIDTH, "", "", bImage);
        }

        create_TITLE_Image(GAUGE_WIDTH, getTitle(), getUnitString(), bImage);

        create_TICKMARKS_Image(GAUGE_WIDTH, 0, 0, 0, 0, 0, 0, 0, true, true, null, bImage);
        
        if (isLcdVisible()) {
        	createLcdImage(new Rectangle2D.Double(((getGaugeBounds().width - GAUGE_WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55), (GAUGE_WIDTH * 0.4), (GAUGE_WIDTH * 0.15)), getLcdColor(), getCustomLcdBackground(), bImage);
        	LCD.setRect(((getGaugeBounds().width - GAUGE_WIDTH * 0.4) / 2.0), (getGaugeBounds().height * 0.55), GAUGE_WIDTH * 0.4, GAUGE_WIDTH * 0.15);
        }
        
        if (pointerImage != null) {
            pointerImage.flush();
        }
        pointerImage = create_POINTER_Image(GAUGE_WIDTH);

        if (isForegroundVisible()) {
        	FOREGROUND_FACTORY.createRadialForeground(GAUGE_WIDTH, false, getForegroundType(), fImage);
        }

        if (disabledImage != null) {
            disabledImage.flush();
        }
        disabledImage = create_DISABLED_Image(GAUGE_WIDTH);

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isInitialized()) {
            return;
        }

        final Graphics2D G2 = (Graphics2D) g.create();

        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Translate the coordinate system related to insets
        G2.translate(getFramelessOffset().getX(), getFramelessOffset().getY());
        
        final AffineTransform OLD_TRANSFORM = G2.getTransform();

        CENTER.setLocation(getGaugeBounds().getCenterX() - getInsets().left, getGaugeBounds().getCenterX() - getInsets().right);

        // Draw combined background image
        G2.drawImage(bImage, 0, 0, null);

        // Draw LCD display
        if (isLcdVisible()) {
            if (getLcdColor() == LcdColor.CUSTOM) {
                G2.setColor(getCustomLcdForeground());
            } else {
                G2.setColor(getLcdColor().TEXT_COLOR);
            }
            G2.setFont(getLcdUnitFont());
            
            final double UNIT_STRING_WIDTH;
            
            if (isLcdUnitStringVisible()) {
                unitLayout = new TextLayout(getLcdUnitString(), G2.getFont(), RENDER_CONTEXT);
                UNIT_BOUNDARY.setFrame(unitLayout.getBounds());
                G2.drawString(getLcdUnitString(), (int) (LCD.getX() + (LCD.getWidth() - UNIT_BOUNDARY.getWidth()) - LCD.getWidth() * 0.03), (int) (LCD.getY() + LCD.getHeight() * 0.76f));
                UNIT_STRING_WIDTH = UNIT_BOUNDARY.getWidth();
            } else {
                UNIT_STRING_WIDTH = 0;
            }
            G2.setFont(getLcdValueFont());
            
            valueLayout = new TextLayout(formatLcdValue(getLcdValue()), G2.getFont(), RENDER_CONTEXT);
            VALUE_BOUNDARY.setFrame(valueLayout.getBounds());
            G2.drawString(formatLcdValue(getLcdValue()), (int) (LCD.getX() + (LCD.getWidth() - UNIT_STRING_WIDTH - VALUE_BOUNDARY.getWidth()) - LCD.getWidth() * 0.09), (int) (LCD.getY() + LCD.getHeight() * 0.76f));
        }

        // Draw the pointer
        G2.rotate(visibleValue * angleStep - Math.PI/2, CENTER.getX(), CENTER.getY());
        G2.drawImage(pointerImage, 0, 0, null);
        G2.setTransform(OLD_TRANSFORM);

        // Draw combined foreground image
        G2.drawImage(fImage, 0, 0, null);

        if (!isEnabled()) {
            G2.drawImage(disabledImage, 0, 0, null);
        }

        // Translate the coordinate system back to original
        G2.translate(-getInnerBounds().x, -getInnerBounds().y);

        G2.dispose();
    }

    @Override
    public void setValue(final double VALUE) {
        if (isEnabled()) {
        	if (Math.abs(VALUE) <= 3000) {
        		setLcdValue(VALUE);   	
        		this.visibleValue = VALUE;
        	}

            fireStateChanged();
            repaint(getInnerBounds());
        }
    }

    @Override
    public void setValueAnimated(double value) {
        if (isEnabled()) {
            if (timeline.getState() == Timeline.TimelineState.PLAYING_FORWARD || timeline.getState() == Timeline.TimelineState.PLAYING_REVERSE) {
                timeline.abort();
            }
            timeline = new Timeline(this);
            timeline.addPropertyToInterpolate("value", getValue(), value);
            timeline.setEase(new Spline(0.5f));

            timeline.setDuration(1000);
            timeline.play();
        }
    }

    @Override
    public double getMinValue() {
        return 0.0;
    }

    @Override
    public double getMaxValue() {
        return 10.0;
    }
    
    /**
     * Converts gauge value from ft/min to rotation in radians
     */
    private void calcAngleStep() {
        angleStep = Math.PI / (300*getMaxValue());
    }

    @Override
    public Paint createCustomLcdBackgroundPaint(final Color[] LCD_COLORS) {
        final Point2D FOREGROUND_START = new Point2D.Double(0.0, LCD.getMinY() + 1.0);
        final Point2D FOREGROUND_STOP = new Point2D.Double(0.0, LCD.getMaxY() - 1);
        if (FOREGROUND_START.equals(FOREGROUND_STOP)) {
            FOREGROUND_STOP.setLocation(0.0, FOREGROUND_START.getY() + 1);
        }

        final float[] FOREGROUND_FRACTIONS = {
            0.0f,
            0.03f,
            0.49f,
            0.5f,
            1.0f
        };

        final Color[] FOREGROUND_COLORS = {
            LCD_COLORS[0],
            LCD_COLORS[1],
            LCD_COLORS[2],
            LCD_COLORS[3],
            LCD_COLORS[4]
        };

        return new LinearGradientPaint(FOREGROUND_START, FOREGROUND_STOP, FOREGROUND_FRACTIONS, FOREGROUND_COLORS);
    }

    @Override
    public Point2D getCenter() {
        return new Point2D.Double(bImage.getWidth() / 2.0 + getInnerBounds().x, bImage.getHeight() / 2.0 + getInnerBounds().y);
    }

    @Override
    public Rectangle2D getBounds2D() {
        return new Rectangle2D.Double(bImage.getMinX(), bImage.getMinY(), bImage.getWidth(), bImage.getHeight());
    }

    @Override
    public Rectangle getLcdBounds() {
        return LCD.getBounds();
    }

    protected BufferedImage create_TICKMARKS_Image(final int WIDTH, final double FREE_AREA_ANGLE,
                                                                  final double OFFSET, final double MIN_VALUE,
                                                                  final double MAX_VALUE, final double ANGLE_STEP,
                                                                  final int TICK_LABEL_PERIOD,
                                                                  final int SCALE_DIVIDER_POWER,
                                                                  final boolean DRAW_TICKS,
                                                                  final boolean DRAW_TICK_LABELS,
                                                                  ArrayList<Section> tickmarkSections,
                                                                  BufferedImage image) {
        if (WIDTH <= 0) {
            return null;
        }
        if (image == null) {
            image = UTIL.createImage(WIDTH, (int) (1.0 * WIDTH), Transparency.TRANSLUCENT);
        }
        final Graphics2D G2 = image.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        G2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        final int IMAGE_WIDTH = image.getWidth();
        final int IMAGE_HEIGHT = image.getHeight();

        final Font STD_FONT = new Font("Verdana", 0, (int) (0.08 * WIDTH));
        final Font SMALL_FONT = new Font("Verdana", 0, (int) (0.04 * WIDTH));
        final BasicStroke THICK_STROKE = new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final BasicStroke MEDIUM_STROKE = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final BasicStroke THIN_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
        final int TEXT_DISTANCE = (int) (0.1 * WIDTH);
        final int MIN_LENGTH = (int) (0.03 * WIDTH);
        final int MED_LENGTH = (int) (0.04 * WIDTH);
        final int MAX_LENGTH = (int) (0.05 * WIDTH);

        // Create the ticks itself
        final float RADIUS = IMAGE_WIDTH * 0.38f;
        final Point2D GAUGE_CENTER = new Point2D.Double(IMAGE_WIDTH / 2.0f, IMAGE_HEIGHT / 2.0f);

        // Draw ticks
        final Point2D INNER_POINT = new Point2D.Double(0, 0);
        final Point2D OUTER_POINT = new Point2D.Double(0, 0);
        final Point2D TEXT_POINT = new Point2D.Double(0, 0);
        final Line2D TICK_LINE = new Line2D.Double(0, 0, 1, 1);

        int counter = 0;
        float valueCounter = 90;
        boolean countUp = false;
        float valueStep = 1;

        G2.setFont(STD_FONT);

        double sinValue = 0;
        double cosValue = 0;

        final double STEP = (2.0 * Math.PI) / (180.0);

        for (double alpha = (2.0 * Math.PI); alpha >= STEP; alpha -= STEP) {
            G2.setStroke(THIN_STROKE);
            G2.setColor(getTickmarkColor());
            sinValue = Math.sin(alpha - 3*Math.PI / 2);
            cosValue = Math.cos(alpha - 3*Math.PI / 2);

            // Different tickmark every 3 units
            if (counter % 3 == 0 && alpha <= 1.33*Math.PI && alpha >= 0.66*Math.PI) {
                G2.setStroke(THIN_STROKE);
                INNER_POINT.setLocation(GAUGE_CENTER.getX() + (RADIUS - MIN_LENGTH) * sinValue, GAUGE_CENTER.getY() + (RADIUS - MIN_LENGTH) * cosValue);
                OUTER_POINT.setLocation(GAUGE_CENTER.getX() + RADIUS * sinValue, GAUGE_CENTER.getY() + RADIUS * cosValue);
                
                TICK_LINE.setLine(INNER_POINT, OUTER_POINT);
                G2.draw(TICK_LINE);        
            }
            // Different tickmark every 30 units plus text
            if (counter % 15 == 0) {
                G2.setStroke(MEDIUM_STROKE);
                INNER_POINT.setLocation(GAUGE_CENTER.getX() + (RADIUS - MED_LENGTH) * sinValue, GAUGE_CENTER.getY() + (RADIUS - MED_LENGTH) * cosValue);
                OUTER_POINT.setLocation(GAUGE_CENTER.getX() + RADIUS * sinValue, GAUGE_CENTER.getY() + RADIUS * cosValue);
                
                TICK_LINE.setLine(INNER_POINT, OUTER_POINT);
                G2.draw(TICK_LINE);        
            }
            // Different tickmark every 30 units plus text
            if (counter == 30 || counter == 0) {
                G2.setStroke(THICK_STROKE);
                INNER_POINT.setLocation(GAUGE_CENTER.getX() + (RADIUS - MAX_LENGTH) * sinValue, GAUGE_CENTER.getY() + (RADIUS - MAX_LENGTH) * cosValue);
                OUTER_POINT.setLocation(GAUGE_CENTER.getX() + RADIUS * sinValue, GAUGE_CENTER.getY() + RADIUS * cosValue);

                // Draw outer text
                TEXT_POINT.setLocation(GAUGE_CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);
                G2.setFont(STD_FONT);

                G2.fill(UTIL.rotateTextAroundCenter(G2, String.valueOf((int) valueCounter/30), (int) TEXT_POINT.getX(), (int) TEXT_POINT.getY(), 0));

                counter = 0;
                
                TICK_LINE.setLine(INNER_POINT, OUTER_POINT);
                G2.draw(TICK_LINE);
            }
            if (counter == 15 && alpha <= Math.PI && alpha >= 0.66*Math.PI) {
            	TEXT_POINT.setLocation(GAUGE_CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);
                G2.setFont(SMALL_FONT);
                
            	G2.fill(UTIL.rotateTextAroundCenter(G2, ".5 UP", (int) TEXT_POINT.getX(), (int) TEXT_POINT.getY(), 0));
            }
            if (counter == 15 && alpha <= 1.33*Math.PI && alpha >= Math.PI) {
            	TEXT_POINT.setLocation(GAUGE_CENTER.getX() + (RADIUS - TEXT_DISTANCE) * sinValue, GAUGE_CENTER.getY() + (RADIUS - TEXT_DISTANCE) * cosValue);
                G2.setFont(SMALL_FONT);
                
            	G2.fill(UTIL.rotateTextAroundCenter(G2, ".5 DN", (int) TEXT_POINT.getX(), (int) TEXT_POINT.getY(), 0));
            }

            counter++;
            if (valueCounter == 0) {
                countUp = true;
            }
            if (valueCounter == 180) {
                countUp = false;
            }
            if (countUp) {
                valueCounter += valueStep;
            } else {
                valueCounter -= valueStep;
            }
        }

        G2.dispose();

        return image;
    }
    
    @Override
    protected BufferedImage create_POINTER_Image(final int WIDTH) {
        if (WIDTH <= 0) {
            return null;
        }

        final BufferedImage IMAGE = UTIL.createImage(WIDTH, (int) (1.0 * WIDTH), Transparency.TRANSLUCENT);
        final Graphics2D G2 = IMAGE.createGraphics();
        G2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        G2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        G2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        G2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        final int IMAGE_WIDTH = IMAGE.getWidth();
        final int IMAGE_HEIGHT = IMAGE.getHeight();

        final GeneralPath POINTER = new GeneralPath();
        POINTER.setWindingRule(Path2D.WIND_EVEN_ODD);
        POINTER.moveTo(IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.4719626168224299);
        POINTER.curveTo(IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.4719626168224299, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.4672897196261682, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.4672897196261682);
        POINTER.lineTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.20093457943925233);
        POINTER.lineTo(IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.16822429906542055);
        POINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.20093457943925233);
        POINTER.lineTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.4672897196261682);
        POINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.4672897196261682, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.4719626168224299, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.4719626168224299);
        POINTER.curveTo(IMAGE_WIDTH * 0.4719626168224299, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.4672897196261682, IMAGE_HEIGHT * 0.49065420560747663, IMAGE_WIDTH * 0.4672897196261682, IMAGE_HEIGHT * 0.5);
        POINTER.curveTo(IMAGE_WIDTH * 0.4672897196261682, IMAGE_HEIGHT * 0.514018691588785, IMAGE_WIDTH * 0.4766355140186916, IMAGE_HEIGHT * 0.5280373831775701, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5327102803738317);
        POINTER.curveTo(IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5327102803738317, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5794392523364486, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.5887850467289719);
        POINTER.curveTo(IMAGE_WIDTH * 0.48598130841121495, IMAGE_HEIGHT * 0.5934579439252337, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.5981308411214953, IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.6074766355140186);
        POINTER.curveTo(IMAGE_WIDTH * 0.48130841121495327, IMAGE_HEIGHT * 0.616822429906542, IMAGE_WIDTH * 0.49065420560747663, IMAGE_HEIGHT * 0.6261682242990654, IMAGE_WIDTH * 0.5, IMAGE_HEIGHT * 0.6261682242990654);
        POINTER.curveTo(IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.6261682242990654, IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.616822429906542, IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.6074766355140186);
        POINTER.curveTo(IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.5981308411214953, IMAGE_WIDTH * 0.514018691588785, IMAGE_HEIGHT * 0.5934579439252337, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.5887850467289719);
        POINTER.curveTo(IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.5794392523364486, IMAGE_WIDTH * 0.5046728971962616, IMAGE_HEIGHT * 0.5327102803738317, IMAGE_WIDTH * 0.5093457943925234, IMAGE_HEIGHT * 0.5327102803738317);
        POINTER.curveTo(IMAGE_WIDTH * 0.5233644859813084, IMAGE_HEIGHT * 0.5280373831775701, IMAGE_WIDTH * 0.5327102803738317, IMAGE_HEIGHT * 0.514018691588785, IMAGE_WIDTH * 0.5327102803738317, IMAGE_HEIGHT * 0.5);
        POINTER.curveTo(IMAGE_WIDTH * 0.5327102803738317, IMAGE_HEIGHT * 0.49065420560747663, IMAGE_WIDTH * 0.5280373831775701, IMAGE_HEIGHT * 0.48130841121495327, IMAGE_WIDTH * 0.5186915887850467, IMAGE_HEIGHT * 0.4719626168224299);
        POINTER.closePath();
        final Point2D POINTER_START = new Point2D.Double(0, POINTER.getBounds2D().getMinY());
        final Point2D POINTER_STOP = new Point2D.Double(0, POINTER.getBounds2D().getMaxY());
        final float[] POINTER_FRACTIONS = {
            0.0f,
            0.59f,
            0.5901f,
            0.60f,
            1.0f
        };
        final Color[] POINTER_COLORS = {
            new Color(255, 255, 255, 255),
            new Color(255, 255, 255, 255),
            new Color(32, 32, 32, 255),
            new Color(32, 32, 32, 255),
            new Color(32, 32, 32, 255)
        };
        final LinearGradientPaint POINTER_GRADIENT = new LinearGradientPaint(POINTER_START, POINTER_STOP, POINTER_FRACTIONS, POINTER_COLORS);
        G2.setPaint(POINTER_GRADIENT);
        G2.fill(POINTER);

        G2.dispose();

        return IMAGE;
    }

    @Override
    public String toString() {
        return "VERTICAL SPEED";
    }
}